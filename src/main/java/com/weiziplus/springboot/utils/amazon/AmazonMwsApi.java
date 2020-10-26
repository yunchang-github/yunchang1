package com.weiziplus.springboot.utils.amazon;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiziplus.springboot.scheduled.mwsapi.AmazonMwsApiSchedule;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import lombok.extern.slf4j.Slf4j;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weiziplus.springboot.utils.DateUtil.*;

/**
 * @author wanglongwei
 * @data 2019/7/15 11:54
 */
@Slf4j
public class AmazonMwsApi {

    private static final String SUCCESS_GET_REPORT_LIST_RESPONSE = "GetReportListResponse";
    //public static String FINANCIALEVENTGROUPSTARTEDBEFORE ="FinancialEventGroupStartedBefore";

    private static String FINANCIALEVENTGROUPSTARTEDAFTER ="FinancialEventGroupStartedAfter";



    public static List<List<String>> getReport(String mwsUrl, String secretKey, Map<String, String> baseParams, String requestType) {
        return getReport(mwsUrl, secretKey, baseParams, requestType, null);
    }

    public static List<List<String>> getReport(String mwsUrl, String secretKey, Map<String, String> baseParams, String requestType, Map<String, String> otherParams) {
        if (StringUtil.isBlank(mwsUrl)) {
            log.warn("mwsUrl不能为空");
            return null;
        }
        if (StringUtil.isBlank(secretKey)) {
            log.warn("secretKey不能为空");
            return null;
        }
        if (null == baseParams) {
            log.warn("baseParams不能为空");
            return null;
        }
        if (StringUtil.isBlank(requestType)) {
            log.warn("requestType不能为空");
            return null;
        }
        baseParams.put("ReportType", requestType);
        if (null != otherParams) {
            baseParams.putAll(otherParams);
        }
        String ReportRequestId = null;

        //发送RequestReport请求
        Map<String, Object> requestReport = AmazonMwsUntil.clientXML("RequestReport", mwsUrl, secretKey, baseParams);
        if (null == requestReport || null != requestReport.get(AmazonMwsUntil.ERROR_RESPONSE)) {
            log.warn("获取Report请求出错：requestReport" + requestType + "---requestReport详情:" + requestReport);
            return null;
        }
        else {
            Map<String, Object> RequestReportResponse = (Map<String, Object>) requestReport.get("RequestReportResponse");
            Map<String, Object> RequestReportResult = (Map<String, Object>) RequestReportResponse.get("RequestReportResult");
            Map<String, Object> ReportRequestInfo = (Map<String, Object>) RequestReportResult.get("ReportRequestInfo");
            ReportRequestId = String.valueOf(ReportRequestInfo.get("ReportRequestId"));
        }
        //去掉多余参数防止签名计算错误
        baseParams.remove(AmazonMwsApiSchedule.PUBLIC_PARAM_MARKETPLACE_ID_LIST_ID);
        baseParams.remove("ReportType");
        baseParams.put("ReportTypeList.Type.1", AmazonMwsUntil.urlEncode(requestType));
        if (null != otherParams) {
            for (String key : otherParams.keySet()) {
                baseParams.remove(key);
            }
        }
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int j = 0;
        Boolean work = true;
        while(work) {
            //发送GetReportList请求
            Map<String, Object> getReportList = AmazonMwsUntil.clientXML("GetReportList", mwsUrl, secretKey, baseParams);
            if (null == getReportList || null != getReportList.get(AmazonMwsUntil.ERROR_RESPONSE)) {
                log.warn("获取Report请求出错getReportList" + getReportList);
                return null;
            }
            if (null == getReportList.get(SUCCESS_GET_REPORT_LIST_RESPONSE)) {
                log.warn("获取Report请求出错getReportList:GetReportListResponse" + getReportList);
                return null;
            }
            //去掉多余参数防止签名计算错误
            baseParams.remove("ReportTypeList.Type.1");
            Map<String, Object> map1 = (Map<String, Object>) getReportList.get(SUCCESS_GET_REPORT_LIST_RESPONSE);
            String getReportListResult = "GetReportListResult";
            if (null == map1.get(getReportListResult)) {
                log.warn("获取Report请求出错getReportList:GetReportListResponse:GetReportListResult" + getReportList);
                return null;
            }
            Map<String, Object> map2 = (Map<String, Object>) map1.get(getReportListResult);
            String reportInfo = "ReportInfo";
            //获取reportInfo对象，-----亚马逊当返回reportInfo只有一个的时候为map，多个的时候为数组
            Object reportInfoObject = map2.get(reportInfo);
            if (null == reportInfoObject) {
                log.warn("获取Report请求出错getReportList:GetReportListResponse:GetReportListResult:ReportInfo" + getReportList);
                return null;
            }
            Map<String, Object> map3;
            if (reportInfoObject.getClass().isArray()
                    || reportInfoObject instanceof JSONArray
                    || reportInfoObject instanceof JSON) {
                if (reportInfoObject instanceof JSONObject) {
                    map3 = (Map<String, Object>) reportInfoObject;
                } else {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) reportInfoObject;
                    String reportId = null;
                    String ReportRequestId1 = null;
                    for (int i = 0; i < list.size(); i++) {
                        map3 = list.get(i);
                        ReportRequestId1 = String.valueOf(map3.get("ReportRequestId"));
                        if (ReportRequestId.equals(ReportRequestId1)) {
                            //发送GetReport请求
                            reportId = String.valueOf(map3.get("ReportId"));
                            baseParams.put("ReportId", reportId);
                            return AmazonMwsUntil.clientTXT("GetReport", mwsUrl, secretKey, baseParams);
                        }
                    }
                    j++;
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (j > 2) {
                        work = false;
                    }
                }
            } else {
                map3 = (Map<String, Object>) reportInfoObject;
            }
        }
       return null;
    }

    public static List<List<String>> getReport2(String mwsUrl, String secretKey, Map<String, String> baseParams, String requestType, Map<String, String> otherParams) {
        if (StringUtil.isBlank(mwsUrl)) {
            log.warn("mwsUrl不能为空");
            return null;
        }
        if (StringUtil.isBlank(secretKey)) {
            log.warn("secretKey不能为空");
            return null;
        }
        if (null == baseParams) {
            log.warn("baseParams不能为空");
            return null;
        }
        if (StringUtil.isBlank(requestType)) {
            log.warn("requestType不能为空");
            return null;
        }
        baseParams.put("ReportType", requestType);
        if (null != otherParams) {
            baseParams.putAll(otherParams);
        }
        //发送RequestReport请求
        Map<String, Object> requestReport = AmazonMwsUntil.clientXML("RequestReport", mwsUrl, secretKey, baseParams);
        if (null == requestReport || null != requestReport.get(AmazonMwsUntil.ERROR_RESPONSE)) {
            log.warn("获取Report请求出错：requestReport" + requestType + "---requestReport详情:" + requestReport);
            return null;
        }
        //去掉多余参数防止签名计算错误
        baseParams.remove(AmazonMwsApiSchedule.PUBLIC_PARAM_MARKETPLACE_ID_LIST_ID);
        baseParams.remove("ReportType");
        baseParams.put("ReportTypeList.Type.1", AmazonMwsUntil.urlEncode(requestType));
        if (null != otherParams) {
            for (String key : otherParams.keySet()) {
                baseParams.remove(key);
            }
        }
        //发送GetReportList请求
        Map<String, Object> getReportList = AmazonMwsUntil.clientXML("GetReportList", mwsUrl, secretKey, baseParams);
        if (null == getReportList || null != getReportList.get(AmazonMwsUntil.ERROR_RESPONSE)) {
            log.warn("获取Report请求出错getReportList" + getReportList);
            return null;
        }
        if (null == getReportList.get(SUCCESS_GET_REPORT_LIST_RESPONSE)) {
            log.warn("获取Report请求出错getReportList:GetReportListResponse" + getReportList);
            return null;
        }
        //去掉多余参数防止签名计算错误
        baseParams.remove("ReportTypeList.Type.1");
        Map<String, Object> map1 = (Map<String, Object>) getReportList.get(SUCCESS_GET_REPORT_LIST_RESPONSE);
        String getReportListResult = "GetReportListResult";
        if (null == map1.get(getReportListResult)) {
            log.warn("获取Report请求出错getReportList:GetReportListResponse:GetReportListResult" + getReportList);
            return null;
        }
        Map<String, Object> map2 = (Map<String, Object>) map1.get(getReportListResult);
        String reportInfo = "ReportInfo";
        //获取reportInfo对象，-----亚马逊当返回reportInfo只有一个的时候为map，多个的时候为数组
        Object reportInfoObject = map2.get(reportInfo);
        if (null == reportInfoObject) {
            log.warn("获取Report请求出错getReportList:GetReportListResponse:GetReportListResult:ReportInfo" + getReportList);
            return null;
        }
        Map<String, Object> map3;
        if (reportInfoObject.getClass().isArray()
                || reportInfoObject instanceof JSONArray
                || reportInfoObject instanceof JSON) {
          if(reportInfoObject instanceof JSONObject){
              map3 = (Map<String, Object>) reportInfoObject;
          }else{
              List<Map<String, Object>> list = (List<Map<String, Object>>) reportInfoObject;
              map3 = list.get(0);
          }
        } else {
            map3 = (Map<String, Object>) reportInfoObject;
        }
        String reportId = String.valueOf(map3.get("ReportId"));
        //发送GetReport请求
        baseParams.put("ReportId", reportId);
        return AmazonMwsUntil.clientTXT("GetReport", mwsUrl, secretKey, baseParams);
    }

    // payment 2019 11 22于payment返回数据

    public static Object getPaymentReport(String secretKey, Map<String, String> baseParams, String requestType,String nextToken) {
        if (StringUtil.isBlank(secretKey)) {
            log.warn("secretKey不能为空");
            return null;
        }
        if (null == baseParams) {
            log.warn("baseParams不能为空");
            return null;
        }
        if(null == baseParams){
            log.warn("baseParams不能为空");
            return null;
        }
        Map<String, Object> map=new HashMap<>();
        if(requestType.equals("ListFinancialEventGroups")){
            baseParams.put(FINANCIALEVENTGROUPSTARTEDAFTER,AmazonMwsUntil.urlEncode(getISO8601Timestamp("+8")));
            map=AmazonMwsUntil.paymentClientXML(requestType, "https://mws.amazonservices.com/Finances/2015-05-01", secretKey, baseParams,"Finances/2015-05-01");
            baseParams.remove(FINANCIALEVENTGROUPSTARTEDAFTER);
            return map;
        }
        if(requestType.equals("ListFinancialEvents")) {
            baseParams.put("PostedAfter",AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-180)))));
            baseParams.put("PostedBefore",AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-1)))));
            map=AmazonMwsUntil.paymentClientXML(requestType, "https://mws.amazonservices.com/Finances/2015-05-01", secretKey, baseParams,"Finances/2015-05-01");
            baseParams.remove("PostedAfter");
            baseParams.remove("PostedBefore");
            return map;
        }
        if(requestType.equals("ListFinancialEventsByNextToken")) {
            baseParams.put("NextToken",AmazonMwsUntil.urlEncode(nextToken));
            Map<String,Object>result=AmazonMwsUntil.paymentClientXML(requestType, "https://mws.amazonservices.com/Finances/2015-05-01", secretKey, baseParams,"Finances/2015-05-01");
            baseParams.remove("NextToken");
            return result;
        }
        return 0;
    }




}
