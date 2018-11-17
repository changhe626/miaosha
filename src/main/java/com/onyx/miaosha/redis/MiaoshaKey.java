package com.onyx.miaosha.redis;


public class MiaoshaKey extends BasePrefix{


    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaKey isGoodsOver=new MiaoshaKey(0,"go");
    public static MiaoshaKey miaoshaPath=new MiaoshaKey(60,"mp");
    public static MiaoshaKey getMiaoshaVerifyCode=new MiaoshaKey(300,"vs");



}
