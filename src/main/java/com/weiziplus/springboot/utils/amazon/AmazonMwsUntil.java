package com.weiziplus.springboot.utils.amazon;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.XML;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author wanglongwei
 * @data 2019/7/12 16:45
 */
@Slf4j
public class AmazonMwsUntil {

    private static final String SIGNATURE_METHOD = "HmacSHA256";

    private static final String SIGNATURE_VERSION = "2";

    private static final String VERSION = "2009-01-01";

    public static final String ERROR_RESPONSE = "ErrorResponse";

    public static String client(String action, String mwsUrl, String secretKey, Map<String, String> params,String link) {
        Map<String, String> parameters = new HashMap<>(params);
        parameters.put("Action", urlEncode(action));
        parameters.put("SignatureMethod", urlEncode(SIGNATURE_METHOD));
        parameters.put("SignatureVersion", urlEncode(SIGNATURE_VERSION));
        parameters.put("Timestamp", urlEncode(DateUtil.getISO8601Timestamp()));
        parameters.put("Version", urlEncode(VERSION));
        try {
            String formattedParameters = calculateStringToSignV2(parameters, mwsUrl,link);
            String signature = sign(formattedParameters, secretKey);
            parameters.put("Signature", urlEncode(signature));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(mwsUrl);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setHeader("Accept", "Accept: text/plain, */*");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
            httpPost.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            StringEntity entity = new StringEntity(sortParams(parameters), StandardCharsets.UTF_8);
            //设置超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(180000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (null == httpEntity) {
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            if (HttpServletResponse.SC_OK != statusCode) {
                log.warn("亚马逊api请求出错状态码不是200,状态码：" + statusCode + "---详情:" + result);
                return null;
            }
            EntityUtils.consume(httpEntity);
            return result;
        } catch (Exception e) {
            log.warn("亚马逊api请求出错" + e);
            return null;
        }
    }

    public static Map<String, Object> clientXML(String action, String mwsUrl, String secretKey, Map<String, String> params) {
        String client = client(action, mwsUrl, secretKey, params,"");
        if (null == client) {
            return null;
        }
        return (Map<String, Object>) JSON.parse(String.valueOf(XML.toJSONObject(client)));
    }

    public static List<List<String>> clientTXT(String action, String mwsUrl, String secretKey, Map<String, String> params) {
        String client = client(action, mwsUrl, secretKey, params,"");
        if (null == client) {
            return null;
        }
        String regex = "^<\\?xml version[\\s\\S]*$";
        if (Pattern.matches(regex, client)) {
            log.warn("请求出错:" + action + "error:" + client);
            return null;
        }
        String[] rows = client.split("\n");
        List<List<String>> result = new ArrayList<>(rows.length);
        for (String row : rows) {
            //-1是为了防止对字符串左右进行去空字符串处理,空字符串为必要数据
            String[] fields = row.replace("\r", "").split("\t", -1);
            result.add(Arrays.asList(fields));
        }
        return result;
    }

    public static List<List<String>> clientTXT2(String action, String mwsUrl, String secretKey, Map<String, String> params) {
        String client = client(action, mwsUrl, secretKey, params,"");
        if (null == client) {
            return null;
        }
        String regex = "^<\\?xml version[\\s\\S]*$";
        if (Pattern.matches(regex, client)) {
            log.warn("请求出错:" + action + "error:" + client);
            return null;
        }
        String[] rows = client.split("\n");
        List<List<String>> result = new ArrayList<>(rows.length);
        for (String row : rows) {
            //-1是为了防止对字符串左右进行去空字符串处理,空字符串为必要数据
            String[] fields = row.replace("\r", "").replace("\"", "").split(",", -1);
            result.add(Arrays.asList(fields));
        }
        return result;
    }

    /**
     * If Signature Version is 2, string to sign is based on following:
     * *
     * *    1. The HTTP Request Method followed by an ASCII newline (%0A)
     * *
     * *    2. The HTTP Host header in the form of lowercase host,
     * *       followed by an ASCII newline.
     * *
     * *    3. The URL encoded HTTP absolute path component of the URI
     * *       (up to but not including the query string parameters);
     * *       if this is empty use a forward '/'. This parameter is followed
     * *       by an ASCII newline.
     * *
     * *    4. The concatenation of all query string components (names and
     * *       values) as UTF-8 characters which are URL encoded as per RFC
     * *       3986 (hex characters MUST be uppercase), sorted using
     * *       lexicographic byte ordering. Parameter names are separated from
     * *       their values by the '=' character (ASCII character 61), even if
     * *       the value is empty. Pairs of parameter and values are separated
     * *       by the '&' character (ASCII code 38).
     *
     * @param parameters
     * @param serviceUrl
     * @return
     * @throws SignatureException
     * @throws URISyntaxException
     */
    private static String calculateStringToSignV2(Map<String, String> parameters, String serviceUrl,String link) throws SignatureException, URISyntaxException {
        // Set endpoint value
        URI endpoint = new URI(serviceUrl.toLowerCase());

        // Create flattened (String) representation
        StringBuilder data = new StringBuilder();
        data.append("POST\n");
        data.append(endpoint.getHost());
        data.append("\n/"+link);
        data.append("\n");

        return sortParams(data, parameters);
    }

    /**
     * 对传递参数转换
     *
     * @param data
     * @param parameters
     * @return
     */
    private static String sortParams(StringBuilder data, Map<String, String> parameters) {
        Map<String, String> sorted = new TreeMap<>(parameters);

        Iterator<Map.Entry<String, String>> pairs = sorted.entrySet().iterator();
        while (pairs.hasNext()) {
            Map.Entry<String, String> pair = pairs.next();
            if (pair.getValue() != null) {
                data.append(pair.getKey()).append("=").append(pair.getValue());
            } else {
                data.append(pair.getKey()).append("=");
            }
            // Delimit parameters with ampersand (&)
            if (pairs.hasNext()) {
                data.append("&");
            }
        }
        return data.toString();
    }

    /**
     * 对传递参数转换
     *
     * @param parameters
     * @return
     */
    private static String sortParams(Map<String, String> parameters) {
        return sortParams(new StringBuilder(), parameters);
    }

    /**
     * Sign the text with the given secret key and convert to base64
     *
     * @param data
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalStateException
     */
    private static String sign(String data, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {
        Mac mac = Mac.getInstance(SIGNATURE_METHOD);
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                SIGNATURE_METHOD));
        byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(signature),
                StandardCharsets.UTF_8);
    }

    public static String urlEncode(String rawValue) {
        String value = (rawValue == null) ? "" : rawValue;
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Unknown encoding: " + StandardCharsets.UTF_8);
            e.printStackTrace();
        }
        return encoded;
    }


    public static Map<String, Object> paymentClientXML(String action, String mwsUrl, String secretKey, Map<String, String> params,String link) {

        String client = paymentClient(action, mwsUrl, secretKey, params,link);
        if (null == client) {
            return null;
        }
        return (Map<String, Object>) JSON.parse(String.valueOf(XML.toJSONObject(client)));
    }

// 获取亚马逊财务数据

    public static String paymentClient(String action, String mwsUrl, String secretKey, Map<String, String> params,String link) {
        Map<String, String> parameters = new HashMap<>(params);
        parameters.put("Action", urlEncode(action));
        parameters.put("SignatureMethod", urlEncode(SIGNATURE_METHOD));
        parameters.put("SignatureVersion", urlEncode(SIGNATURE_VERSION));
        parameters.put("Timestamp", urlEncode(DateUtil.getISO8601Timestamp()));
        parameters.put("Version", urlEncode("2015-05-01"));
        try {
            String formattedParameters = calculateStringToSignV2(parameters, mwsUrl,link);
            String signature = sign(formattedParameters, secretKey);
            parameters.put("Signature", urlEncode(signature));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(mwsUrl);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setHeader("Accept", "Accept: text/plain, */*");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
            httpPost.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            StringEntity entity = new StringEntity(sortParams(parameters), StandardCharsets.UTF_8);
            //设置超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(180000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (null == httpEntity) {
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            if (HttpServletResponse.SC_OK != statusCode) {
                log.warn("亚马逊api请求出错状态码不是200,状态码：" + statusCode + "---详情:" + result);
                return null;
            }
            EntityUtils.consume(httpEntity);
            return result;
        } catch (Exception e) {
            log.warn("亚马逊api请求出错" + e);
            return null;
        }
    }




}
