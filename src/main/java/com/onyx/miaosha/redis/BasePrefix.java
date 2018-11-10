package com.onyx.miaosha.redis;

/**
 * 为了防止 redis的key 的重复,我们在前面设置一个前缀,防止重复...
 * 两级的控制....
 */
public abstract  class BasePrefix  implements KeyPrefix{


    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix){
        this(0,prefix);
    }


    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }


    /**
     * 0  代表永远不过期
     * @return
     */
    @Override
    public int expireSeconds() {
        return 0;
    }

    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName+":"+prefix;
    }



}
