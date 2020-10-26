package com.weiziplus.springboot.utils;

import org.springframework.stereotype.Component;
/**
 * 设置错误次数，每次请求失败后增加一次，睡眠时间翻倍，成功后置零
* */

public class APICallsUtil {
    public static int errorNum = 1;

    public static void error(){
        if (errorNum <= 6){
            errorNum ++;
        }
    }

    public static void success(){
        errorNum = 1;
    }
}
