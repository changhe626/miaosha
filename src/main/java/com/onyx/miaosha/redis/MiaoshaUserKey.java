package com.onyx.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600*24 * 2;

    private MiaoshaUserKey(int time,String prefix) {
        super(time,prefix);
    }

    public static MiaoshaUserKey  token=new MiaoshaUserKey(TOKEN_EXPIRE,"tk");


}
