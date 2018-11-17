package com.onyx.miaosha.redis;


public class AccessKey extends BasePrefix{

    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    //设置缓存的时间为60s
    public static AccessKey access=new AccessKey(5,"access");

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }



}
