package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.rabbitmq.MQSender;
import com.onyx.miaosha.rabbitmq.MiaoshaMessage;
import com.onyx.miaosha.redis.GoodsKey;
import com.onyx.miaosha.redis.MiaoshaKey;
import com.onyx.miaosha.redis.OrderKey;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.GoodsService;
import com.onyx.miaosha.service.MiaoshaService;
import com.onyx.miaosha.utils.MD5Util;
import com.onyx.miaosha.utils.UUIDUtil;
import com.onyx.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;
    //尽量不要在MiaoShaService中引入orderDao,这样结构不清晰,要么就引入orderService....

    private Map<Long,Boolean> localOverMap= new HashMap<Long, Boolean>();

    /**
     * 优化之前的TPS 是421...  1000个并发跑了10次
     * 5000个用户...
     * 数据库的秒杀数量变成了-44.....实际上就20上商品,创建了64个订单出来....程序出错了.
     *
     * 优化后,修正了数量的错误后,QPS是159.最后的库存变成了0
     *
     * 全部优化之后,QPS:1590  没有卖超一个...很好...效果杠杠的
     *
     *
     * @param user
     * @param id
     * @return
     * GET  是幂等的,调用多少次都是一样的结果,而且不对服务端产生影响
     * POST是提交数据
     *
     *
     * 接口优化思路:  减少数据库访问
     * 1.系统初始化,把商品库存数量加载到Redis
     * 2.收到请求,Redis预减库存,库存不足,直接返回,否则进入3(异步下单)
     * 3.请求入队,立即返回排队中
     * 4.请求出队,生成订单,减少库存
     * 5.客户端轮训,是否秒杀成功
     *
     * nginx 对应用的横向扩展:
     * 修改nginx.conf文件:
     *
     * upstream server_pool_miaosha{
     *     server localhost:9000    weight=1 max_fails=2  fail_timeout=30s;
     *     server otherserver:9090  weight=1 max_fails=2  fail_timeout=30s;
     * }
     *
     * server{
     *     listen  80;
     *     server_name localhost  10.110.3.62;
     *
     *     location /{
     *         proxy_pass http://server_pool_miaosha;
     *     }
     * }
     *
     * nginx 的缓存....压缩
     *
     * 流量再大....
     * LVS  一般到不了这个量级(千万,亿)
     *
     *
     * 安全优化:
     * 1.秒杀地址的隐藏,地址都是从服务器获取的
     *  思路:秒杀开始之前,先去请求接口获取秒杀地址
     *  1.接口改造,带上PathVariable参数
     *  2.添加生成的地址的接口
     *  3.秒杀收到请求,先验证PathVariable
     *
     * 2.数学公式验证码
     *  思路:点击秒杀之前,先输入验证码,分散用户请求
     *  1.添加生成验证码的接口
     *  2.在获取秒杀路径的时候,验证验证码
     *  3.ScriptEngine使用
     *
     * 3.接口的限流防刷
     *
     *
     */
    @RequestMapping(value = "{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> miaoSha(MiaoshaUser user,@RequestParam("goodsId") long id,
                                  @PathVariable("path")String path){
        //验证一下path
        boolean check=miaoshaService.checkPath(user,id,path);
        if(!check){
            return Result.fail(CodeMsg.REQUEST_ILLEGAL);
        }

        if(user==null){
            return Result.fail(CodeMsg.NO_USER);
        }
        //商品的数量的判断...
        Boolean aBoolean = localOverMap.get(id);
        if(aBoolean){
            return Result.fail(CodeMsg.COUNT_EMPTY);
        }
        //预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, id + "");
        if(stock<0){
            localOverMap.put(id,true);
            return Result.fail(CodeMsg.COUNT_EMPTY);
        }
        //发送消息
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(id);
        message.setUser(user);
        mqSender.sendMiaoshaMessage(message);
        //0 代表排队中,要改前端的代码了
        return Result.success(0);



        /*//判断库存
        GoodsVo goodsVo = goodsService.getById(id);// 10个  req1  req2
        if(goodsVo.getStockCount()<1){
            return Result.fail(CodeMsg.COUNT_EMPTY);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), id);
        if(order!=null){
            return Result.fail(CodeMsg.MIAOSHA_FAIL);
        }
        //开始秒杀,减库存
        //下订单
        //写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        return Result.success(orderInfo);*/

    }

    /**
     * 系统初始化,把商品数量加载到系统里面去
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if(goodsVos==null){
            return;
        }
        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock,goodsVo.getId()+"",goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }


    /**
     * 为了防止一个用户秒杀了两个,在miaosha_order的表中建立一个userId,goodId的唯一索引
     * 出错回滚
     */


    /**
     * 轮训,查询秒杀的结果
     * 成功返回订单id,
     * 库存不足-1
     * 还在排队中0
     */
    @RequestMapping(value = "result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Object>  miaoshaResult(MiaoshaUser user,@RequestParam("goodsId")long goodsId){
        if(user==null){
            return Result.fail(CodeMsg.NO_USER);
        }
        long orderId=miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(orderId);
    }


    /**
     * 重置信息的接口
     * @return
     */
    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset() {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(20);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 20);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getOrderByUserIdGoodsId);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }


    @RequestMapping(value = "path",method = RequestMethod.GET)
    @ResponseBody
    public Result<Object> getMiaoshaPath(MiaoshaUser user,@RequestParam("goodsId")long goodsIs,
                                         @RequestParam("verifyCode")int verifyCode){

        boolean check=miaoshaService.checkVrifyCode(user,goodsIs,verifyCode);
        if(!check){
            return Result.fail(CodeMsg.CODE_ERROR);
        }
        if(user==null){
            return Result.fail(CodeMsg.NO_USER);
        }
        String md5=miaoshaService.createPath(user,goodsIs);
        return Result.success(md5);
    }



    @RequestMapping(value = "verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<Object> verifyCode(MiaoshaUser user, @RequestParam("goodsId")long goodsIs, HttpServletResponse response){
        if(user==null){
            return Result.fail(CodeMsg.NO_USER);
        }
        BufferedImage image=miaoshaService.createVerifyCode(user,goodsIs);
        try {
            OutputStream stream = response.getOutputStream();
            ImageIO.write(image,"JPEG",stream);
            stream.flush();
            stream.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(CodeMsg.SERVER_ERROR);
        }
    }


}
