package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.MiaoshaDao;
import com.onyx.miaosha.domain.MiaoshaOrder;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.redis.MiaoshaKey;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.utils.MD5Util;
import com.onyx.miaosha.utils.UUIDUtil;
import com.onyx.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaService {


    @Autowired
    private MiaoshaDao miaoshaDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean stock = goodsService.reduceStock(goods);
        //只有减库存成功了,才生成订单
        if(stock){
            //order_info maiosha_order
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getMiaoshaResult(Long id, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        if(order!=null){
            return order.getOrderId();
        }else {
            //没秒杀到,还在排队中
            boolean isOver=getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exist(MiaoshaKey.isGoodsOver,goodsId+"");
    }

    /**
     * 商品是否卖完了
     * @param goodsId
     * @return
     */
    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,goodsId+"",true);
    }


    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }


    public String createPath(MiaoshaUser user, long goodsIs) {
        String md5 = MD5Util.md5(UUIDUtil.uuid() + "123");
        redisService.set(MiaoshaKey.miaoshaPath, user.getId() + "_" + goodsIs, md5);
        return md5;

    }

    public boolean checkPath(MiaoshaUser user, long goodsIs, String path) {
        if(user==null ||StringUtils.isBlank(path) || goodsIs<0){
            return false;
        }
        String s = redisService.get(MiaoshaKey.miaoshaPath, user.getId() + "_" + goodsIs, String.class);
        if(path.equals(s)){
            //用完一次就删除
            redisService.delete(MiaoshaKey.miaoshaPath, user.getId() + "_" + goodsIs);
            return true;
        }else {
            return false;
        }
    }


    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsIs) {
        if(user==null || goodsIs<0){
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsIs, rnd);
        //输出图片
        return image;


    }

    private int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};

    /**
     * + - *
     * 生成验证码
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 验证码的校验
     */
    public boolean checkVrifyCode(MiaoshaUser user, long goodsIs, int verifyCode) {
        if(user==null || goodsIs<0){
            return false;
        }
        Integer integer = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsIs, Integer.class);
        if(integer==null){
            return false;
        }
        //删除验证码
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsIs);
        if(integer.compareTo(verifyCode)==0){
            return true;
        }else {
            return false;
        }
    }


}
