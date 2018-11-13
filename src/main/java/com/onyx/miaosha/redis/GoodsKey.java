package com.onyx.miaosha.redis;


public class GoodsKey extends BasePrefix{

    private GoodsKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    //设置缓存的时间为60s
    public static GoodsKey getGoodsList=new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail=new GoodsKey(60,"gd");



}
