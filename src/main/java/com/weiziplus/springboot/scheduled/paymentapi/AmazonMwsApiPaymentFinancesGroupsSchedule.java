package com.weiziplus.springboot.scheduled.paymentapi;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.InventoryAgeMapper;
import com.weiziplus.springboot.mapper.payment.payMentMapper;
import com.weiziplus.springboot.mapper.scheduled.*;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.scheduled.mwsapi.AmazonMwsApiBaseSchedule;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.UuidUtils;
import com.weiziplus.springboot.utils.amazon.AmazonMwsUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.util.*;

import static com.weiziplus.springboot.utils.amazon.AmazonMwsApi.getPaymentReport;

@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonMwsApiPaymentFinancesGroupsSchedule extends AmazonMwsApiBaseSchedule {
    @Autowired
    InventoryAgeMapper inventoryAgeMapper;

    @Autowired
    EveryDayInventoryRecordsMapper everyDayInventoryRecordsMapper;

    @Autowired
    FbaCustomerReturnsMapper fbaCustomerReturnsMapper;

    @Autowired
    InventoryMapper inventoryMapper;

    @Autowired
    RemovalOrderDetailMapper removalOrderDetailMapper;

    @Autowired
    RemovalShipmentDetailMapper removalShipmentDetailMapper;

    @Autowired
    CompleteOrderMapper completeOrderMapper;

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    payMentMapper payMentMapper;

    /**
     * 通用逻辑处理
     *
     * @param
     */
//    @Scheduled(cron = "0 06 19 * * ?")
//    @Transactional(rollbackFor = Exception.class)
    public void Payment() {
        baseHandle(TASK_NAME[15], 0);
    }



    protected void baseHandle(String taskName, int type) {
        AmazonMwsApiPaymentFinancesGroupsSchedule.log.info("***********获取------" + taskName + "-----定时任务开始**********");
        List<Shop> shopList = shopMapper.getAllList();
        try {
            for (Shop shop : shopList) {
                Map<String, Object> map = beginScheduleTaskValidate(taskName, shop);
                if (null == map) {
                    AmazonMwsApiPaymentFinancesGroupsSchedule.log.warn("×××××" + taskName + "×××定时任务出错××××××××××××××××××");
                    return;
                }
                Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
                String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
                String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
                String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
                String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
                List<Area> areaList = (List<Area>) map.get("area");
                Map<String, String> params = new HashMap<>(3);
                int errorNum = 0;
                StringBuffer errorMsg = new StringBuffer();
                for (int index = 0; index < areaList.size(); index++) {
                    Area area = areaList.get(index);
//                    创建事务还原点
                    Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                    try {
                        params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
                        params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
                        params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
                        if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                            params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
                        }
//                      requestType 表示不同的财务事件     ListFinancialEventGroups
                        Map<String, Object> requestReport = (Map<String, Object>)getPaymentReport(secretKey, params, "ListFinancialEvents","");
                        if (null == requestReport) {
                            //回滚事务
                            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                            errorNum++;
                            errorMsg.append("第").append(errorNum).append("次出错,详情请看日志信息");
                            //如果错误次数超出最大次数
                            if (errorNum >= MAX_ERROR_NUM) {
                                logWarnErrorMsg(taskName, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode(), errorMsg);
                                errorNum = 0;
                                errorMsg = new StringBuffer();
                            } else {
                                //没有的话重新本次请求
                                index--;
                                Thread.sleep((long) Math.pow(2, errorNum + 1));
                            }
                            continue;
                        }
                        handleListFinancialEventGroups(requestReport);
                        errorNum = 0;
                        errorMsg = new StringBuffer();
                    } catch (Exception e) {
//                        回滚事务
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        errorNum++;
                        errorMsg.append("第").append(errorNum).append("次出错,详情:").append(e);
                        //如果错误次数超出最大次数
                        if (errorNum >= MAX_ERROR_NUM) {
                            logWarnErrorMsg(taskName, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode(), errorMsg);
                            errorNum = 0;
                            errorMsg = new StringBuffer();
                        } else {
                            //没有的话重新本次请求
                            index--;
                            Thread.sleep((long) Math.pow(2, errorNum + 1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            AmazonMwsApiPaymentFinancesGroupsSchedule.log.warn("×××××" + taskName + "×××定时任务出错××××××××××详情:" + e);
        } finally {
            AmazonMwsApiPaymentFinancesGroupsSchedule.log.info("***********获取------" + taskName + "-----定时任务结束**********");
        }
    }

//   财务事务组事件解析成对应的实体
//    2019 11 22

    private int handleListFinancialEventGroups(Map<String, Object> requestReport) {

        Map<String, Object> map1 = (Map<String, Object>) requestReport.get("ListFinancialEventGroupsResponse");
        Map<String, Object> ResponseMetadata = (Map<String, Object>) requestReport.get("ResponseMetadata");
        Map<String, Object> map2 = (Map<String, Object>) map1.get("ListFinancialEventGroupsResult");
        Map<String, Object> map4 = (Map<String, Object>) map2.get("FinancialEventGroupList");
        Map<String, Object> map3 = null;
        Object map5 = map4.get("FinancialEventGroup");
        List<Object> arrayList = new ArrayList<>();
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{

                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayListfinancialeventgroups ts = new PayListfinancialeventgroups();
                    Map<String, Object> OriginalTotal1 = (Map<String, Object>) ls.get("OriginalTotal");
                    Map<String, Object> BeginningBalance1 = (Map<String, Object>) ls.get("BeginningBalance");
                    ts.setFinancialEventGroupId(String.valueOf(ls.get("FinancialEventGroupId")));
                    ts.setFinancialEventGroupStart(String.valueOf( ls.get("FinancialEventGroupStart")));
                    ts.setProcessingStatus(String.valueOf( ls.get("ProcessingStatus")));
                    ts.setOriginalTotalCurrencyAmount(String.valueOf(OriginalTotal1.get("CurrencyAmount")));
                    ts.setOriginalTotalCurrencyCode(String.valueOf( OriginalTotal1.get("CurrencyCode")));
                    ts.setBeginningBalanceCurrencyAmount(String.valueOf(BeginningBalance1.get("CurrencyAmount")));
                    ts.setBeginningBalanceCurrencyCode(String.valueOf( BeginningBalance1.get("CurrencyCode")));
//                  ts.setRequestId(String.valueOf( ResponseMetadata.get("RequestId"));
                    arrayList.add(ts);
                }
                if (judge(map3))
                    baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        if (judge(map3))
        {
            PayListfinancialeventgroups ts = new PayListfinancialeventgroups();
            Map<String, Object> OriginalTotal1 = (Map<String, Object>) map3.get("OriginalTotal");
            Map<String, Object> BeginningBalance1 = (Map<String, Object>) map3.get("BeginningBalance");
            ts.setFinancialEventGroupId(String.valueOf(map3.get("FinancialEventGroupId")));
            ts.setFinancialEventGroupStart(String.valueOf( map3.get("FinancialEventGroupStart")));
            ts.setProcessingStatus(String.valueOf( map3.get("ProcessingStatus")));
            ts.setOriginalTotalCurrencyAmount(String.valueOf(OriginalTotal1.get("CurrencyAmount")));
            ts.setOriginalTotalCurrencyCode(String.valueOf( OriginalTotal1.get("CurrencyCode")));
            ts.setBeginningBalanceCurrencyAmount(String.valueOf(BeginningBalance1.get("CurrencyAmount")));
            ts.setBeginningBalanceCurrencyCode(String.valueOf( BeginningBalance1.get("CurrencyCode")));
            baseInsert(ts);
        }


        return 0;
    }


    private Boolean judge(Map<String, Object> map3){
        String FinancialStart = map3.get("FinancialEventGroupStart").toString();
        //获取数据库里面最新的日期
        String latestDay;
        latestDay = payMentMapper.getLatestDay();
        if (!StringUtil.isBlank(latestDay)) {
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            try {
                if (DateUtil.compateTime(latestDay,FinancialStart) >= 0) {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }




}