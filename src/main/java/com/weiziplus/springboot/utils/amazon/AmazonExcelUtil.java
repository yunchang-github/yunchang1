package com.weiziplus.springboot.utils.amazon;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wanglongwei
 * @data 2019/8/8 10:33
 */
@Slf4j
public class AmazonExcelUtil {

    /**
     * 获取excel的sheet
     *
     * @param url
     * @return
     */
    public static Sheet getExcelSheet(String url) {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(180000).setConnectTimeout(180000).setSocketTimeout(180000).build()).build();
        try {
            HttpGet get = new HttpGet(url);
            get.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            get.setHeader("Accept", "Accept: text/plain, */*");
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
            get.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
            get.addHeader("X-Requested-With", "XMLHttpRequest");
            CloseableHttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpServletResponse.SC_OK != statusCode) {
                log.warn("状态码不是200,状态码：" + statusCode + "---请求地址url:" + url + "---详情:" + JSON.toJSONString(response));
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                log.warn("获取的内容为空");
                return null;
            }
            Workbook workbook = new XSSFWorkbook(entity.getContent());
            EntityUtils.consume(entity);
            //默认只有一个sheet
            return workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
