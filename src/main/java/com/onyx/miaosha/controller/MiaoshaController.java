package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.MiaoshaOrder;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.GoodsService;
import com.onyx.miaosha.service.MiaoshaService;
import com.onyx.miaosha.service.OrderService;
import com.onyx.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("miaosha")
public class MiaoshaController {

    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    //尽量不要在MiaoShaService中引入orderDao,这样结构不清晰,要么就引入orderService....


    /**
     * 优化之前的TPS 是421...  1000个并发跑了10次
     * 5000个用户...
     * 数据库的秒杀数量变成了-44.....实际上就20上商品,创建了64个订单出来....程序出错了.
     *
     *
     * @param user
     * @param id
     * @return
     * GET  是幂等的,调用多少次都是一样的结果,而且不对服务端产生影响
     * POST是提交数据
     *
     */
    @RequestMapping(value = "do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> miaoSha(MiaoshaUser user,@RequestParam("goodsId") long id){
        if(user==null){
            return Result.fail(CodeMsg.NO_USER);
        }
        //判断库存
        GoodsVo goodsVo = goodsService.getById(id);

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

        return Result.success(orderInfo);

    }




}
