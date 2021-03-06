package com.onyx.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;


@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;


    /**
     * 获取对象
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix,String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //拼接上前缀
            String s = jedis.get(keyPrefix.getPrefix()+key);
            T t = stringToBean(s,clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 设置对象
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void set(KeyPrefix keyPrefix,String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String s = beanToString(value);
            if (s == null) {
                return;
            }
            //拼接上前缀
            int seconds = keyPrefix.expireSeconds();
            String realKey = keyPrefix.getPrefix() + key;
            if(seconds>0){
                jedis.setex(realKey,seconds,s);
            }else {
                jedis.set(realKey, s);
            }
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 键是否存在
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean exist(KeyPrefix keyPrefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //拼接上前缀
            return jedis.exists(keyPrefix.getPrefix()+key);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * delete 方法
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix keyPrefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //拼接上前缀
            Long del = jedis.del(keyPrefix.getPrefix() + key);
            return del>1;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * +1
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long incr(KeyPrefix keyPrefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //拼接上前缀
            return jedis.incr(keyPrefix.getPrefix()+key);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * -1
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long decr(KeyPrefix keyPrefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //拼接上前缀
            return jedis.decr(keyPrefix.getPrefix()+key);
        } finally {
            returnToPool(jedis);
        }
    }


    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> aClass = value.getClass();
        if (aClass == int.class || aClass == Integer.class) {
            return String.valueOf(value);
        } else if (aClass == String.class) {
            return String.valueOf(value);
        } else if (aClass == long.class || aClass == Long.class) {
            return String.valueOf(value);
        } else {
            return JSON.toJSONString(value);
        }
    }


    public static  <T> T stringToBean(String s, Class<T> aClass) {
        if(s==null || s.length()<=0){
            return null;
        }
        if (aClass == int.class || aClass == Integer.class) {
            return  (T)Integer.decode(s);
        } else if (aClass == String.class) {
            return (T)s;
        } else if (aClass == long.class || aClass == Long.class) {
            return (T)Long.decode(s);
        } else {
            return JSON.parseObject(s,aClass);
        }
    }


    /**
     * 返回到连接池中去
     *
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }


    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
