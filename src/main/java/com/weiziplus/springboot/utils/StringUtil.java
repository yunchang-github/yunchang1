package com.weiziplus.springboot.utils;

import com.weiziplus.springboot.config.GlobalConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串常用工具
 *
 * @author wanglongwei
 * @data 2019/5/7 17:00
 */
public class StringUtil {
    /**
     * 字符串为null或""或"undefined"
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (null == str) {
            return true;
        }
        str = str.trim();
        return 0 >= str.length() || GlobalConfig.UNDEFINED.equals(str) || GlobalConfig.NULL.equals(str);
    }

    /**
     * 字符串不为null或""或"undefined"
     *
     * @param str
     * @return
     */
    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 生成uuid
     *
     * @return
     */
    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 对象转字符串
     *
     * @param object
     * @return
     */
    public static String valueOf(Object object) {
        if (null == object) {
            return null;
        }
        return String.valueOf(object);
    }

    /**
     * 方案：只处理每个分割字符的两边空格， 中间空格不管。 考虑操作员在输入时候，在两端多了空格的问题
     * 	转换前：" 11 1 , 2 22   ，333 444 555 ,111,222,111,222";
     * 	转换后：[11 1, 22 2, 333 444 555, 111]
     * @return
     */
    public static List<String> parseReqParamToArrayUseTrim(String str) {
        List<String> collect = new ArrayList<>();
        if(str == null || str.length() == 0 || ("").equals(str.trim())) {
            return collect;
        }
        StringBuilder strbd = new StringBuilder();
        String regex = ",|，";
        String[] strAry = str.split(regex);
        // 过滤掉每个字符串两边的空格，例如： "111,   2 22 ,    3333    " -> "111,2 22,333"。不过滤中间的空格
        Arrays.asList(strAry).stream().forEach(item -> strbd.append(item.trim() + ","));
        // 重新处理为List<String>
        collect = Arrays.asList(strbd.toString().split(",")).stream().filter(param -> !("").equals(param)).distinct()
                .collect(Collectors.toList());
        return collect;
    }


    public static String parseReqParamToString(String str) {
        String collect = new String();
        if(str == null || str.length() == 0 || ("").equals(str.trim())) {
            return collect;
        }
        StringBuilder strbd = new StringBuilder();
        String regex = ",|，";
        String[] strAry = str.split(regex);
        // 过滤掉每个字符串两边的空格，例如： "111,   2 22 ,    3333    " -> "111,2 22,333"。不过滤中间的空格

        for (int i = 0; i <strAry.length ; i++) {
            if(i>=1){
                strbd.append(",");
            }
            strbd.append("'");
            strbd.append(strAry[i]);
            strbd.append("'");

        }
        return strbd.toString();
    }



    //获取一下币种
    public static String  getCurrency(String code){//传进来国家代号   jdk8中这个类不全
        String currencyCode = null;
        switch (code){
            case "BA":
                currencyCode =  CurrencyCode.BRL.getCode();
                break;
            case "CA":
                currencyCode =  CurrencyCode.CAD.getCode();
                break;
            case "MX":
                currencyCode =  CurrencyCode.MXN.getCode();
                break;
            case "US":
                currencyCode =  CurrencyCode.USD.getCode();
                break;
            case "AE":
                currencyCode =  CurrencyCode.AED.getCode();
                break;
            case "DE":
               // currencyCode =  CurrencyCode.EUR.getCode();  //德国是欧元，也有可能是德国自己的货币
                currencyCode =  CurrencyCode.EUR.getCode();
                break;
            case "ES":
                // currencyCode =  CurrencyCode.EUR.getCode();  //德国是欧元，也有可能是德国自己的货币
                currencyCode =  CurrencyCode.EUR.getCode();
                break;
            case "FR":
                currencyCode = CurrencyCode.EUR.getCode();
                break;
            case "IT":
                currencyCode = CurrencyCode.EUR.getCode();
                break;
            case "GB":
                currencyCode = CurrencyCode.GBP.getCode();
                break;
            case "IN":
                currencyCode = CurrencyCode.INR.getCode();
                break;
            case "TR":
                currencyCode = CurrencyCode.TRY.getCode();
                break;
            case "AU":
                currencyCode = CurrencyCode.AUD.getCode();
                break;
            case "JP":
                currencyCode = CurrencyCode.JPY.getCode();
                break;
            case "CN":
                currencyCode = CurrencyCode.CNY.getCode();
                break;
            default:
                currencyCode =  CurrencyCode.USD.getCode();
        }


        return currencyCode;
    }



    //根据币种得到国家
    public static String  getMwsCountryCode(String code){//传进来币种   jdk8中这个类不全
        String countryCode = null;
        switch (code){
            case "BRL":
                countryCode =  "BA";
                break;
            case "CAD":
                countryCode =  "CA";
                break;
            case "MXN":
                countryCode = "MX" ;
                break;
            case "USD":
                countryCode =  "US";
                break;
            case "AED":
                countryCode =  "AE";
                break;
            case "EUR":
                countryCode = "EUR";
                break;
            case "GBP":
                countryCode = "GB";
                break;
            case "INR":
                countryCode = "IN";
                break;
            case "TRY":
                countryCode = "TR";
                break;
            case "AUD":
                countryCode =  "AU";
                break;
            case "JPY":
                countryCode = "JP";
                break;
            case "CNY":
                countryCode = "CN";
                break;
            default:
                countryCode =  "US";
        }


        return countryCode;
    }


    //根据marketplaceName获取国家简称
    public static String  getMwsCountryCodeByMarketplaceName(String marketplaceName){//传进来币种   jdk8中这个类不全
        String countryCode = null;
        switch (marketplaceName){
            case "amazon.com":
                countryCode =  "US";
                break;
            case "amazon.ca":
                countryCode =  "CA";
                break;
            case "amazon.de":
                countryCode = "DE" ;
                break;
            case "amazon.co.uk":
                countryCode =  "UK";
                break;
            case "amazon.fr":
                countryCode =  "FR";
                break;
            case "amazon.es":
                countryCode = "ES";
                break;
            case "amazon.it":
                countryCode = "IT";
                break;

            default:
                countryCode =  "US";
        }


        return countryCode;
    }


    public static String getRemoveStringNum(String str) {
        StringBuilder builder=new StringBuilder();
        String regEx="(\\d+(\\.\\d+)?)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {//当符合正则表达式定义的条件时
            builder.append(m.group()+",");
        }
        String result = builder.toString();
        /*if(!StringUtils.isEmpty(result)&&(result.lastIndexOf(",")==(result.length()-1))){
            result = result.substring(0, result.length()-1);
        }*/
        return result.replace(",","" );
    }



}
