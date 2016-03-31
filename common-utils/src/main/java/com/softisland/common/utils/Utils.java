package com.softisland.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


/**
 * Created by liwx on 2015/4/15.
 */
public final class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * 获取32位的UUID
     * @return
     */
    public static String getUuid(){
        return UUID.randomUUID().toString().toUpperCase().replace("-","");
    }

    /**
     * 获取20位的流水ID
     * @return
     */
    public static String get20Id(){
        return DateTimeUtil.dateToStrSimpleYMDHMSS()+Double.toString(Math.random()).substring(2, 5);
    }

    /**
     * 获取a-zA-Z0-9的随机码
     * @param count
     * @return
     */
    public static String getRandomAlphanumeric(int count){
        if(count <=0 ){
            count = 4;
        }
        return RandomStringUtils.randomAlphanumeric(count);
    }

}
