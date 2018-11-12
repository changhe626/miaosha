package com.onyx.miaosha.service;

import com.onyx.miaosha.dao.MiaoshaDao;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {


    @Autowired
    private MiaoshaDao miaoshaDao;

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        goodsService.reduceStock(goods);
        //order_info maiosha_order
        return orderService.createOrder(user, goods);
    }

}
