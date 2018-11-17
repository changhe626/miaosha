package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.MiaoshaDao;
import com.onyx.miaosha.domain.MiaoshaOrder;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.redis.MiaoshaKey;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
