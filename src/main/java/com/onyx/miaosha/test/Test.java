package com.onyx.miaosha.test;

public class Test {

    /**
     * 1.压测  goods/to_list   没有cookie,我的机器上大概的支持的QPS是534,
     * 这个方法就是从mysql 中查询所有的商品,获取列表,返回到前端页面上,这个的
     * 瓶颈在mysql 上,会占用大量的CPU和内存资源
     *
     * 对于不用的测试计划,直接禁用掉就好了
     * 2b8a20df66d74235831db2aad5d87700
     *
     *
     *
     * Redis的压测:
     * redis-benchmark -h  127.0.0.1  -p 6379  -c 100  -n  10000
     * 100个并发链接,1000000个请求
     *
     * redis-benchmark  -h 127.0.0.1 -p 6379  -q -d  100
     * 存取大小为100字节的数据包
     *
     * redis-benchmark -t set,lpush -n 100000  -q
     * 只测试某些操作的性能
     *
     * redis-benchmark  -n  100000 -q script load "redis.call('set','foo','bar')"
     * 只测试某些数据存取的性能
     *
     */




}
