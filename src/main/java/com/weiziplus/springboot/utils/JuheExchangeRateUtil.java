package com.weiziplus.springboot.utils;

import com.weiziplus.springboot.models.DO.ExchangeRateDO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JuheExchangeRateUtil {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的KEY
    public static final String APPKEY ="431fa31e69caa3cdd7e28b62d3f2cbef";

    //1.人民币牌价
    public static void getRequest1(){
        String result =null;
        String url ="http://web.juhe.cn:8080/finance/exchange/rmbquot";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//APP Key
        params.put("type","");//两种格式(0或者1,默认为0)

        try {
            result =net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                System.out.println(object.get("result"));
            }else{
                System.out.println(object.get("error_code")+":"+object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //2.外汇汇率
    public static List<ExchangeRateDO> getRequest2(){
        String result =null;
        String url ="http://op.juhe.cn/onebox/exchange/currency";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("from","MXN");//转换汇率前的货币代码
        params.put("to","USD");//转换汇率成的货币代码
        params.put("key",APPKEY);//APP Key
        try {
            result =net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                JSONArray jsonArray = JSONArray.fromObject(object.get("result"));
                JSONObject list = jsonArray.getJSONObject(0);
                List<ExchangeRateDO> ExchangeRateList = new ArrayList<>();
                for (int i = 1; i <= list.size(); i++) {
                    JSONArray jsonArr = JSONArray.fromObject(list.get("data" + i));
                    JSONObject asd = jsonArr.getJSONObject(0);
                    ExchangeRateDO exchangeRateDO = (ExchangeRateDO) JSONObject.toBean(asd,ExchangeRateDO.class);
                    if (exchangeRateDO.getCode().substring(0,3).equals("USD")){
                        //1除以汇率
                        BigDecimal closePri = exchangeRateDO.getClosePri();
                        closePri = BigDecimal.valueOf(1).divide(closePri,2, RoundingMode.HALF_UP);
                        exchangeRateDO.setClosePri(closePri);
                        String subCode = exchangeRateDO.getCode().replace("USD","");
                        exchangeRateDO.setCode(subCode + "USD");
                        String subCurrency = exchangeRateDO.getCurrency().replace("美元","");
                        exchangeRateDO.setCurrency(subCurrency + "美元");
                    }
                    ExchangeRateList.add(exchangeRateDO);
                }
                return ExchangeRateList;
            }else{
                System.out.println(object.get("error_code")+":"+object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    out.writeBytes(urlencode(params));
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
