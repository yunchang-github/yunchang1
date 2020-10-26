package com.weiziplus.springboot.utils;

import com.weiziplus.springboot.config.GlobalConfig;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用工具类
 *
 * @author wanglongwei
 * @data 2019/7/5 15:18
 */
public class ToolUtil {


    /**
     * double保留小数位数---默认两位
     *
     * @param d
     * @return
     */
    public static Double doubleKeepDecimal(Double d) {
        return doubleKeepDecimal(d, 2);
    }

    /**
     * double保留小数位数
     *
     * @param d
     * @param number
     * @return
     */
    public static Double doubleKeepDecimal(Double d, Integer number) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return bigDecimal.setScale(number, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 字符串转Integer
     *
     * @param string
     * @return
     */
    public static Integer valueOfInteger(String string) {
        if (null == string) {
            return null;
        }
        string = string.trim();
        if (0 >= string.length() || GlobalConfig.UNDEFINED.equals(string) || GlobalConfig.NULL.equals(string)) {
            return null;
        }
        return Integer.valueOf(string);
    }

    /**
     * 字符串转Double
     *
     * @param string
     * @return
     */
    public static Double valueOfDouble(String string) {
        if (null == string) {
            return null;
        }
        string = string.trim();
        if (0 >= string.length() || GlobalConfig.UNDEFINED.equals(string) || GlobalConfig.NULL.equals(string)) {
            return null;
        }
        return Double.valueOf(string);
    }

    /**
     * 字符串转Long
     *
     * @param string
     * @return
     */
    public static Long valueOfLong(String string) {
        if (null == string) {
            return null;
        }
        string = string.trim();
        if (0 >= string.length() || GlobalConfig.UNDEFINED.equals(string) || GlobalConfig.NULL.equals(string)) {
            return null;
        }
        return Long.valueOf(string);
    }

    /***
     * 将对象转换为map对象
     * @param obj 对象
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj));
			}
		} catch (Exception e) {

		}
		return map;
	}


    /**
     * 将object对象转为list
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> objectOfList(Object obj, Class<T> clazz) {
        if (null == obj || null == clazz) {
            return null;
        }
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 将object对象转为set
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Set<T> objectOfSet(Object obj, Class<T> clazz) {
        if (null == obj || null == clazz) {
            return null;
        }
        Set<T> result = new HashSet<>();
        if (obj instanceof Set<?>) {
            for (Object o : (Set<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
