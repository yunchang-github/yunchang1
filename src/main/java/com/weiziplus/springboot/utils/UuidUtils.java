package com.weiziplus.springboot.utils;

import java.util.UUID;
/*
* UUid工具类*/
public class UuidUtils {
    /**
     * 随机生成32位 大写 UUID
     * @return UUID
     */
    public static String buildUuid(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }
}
