package com.onyx.miaosha.redis;


public class OrderKey extends BasePrefix{

    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getOrderByUserIdGoodsId=new OrderKey("id");



}
