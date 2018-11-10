package com.onyx.miaosha.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();


}
