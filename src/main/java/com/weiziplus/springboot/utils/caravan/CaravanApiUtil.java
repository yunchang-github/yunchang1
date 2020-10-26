package com.weiziplus.springboot.utils.caravan;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.config.GlobalConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 马帮api
 *
 * @author wanglongwei
 * @data 2019/8/16 16:57
 */
@Slf4j
@Data
public class CaravanApiUtil {

    /**
     * 请求基本url
     */
    private static String BASE_URL = "http://openapi.mabangerp.com";

    /**
     * 请求成功的code
     */
    public static String CODE_SUCCESS = "000";

    /**
     * 请求失败的code
     */
    public static String CODE_ERROR = "999";

    /**
     * 获取数据
     *
     * @param params
     * @return
     */
    public static Map<String, Object> getClient(Map<String, String> params) {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        Map<String, String> baseParams = new HashMap<String, String>(7) {{
            put("developerId", GlobalConfig.DEVELOPER_ID);
            put("authToken", GlobalConfig.AUTH_TOKEN);
            put("companyId", GlobalConfig.COMPANY_ID);
            put("apiType", "private");
            put("version", "v3");
            put("action", "get-finance-list");
            put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        }};
        if (null != params) {
            baseParams.putAll(params);
        }
        StringBuilder stringBuilder = new StringBuilder(BASE_URL);
        boolean flag = false;
        for (Map.Entry<String, String> entry : baseParams.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (!flag) {
                stringBuilder.append("?");
                flag = true;
            } else {
                stringBuilder.append("&");
            }
            try {
                stringBuilder.append(key).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                log.warn("马帮api:url请求参数处理错误");
                return null;
            }
        }
        try {
            HttpGet get = new HttpGet(String.valueOf(stringBuilder));
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("马帮api:获取的内容为空");
                return null;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpServletResponse.SC_OK != statusCode) {
                log.warn("马帮api:请求失败，状态码:" + statusCode + "   详情:" + result);
                return null;
            }
            Map<String, Object> resultMap = (Map<String, Object>) JSON.parse(result);
            String resultCode = String.valueOf(resultMap.get("code"));
            if (!CODE_SUCCESS.equals(resultCode)) {
                log.warn("马帮api:请求失败，错误码:" + resultCode + "   详情:" + result);
                return null;
            }
            return resultMap;
        } catch (IOException e) {
            log.warn("马帮api:请求出错,详情:" + e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
