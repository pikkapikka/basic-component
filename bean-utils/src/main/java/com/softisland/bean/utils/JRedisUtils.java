package com.softisland.bean.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by liwx on 15/11/9.
 */
public class JRedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(JRedisUtils.class);

    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 现在默认的template,key为字符串,value位字符串
     */
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 构建RedisTemplate和StringRedisTemplate
     * @param redisProperties
     */
    public static JRedisUtils getInstance(RedisProperties redisProperties){
    	//RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
        JedisShardInfo info = new JedisShardInfo(redisProperties.getHost(),redisProperties.getPort());
        info.setPassword(redisProperties.getPassword());
        JedisConnectionFactory factory = new JedisConnectionFactory(poolConfig);
        factory.setShardInfo(info);
        JRedisUtils redisUtils = new JRedisUtils(factory);
        return redisUtils;
    }
    /**
     * 构建RedisTemplate和StringRedisTemplate
     * @param factory
     */
    public JRedisUtils(RedisConnectionFactory factory){
        redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.afterPropertiesSet();
    }
    public String redisDbInfo(){
    	JedisConnectionFactory factory = (JedisConnectionFactory)stringRedisTemplate.getConnectionFactory();
    	JedisShardInfo js = factory.getShardInfo();
    	String ret = "redis db info:===>>"+js.getHost()+":"+js.getPort()+" passwd:"+js.getPassword();
    	logger.debug(ret);
    	return ret;
    }
    /**
     * 根据key和MAP中的key获取MAP中的值
     * @param key
     * @param hashKey
     * @return
     * @throws Exception
     */
    public Object getHashValueByKey(String key,String hashKey)throws Exception{
        return stringRedisTemplate.opsForHash().get(key,hashKey);
    }

    /**
     * 用来测试玩的
     * @throws Exception
     */
    public void test()throws Exception{
        //redisTemplate.opsForHash().put("lwx","aa","bb");
        //Object object = redisTemplate.opsForHash().get("lwx","aa");
        //stringRedisTemplate.opsForHash().put("1","1","11");
        //stringRedisTemplate.opsForHash().put("1","2","22");
        addValue("111","111");
        addValue("111","222");
        System.out.println(getValue("111"));
    }

    /**
     * 通过KEY获取HashMap
     * @param key
     * @return
     * @throws Exception
     */
    public Map<Object,Object> getMapEntries(String key)throws Exception{
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 将值存入MAP中
     * @param key
     * @param hashKey
     * @param hasValue
     * @throws Exception
     */
    public void putValueToMap(String key,String hashKey,String hasValue)throws Exception{
        stringRedisTemplate.opsForHash().put(key,hashKey,hasValue);
    }

    /**
     * 重新设置某个KEY的值
     * @param key
     * @param value
     * @throws Exception
     */
    public void setValue(String key,String value)throws Exception{
        stringRedisTemplate.opsForValue().set(key,value);
    }


    /**
     * 添加值
     * @param key
     * @param value
     * @throws Exception
     */
    public void addValue(String key,String value)throws Exception{
        stringRedisTemplate.opsForValue().append(key,value);
    }

    /**
     * 添加值到LIST中
     * @param key
     * @param value
     * @throws Exception
     */
    public void addValueToList(String key,String value)throws Exception{
        stringRedisTemplate.boundListOps(key).leftPush(value);
    }

    /**
     * 根据KEY从list中获取所有值
     * @param key
     * @return
     * @throws Exception
     */
    public List<String> getValuesFromList(String key)throws Exception{
        return stringRedisTemplate.boundListOps(key).range(0,-1);
    }

    /**
     * 通过key从LIST中获取指定范围的值
     * @param key
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<String> getValuesFromList(String key,int start,int end)throws Exception{
        return stringRedisTemplate.boundListOps(key).range(start,end);
    }

    /**
     * 获取LIST的大小
     * @param key
     * @return
     * @throws Exception
     */
    public Long getListSize(String key)throws Exception{
        return stringRedisTemplate.boundListOps(key).size();
    }

    /**
     * 从redis中获取值
     * @param key
     * @return
     * @throws Exception
     */
    public String getValue(String key)throws Exception{
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 添加一组值
     * @param values
     * @throws Exception
     */
    public void addValues(Map<String,String> values)throws Exception{
        stringRedisTemplate.opsForValue().multiSet(values);
    }

    /**
     * 获取某个KEY的值的数量
     * @param key
     * @return
     * @throws Exception
     */
    public Long getSize(String key)throws Exception{
        return stringRedisTemplate.opsForValue().size(key);
    }

    /**
     * 删除某个KEY的所有值
     * @param key
     * @throws Exception
     */
    public void deleteValue(String key)throws Exception{
        stringRedisTemplate.delete(key);
    }

    /**
     *
     * @param keys
     * @throws Exception
     */
    public void deleteValues(List<String> keys)throws Exception{
        stringRedisTemplate.delete(keys);
    }

    /**
     * 根据KEY移除某个KEY下面的MAP中某几个HashKey的值
     * @param key
     * @param hashKeys
     * @throws Exception
     */
    public void removeMapValues(String key,String... hashKeys)throws Exception{
        stringRedisTemplate.opsForHash().delete(key,hashKeys);
    }

    /**
     * 获取hash的大小
     * @param key
     * @return
     * @throws Exception
     */
    public Long getHashSize(String key)throws Exception{
        return stringRedisTemplate.opsForHash().size(key);
    }

    /**
     * 对现有的KEY进行重命名
     * @param oldKey
     * @param newKey
     * @throws Exception
     */
    public void renameKey(String oldKey,String newKey)throws Exception{
        stringRedisTemplate.rename(oldKey, newKey);

    }

    /**
     * 检查MAP中是否包含已存在的值
     * @param key
     * @param hashKey
     * @return
     * @throws Exception
     */
    public boolean hasKeyInMap(String key,String hashKey)throws Exception{
        return stringRedisTemplate.opsForHash().hasKey(key,hashKey);
    }



    public static void main(String[] args)throws Exception {
        /*JedisShardInfo info = new JedisShardInfo("114.215.178.40",6379);
        info.setPassword("1qaz2WSX!@");*/

        JedisShardInfo info = new JedisShardInfo("192.168.1.102",6379);
        info.setPassword("123456");

        RedisConnectionFactory factory = new JedisConnectionFactory(info);

        JRedisUtils utils = new JRedisUtils(factory);
        utils.test();
    }
}
