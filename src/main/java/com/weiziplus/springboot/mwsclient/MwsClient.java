package com.weiziplus.springboot.mwsclient;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static com.weiziplus.springboot.mwsclient.MwsClientConstant.*;
import static com.weiziplus.springboot.mwsclient.MwsClientUtil.sortParams;

public abstract class MwsClient {
    private static final Logger logger = LoggerFactory.getLogger(MwsClient.class);
    private MwsClientConfig config=new MwsClientConfig();
    private MwsClientAction action=new MwsClientAction();
    private HashMap<String, String> parameters=new HashMap<>();
    /**
     * 初始化请求必填固定参数
     * @param sellerId      商家授权ID
     * @param mWSAuthToken  商家授权Token
     * @param aWSAccessKeyId    亚马逊秘钥ID
     * @param secretKey     亚马逊秘钥
     */
    protected void builderConfig(String sellerId,String mWSAuthToken,String aWSAccessKeyId,String secretKey){
        this.config.setSellerId(MwsClientUtil.urlEncode(sellerId));
        this.config.setmWSAuthToken(MwsClientUtil.urlEncode(mWSAuthToken));
        this.config.setaWSAccessKeyId(MwsClientUtil.urlEncode(aWSAccessKeyId));
        this.config.setSecretKey(secretKey);
    }
    protected void builderConfig(MwsClientConfig config){
        this.config=config;
    }

    /**
     * 初始化请求相关参数
     * @param postUrI   请求地址
     * @param module    请求模块
     * @param version   请求版本
     * @param method    请求方法
     * @param marketplaceId 亚马逊节点ID
     */
    protected void builderAction(String postUrI,String module,String version,String method,String marketplaceId){
        this.action.setPostUrI(postUrI);
        this.action.setModule(module);
        this.action.setVersion(version);
        this.action.setMethod(method);
        this.action.setMarketplaceId(marketplaceId);
        this.initParametersConfig();
    }
    protected void builderAction(MwsClientAction action){
        this.action=action;
        this.initParametersConfig();
    }
    /**
     * 设置parameters初始参数
     */
    private void initParametersConfig(){
        this.parameters.put("MarketplaceId.Id.1", action.getMarketplaceId());
        this.parameters.put("AWSAccessKeyId", config.getaWSAccessKeyId());
        this.parameters.put("MWSAuthToken", config.getmWSAuthToken());
        this.parameters.put("SellerId", config.getSellerId());
    }
    /**
     * 设置修改请求的 API方法
     * @param method 要请求的方法
     */
    protected void setPostMethod(String method){
        this.action.setMethod(method);
    }
    /***
     * post请求通道
     */
    public JSONObject doPost(HashMap<String, String> params) throws Exception {
        String str = DateTime.now(DateTimeZone.UTC).toString(TIME_FORMAT_STR);
        // 拼接完整URL
        String url=this.action.getPostUrI()+"/"+this.action.getModule()+"/"+this.action.getVersion();
        parameters.put("Timestamp", MwsClientUtil.urlEncode(str));
        parameters.put("Action", MwsClientUtil.urlEncode(this.action.getMethod()));
        parameters.put("SignatureMethod", MwsClientUtil.urlEncode(ALGORITHM));
        parameters.put("Version", MwsClientUtil.urlEncode(this.action.getVersion()));
        parameters.put("SignatureVersion", MwsClientUtil.urlEncode(SIGNATURE_VERSION));
        // 把自定义传参合并到当前parameters数据里
        params.forEach((key,value)->parameters.merge(key,value,(v1,v2) -> (v2)));
        String formattedParameters = calculateStringToSignV2(parameters,url );
        logger.info("签名内容：{}", formattedParameters);
        String signature = MwsClientUtil.sign(formattedParameters, this.config.getSecretKey());
        logger.info("签名signature: {}", signature);
        parameters.put("Signature", MwsClientUtil.urlEncode(signature));
        parameters.put("Timestamp", str);
        String paramStr = sortParams(new StringBuilder(), parameters);
        logger.info("排序后参数：{}", paramStr);
        return doPost(url, paramStr);
    }
    /**
     * signV2签名内容
     *
     * @param parameters
     * @param serviceUrl
     * @return
     * @throws SignatureException
     * @throws URISyntaxException
     */
    private String calculateStringToSignV2(
            Map<String, String> parameters, String serviceUrl)
            throws  URISyntaxException {
        URI endpoint = new URI(serviceUrl.toLowerCase());
        StringBuilder data = new StringBuilder();
        data.append("POST\n");
        data.append(endpoint.getHost());
        data.append("\n/"+this.action.getModule()+"/"+this.action.getVersion());
        data.append("\n");
        return sortParams(data, parameters);
    }
    /***
     * post请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static JSONObject doPost(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpPost.setHeader("Accept", "Accept: text/plain, */*");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
        httpPost.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
        httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            InputStream is = responseEntity.getContent();
            JSON xml = new XMLSerializer().readFromStream(is);
            return JSONObject.fromObject(xml);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw e;
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }
}
