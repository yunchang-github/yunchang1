package com.weiziplus.springboot.utils.amazon;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.models.RefreshTokenDO;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.ShopAreaProfile;
import com.weiziplus.springboot.utils.APICallsUtil;
import com.weiziplus.springboot.utils.CryptoUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 亚马逊广告api
 *
 * @author wanglongwei
 * @data 2019/8/13 11:16
 */
/*
* 主要修改了线程的睡眠时间，设定三秒，保证请求能正确连接
* --苏建东
* */
@Slf4j
public class AmazonAdvertApiUtil {

    /**
     * 刷新token
     */
   // private static final String REFRESH_TOKEN = GlobalConfig.REFRESH_TOKEN;

    /**
     * client_id
     */
    private static final String CLIENT_ID = GlobalConfig.CLIENT_ID;

    /**
     * client_secret
     */
    private static final String CLIENT_SECRET = GlobalConfig.CLIENT_SECRET;

    /**
     * redis里面存放的access_token的key
     */
    private static final String REDIS_KEY_ACCESS_TOKEN = "access_token";

    /**
     * Amazon-Advertising-API-Scope
     */
//    private static final String AMAZON_ADVERTISING_API_SCOPE = GlobalConfig.SCOPE_PROFILE_ID;

    /**
     * AMAZON_ADVERTISING_API_REDIRECT_URI
     */
    private static final String AMAZON_ADVERTISING_API_REDIRECT_URI= GlobalConfig.REDIRECT_URI;

    /**
     * 请求令牌
     *
     * @return
     */
    public static Map setTokenByCode(String regionCode,String code) {
        String url = "";
        if ("NA".equals(regionCode)){
            url = "https://api.amazon.com/auth/o2/token";
        }else if ("EU".equals(regionCode)){
            url = "https://api.amazon.co.uk/auth/o2/token";
        }else if ("FE".equals(regionCode)){
            url = "https://api.amazon.co.jp/auth/o2/token";
        }
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        Map<String,Object> map = new HashMap<String,Object>(2);
        try {
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("charset","UTF-8");
            List<NameValuePair> params = new ArrayList<>(5);
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            params.add(new BasicNameValuePair("client_id", CLIENT_ID));
            params.add(new BasicNameValuePair("code", code));
            params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
            params.add(new BasicNameValuePair("redirect_uri", AMAZON_ADVERTISING_API_REDIRECT_URI));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            post.setEntity(urlEncodedFormEntity);
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("获取的内容为空");
                map.put("flag",false);
                return map;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpServletResponse.SC_OK != statusCode && HttpServletResponse.SC_ACCEPTED != statusCode) {
                log.warn("状态码不是200或202,状态码：" + statusCode + "---请求地址url:" + url + "---详情:" + result);
                map.put("flag",false);
                return map;
            }
            Map<String, Object> result1 = (Map<String, Object>) JSON.parse(result);
            //RedisUtil.set(REDIS_KEY_ACCESS_TOKEN, result1.get("access_token"));
            map.put("flag",true);
            map.put("refresh_token",result1.get("refresh_token"));
            return map;
        } catch (IOException e) {
            log.warn("获取refresh_token出错,详情:" + e);
            map.put("flag",false);
            return map;
        }
    }




    /**
     * 获取access_token
     *
     * @return
     */
    public static boolean setAccessTokenToRedis(RefreshTokenDO refreshTokenDO) {
        String url = "";
        if ("NA".equals(refreshTokenDO.getRegionCode())){
            url = "https://api.amazon.com/auth/o2/token";
        }else if ("EU".equals(refreshTokenDO.getRegionCode())){
            url = "https://api.amazon.co.uk/auth/o2/token";
        }else if ("FE".equals(refreshTokenDO.getRegionCode())){
            url = "https://api.amazon.co.jp/auth/o2/token";
        }

        //设置代理IP、端口、协议（请分别替换）
        HttpHost proxy = new HttpHost("127.0.0.1", 3128, "http");

        //把代理设置到请求配置
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setProxy(proxy).setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000)
                .build();

        //实例化CloseableHttpClient对象
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();

        //CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
        //        .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        try {
            log.info("--------------获取access_token任务开始------------");
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> params = new ArrayList<>(4);
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            params.add(new BasicNameValuePair("client_id", CLIENT_ID));
            params.add(new BasicNameValuePair("refresh_token", refreshTokenDO.getRefreshToken()));
            params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            post.setEntity(urlEncodedFormEntity);
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("获取的内容为空");
                return false;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpServletResponse.SC_OK != statusCode && HttpServletResponse.SC_ACCEPTED != statusCode) {
                log.warn("状态码不是200或202,状态码：" + statusCode + "---请求地址url:" + url + "---详情:" + result);
                return false;
            }
            Map<String, Object> result1 = (Map<String, Object>) JSON.parse(result);
            RedisUtil.set(REDIS_KEY_ACCESS_TOKEN+ CryptoUtil.decode(refreshTokenDO.getSellerId()) + refreshTokenDO.getRegionCode(), result1.get("access_token"));
            System.out.println("access_token获取成功");
            return true;
        } catch (IOException e) {
            log.warn("获取access_token出错,详情:" + e);
            return false;
        }
    }

    /**
     * 获取配置文件
     *
     * @return
     */
    public static List<Map<String, Object>> getProfiles(RefreshTokenDO refreshTokenDO) {
        String url = "";
        if ("NA".equals(refreshTokenDO.getRegionCode())){
            url = "https://advertising-api.amazon.com";
        }else if ("EU".equals(refreshTokenDO.getRegionCode())){
            url = "https://advertising-api-eu.amazon.com";
        }else if ("FE".equals(refreshTokenDO.getRegionCode())){
            url = "https://advertising-api-fe.amazon.com";
        }
        String baseUrl = "/v2/profiles";
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        try {
            HttpGet get = new HttpGet(url + baseUrl);
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Authorization", "Bearer " + String.valueOf(RedisUtil.get(REDIS_KEY_ACCESS_TOKEN + CryptoUtil.decode(refreshTokenDO.getSellerId()) + refreshTokenDO.getRegionCode())));
            get.addHeader("Amazon-Advertising-API-ClientId", CLIENT_ID);
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("获取的内容为空");
                return null;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpServletResponse.SC_OK != statusCode) {
                log.warn("状态码不是200,状态码：" + statusCode + "---获取配置文件信息失败---详情:" + result);
                return null;
            }
            return (List<Map<String, Object>>) JSON.parse(result);
        } catch (IOException e) {
            log.warn("获取配置文件信息失败,详情:" + e);
            return null;
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param paramsJsonStr---json字符串
     * @return
     */
    public static Map<String, Object> post(ShopAreaProfile shopAreaProfile,String sellerId, String url, Map<String, Object> paramsJsonStr) {
        String baseUrl = "";
        if ("NA".equals(shopAreaProfile.getRegionCode())){
            baseUrl = "https://advertising-api.amazon.com";
        }else if ("EU".equals(shopAreaProfile.getRegionCode())){
            baseUrl = "https://advertising-api-eu.amazon.com";
        }else if ("FE".equals(shopAreaProfile.getRegionCode())){
            baseUrl = "https://advertising-api-fe.amazon.com";
        }
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        try {
            HttpPost post = new HttpPost(baseUrl + url);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Authorization", "Bearer " + String.valueOf(RedisUtil.get(REDIS_KEY_ACCESS_TOKEN + sellerId + shopAreaProfile.getRegionCode())));
            post.addHeader("Amazon-Advertising-API-Scope", shopAreaProfile.getProfileId());
            post.addHeader("Amazon-Advertising-API-ClientId", CLIENT_ID);
            if (null != paramsJsonStr) {
                post.setEntity(new StringEntity(JSON.toJSONString(paramsJsonStr)));
            }
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("I'm a sign-----获取的内容为空");
                return null;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            int statusCode = response.getStatusLine().getStatusCode();
            //请求成功状态码为202
            if (HttpServletResponse.SC_OK != statusCode && HttpServletResponse.SC_ACCEPTED != statusCode) {
                log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺请求reportID时出错，状态码不是202,状态码：" + statusCode + "---请求地址url:" + url + "---详情:" + result);
                return null;
            }
            return (Map<String, Object>) JSON.parse(result);
        } catch (IOException e) {
            log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺发送post请求出错,详情:" + e);
            System.out.println("I'm a sign-----errorNum:" + APICallsUtil.errorNum);
            return null;
        }
    }

    /**
     * 发送get请求
     *
     * @param url
     * @return
     */
    public static Map<String, Object> get(ShopAreaProfile shopAreaProfile,String sellerId, String url) {
        String baseUrl = "";
        if ("NA".equals(shopAreaProfile.getRegionCode())){
            baseUrl = "https://advertising-api.amazon.com";
        }else if ("EU".equals(shopAreaProfile.getRegionCode())){
            baseUrl = "https://advertising-api-eu.amazon.com";
        }else if ("FE".equals(shopAreaProfile.getRegionCode())){
            baseUrl = "https://advertising-api-fe.amazon.com";
        }
        //String baseUrl = "https://advertising-api.amazon.com";
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        try {
            HttpGet get = new HttpGet(baseUrl + url);
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Authorization", "Bearer " + String.valueOf(RedisUtil.get(REDIS_KEY_ACCESS_TOKEN + sellerId + shopAreaProfile.getRegionCode())));
            get.addHeader("Amazon-Advertising-API-Scope", shopAreaProfile.getProfileId());
            get.addHeader("Amazon-Advertising-API-ClientId", CLIENT_ID);
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("I'm a sign-----获取的内容为空");
                return null;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            int statusCode = response.getStatusLine().getStatusCode();
            //请求成功状态码为202
            if (HttpServletResponse.SC_OK != statusCode && HttpServletResponse.SC_ACCEPTED != statusCode) {
                log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺获取location时出错，状态码不是202或202,状态码：" + statusCode + "---请求地址url:" + url + "---详情:" + result);
                return null;
            }
            return (Map<String, Object>) JSON.parse(result);
        } catch (IOException e) {
            log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺发送get请求出错,详情:" + e);
            System.out.println("I'm a sign-----errorNum:" + APICallsUtil.errorNum);
            return null;
        }
    }

    /**
     * 下载并处理json格式的gz压缩包
     *
     * @param url
     * @return
     */
    public static List<Map<String, Object>> downloadJsonGz(ShopAreaProfile shopAreaProfile,String sellerId, String url) {
        RequestConfig build = RequestConfig.custom()
                .setConnectionRequestTimeout(180000)
                .setConnectTimeout(180000)
                .setSocketTimeout(180000)
                //阻止重定向
                .setRedirectsEnabled(false)
                .build();
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(build).build();
        try {
            HttpGet get = new HttpGet(url);
            get.addHeader("Authorization", "Bearer " + String.valueOf(RedisUtil.get(REDIS_KEY_ACCESS_TOKEN + sellerId + shopAreaProfile.getRegionCode())));
            get.addHeader("Amazon-Advertising-API-Scope", shopAreaProfile.getProfileId());
            get.addHeader("Amazon-Advertising-API-ClientId", CLIENT_ID);
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("I'm a sign-----获取的内容为空");
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            //请求状态码不是307
            if (HttpServletResponse.SC_TEMPORARY_REDIRECT != statusCode) {
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
                log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺获取report结果时错误，状态码不是307,状态码：" + statusCode + "---请求地址url:" + url + "---详情:" + result);
                return null;
            }
            //获取跳转目标地址
            Header header = response.getFirstHeader("location");
            Thread.sleep(3000L);
            HttpGet newGet = new HttpGet(header.getValue());
            CloseableHttpResponse response1 = client.execute(newGet);
            HttpEntity entity1 = response1.getEntity();
            if (null == entity1) {
                log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺获取report的内容为空");
                return null;
            }
            APICallsUtil.success();
            return readJsonGzToList(entity1.getContent());
        } catch (Exception e) {
            log.warn("I'm a sign-----shopid=" + shopAreaProfile.getShopId() + "，areaid=" + shopAreaProfile.getAreaId() + "的店铺下载并处理json格式的gz压缩包出错,详情:" + e);
            APICallsUtil.error();
            System.out.println("I'm a sign-----errorNum:" + APICallsUtil.errorNum);
            return null;
        }
    }

    /**
     * 根据输入流读取gz压缩包里面的json数据
     *
     * @param fileInputStream
     * @return
     * @throws IOException
     */
    private static List<Map<String, Object>> readJsonGzToList(InputStream fileInputStream) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
        InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder jsonString = new StringBuilder();
        String line;
        while (null != (line = bufferedReader.readLine())) {
            jsonString.append(line);
        }
        bufferedReader.close();
        inputStreamReader.close();
        gzipInputStream.close();
        //System.out.println("解析后的数据：" + (List<Map<String, Object>>) JSON.parse(String.valueOf(jsonString)));
        return (List<Map<String, Object>>) JSON.parse(String.valueOf(jsonString));
    }

    /**
     * 获取数据
     */
    public static List<Map<String, Object>> getReport(ShopAreaProfile shopAreaProfile, String sellerId, String type1, String type2, Map<String, Object> params) {
        Map<String, Object> post = post(shopAreaProfile,sellerId, "/v2/" + type1 + "/" + type2 + "/report", params);
        if (null == post) {
            APICallsUtil.error();
            return null;
        }
        try {
            Thread.sleep((long) (Math.pow(2,APICallsUtil.errorNum)*1000));
            System.out.println("errorNum:" + APICallsUtil.errorNum);
        } catch (Exception e) {
            log.warn("I'm a sign-----亚马逊广告api获取下载链接休眠" + APICallsUtil.errorNum + "秒出错,详情:" + e);
            return null;
        }
        String reportId = String.valueOf(post.get("reportId"));
        Map<String, Object> get = get(shopAreaProfile, sellerId,"/v2/reports/" + reportId);
        if (null == get) {
            APICallsUtil.error();
            return null;
        }
        Object downUrl = get.get("location");
        if (null == downUrl) {
            APICallsUtil.error();
            return null;
        }
        try {
            Thread.sleep((long) (Math.pow(2,APICallsUtil.errorNum)*1000));
            System.out.println("errorNum:" + APICallsUtil.errorNum);
        } catch (Exception e) {
            log.warn("I'm a sign-----亚马逊广告api下载请求休眠" + APICallsUtil.errorNum + "秒出错,详情:" + e);
            return null;
        }
        return downloadJsonGz(shopAreaProfile,sellerId, String.valueOf(downUrl));
    }

}
