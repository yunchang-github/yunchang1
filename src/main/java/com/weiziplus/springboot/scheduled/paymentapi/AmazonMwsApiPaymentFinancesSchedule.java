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
import com.weiziplus.springboot.utils.UuidUtils;
import com.weiziplus.springboot.utils.amazon.AmazonMwsUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;


import static com.weiziplus.springboot.utils.amazon.AmazonMwsApi.getPaymentReport;

@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonMwsApiPaymentFinancesSchedule extends AmazonMwsApiBaseSchedule {
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
    public void Payment() {
        baseHandle(TASK_NAME[15], 0);
    }

    protected void baseHandle(String taskName, int type) {
        AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取------" + taskName + "-----定时任务开始**********");
        List<Shop> shopList = shopMapper.getAllList();
        try {
            for (Shop shop : shopList) {
                Map<String, Object> map = beginScheduleTaskValidate(taskName, shop);
                if (null == map) {
                    AmazonMwsApiPaymentFinancesSchedule.log.warn("×××××" + taskName + "×××定时任务出错××××××××××××××××××");
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
                    //创建事务还原点
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
                        String nextToken = FinancialEvents(requestReport,"ListFinancialEventsResponse",String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
                        if(null!=nextToken){
                            ListFinancialEventsByNextToken(secretKey,params,nextToken, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode()); //处理token数据
                        }
                        errorNum = 0;
                        errorMsg = new StringBuffer();
                    } catch (Exception e) {
                        //回滚事务
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
            AmazonMwsApiPaymentFinancesSchedule.log.warn("×××××" + taskName + "×××定时任务出错××××××××××详情:" + e);
        } finally {
            AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取------" + taskName + "-----定时任务结束**********");
        }
    }




    //处理nexttoken数据
    private  void ListFinancialEventsByNextToken(String secretKey,Map<String,String>baseParams,String nexttoken,String shopname,String area){
        Boolean flag = true;
        while (flag){
            int times=1;
            times++;
            Map<String,Object>result= (Map<String, Object>)getPaymentReport(secretKey, baseParams, "ListFinancialEventsByNextToken",nexttoken);
            Map<String,Object>result1 = (Map<String, Object>) result.get("ListFinancialEventsByNextTokenResponse");
            Map<String,Object>result2 = (Map<String, Object>) result1.get("ListFinancialEventsByNextTokenResult");
            AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取token次数------" + "" );
            nexttoken=FinancialEvents(result,"ListFinancialEventsByNextTokenResponse",shopname,area);
            if(null== nexttoken){
                flag = false;
            }
        }
    }


//处理财务nexttoken逻辑
private String FinancialEvents(Map<String, Object> requestReport, String ListFinancialEventsType,String shopname,String area){
    Map<String, Object> map1 = (Map<String, Object>) requestReport.get(ListFinancialEventsType);
    Map<String, Object> map2 = new HashMap<>();
    Map<String, Object> map3 =new HashMap<>();
    if(ListFinancialEventsType.equals("ListFinancialEventsByNextTokenResponse")){
        map2 = (Map<String, Object>) map1.get("ListFinancialEventsByNextTokenResult");
        map3 = (Map<String, Object>) map2.get("FinancialEvents");
        financialData(map3,shopname,area);
        if(null!=map2.get("NextToken")){
            return String.valueOf( map2.get("NextToken"));
        }
    }
    else {
        map2 = (Map<String, Object>) map1.get("ListFinancialEventsResult");
        map3 = (Map<String, Object>) map2.get("FinancialEvents");
        financialData(map3,shopname,area);
        if(null!=map2.get("NextToken")){
            return String.valueOf( map2.get("NextToken"));
        }
    }
    return null;
}

//解析财务数据
private void financialData(Map<String,Object>map3,String shopname,String area){
        if (null != map3.get("ShipmentEventList")) {
            if (!(map3.get("ShipmentEventList") instanceof String)) {
                Map<String, Object> param = (Map<String, Object>) map3.get("ShipmentEventList");
                param.put("DataType", "ShipmentEventList");
                param.put("ShopName",shopname);
                param.put("Area",area);
                handleShipmentEvent(param);
            }
        }

    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----ShipmentEventList-"  + "-----**********");
    if (null != map3.get("RefundEventList")) {
        if(!(map3.get("RefundEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("RefundEventList");
            param.put("DataType", "RefundEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleShipmentEvent(param);
        }
    }

    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----RefundEventList-"  + "-----**********");
    if (null != map3.get("GuaranteeClaimEventList")) {
        if(!(map3.get("GuaranteeClaimEventList")instanceof String)){
            Map<String, Object> param = (Map<String, Object>) map3.get("GuaranteeClaimEventList");
            param.put("DataType", "GuaranteeClaimEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleShipmentEvent(param);
        }

    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----GuaranteeClaimEventList-"  + "-----**********");
    if (null != map3.get("ChargebackEventList")) {
        if(!(map3.get("ChargebackEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("ChargebackEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            param.put("DataType", "ChargebackEventList");
            handleShipmentEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----ChargebackEventList-"  + "-----**********");
    if (null != map3.get("PayWithAmazonEventList")) {
        if(!(map3.get("PayWithAmazonEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("PayWithAmazonEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handlePayWithAmazonEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----PayWithAmazonEventList-"  + "-----**********");
    if (null != map3.get("ServiceProviderCreditEventList")) {
        if(!(map3.get("ServiceProviderCreditEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("ServiceProviderCreditEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleSolutionProviderCreditEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----ServiceProviderCreditEventList-"  + "-----**********");
    if (null != map3.get("RetrochargeEventList")) {
        if(!(map3.get("RetrochargeEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("RetrochargeEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleRetrochargeEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----RetrochargeEventList-"  + "-----**********");
    if (null != map3.get("RentalTransactionEventList")) {
        if(!(map3.get("RentalTransactionEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("RentalTransactionEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleRentalTransactionEvent(param);
        }


    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----RentalTransactionEventList-"  + "-----**********");
    if (null != map3.get("ProductAdsPaymentEventList")) {
        if(!(map3.get("ProductAdsPaymentEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("ProductAdsPaymentEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleProductAdsPaymentEvent(param);
        }


    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----ProductAdsPaymentEventList-"  + "-----**********");
    if (null != map3.get("ServiceFeeEventList")) {
        if(!(map3.get("ServiceFeeEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("ServiceFeeEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleServiceFeeEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----ServiceFeeEventList-"  + "-----**********");
    if (null != map3.get("DebtRecoveryEventList")) {
        if(!(map3.get("DebtRecoveryEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("DebtRecoveryEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleDebtRecoveryEvent(param);
        }

    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----DebtRecoveryEventList-"  + "-----**********");
    if (null != map3.get("LoanServicingEventList")) {
        if(!(map3.get("LoanServicingEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("LoanServicingEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleLoanServicingEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----LoanServicingEventList-"  + "-----**********");
    if (null != map3.get("AdjustmentEventList")) {
        if(!(map3.get("AdjustmentEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("AdjustmentEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleAdjustmentEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----AdjustmentEventList-"  + "-----**********");
     if (null != map3.get("CouponPaymentEventList")) {
        if(!(map3.get("CouponPaymentEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("CouponPaymentEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleCouponPaymentEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----CouponPaymentEventList-"  + "-----**********");
     if (null != map3.get("SAFETReimbursementEventList")) {
        if(!(map3.get("SAFETReimbursementEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("SAFETReimbursementEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleSAFETReimbursementEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----SAFETReimbursementEventList-"  + "-----**********");
    if (null != map3.get("SellerReviewEnrollmentPaymentEventList")) {

        if(!(map3.get("SellerReviewEnrollmentPaymentEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("SellerReviewEnrollmentPaymentEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleSellerReviewEnrollmentPaymentEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----SellerReviewEnrollmentPaymentEventList-"  + "-----**********");
    if (null != map3.get("FBALiquidationEventList")) {

        if(!(map3.get("FBALiquidationEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("FBALiquidationEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleFBALiquidationEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----FBALiquidationEventList-"  + "-----**********");
    if (null != map3.get("ImagingServicesFeeEventList")) {

        if(!(map3.get("ImagingServicesFeeEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("ImagingServicesFeeEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleImagingServicesFeeEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----ImagingServicesFeeEventList-3"  + "-----**********");
    if (null != map3.get("AffordabilityExpenseEventList")) {

        if(!(map3.get("AffordabilityExpenseEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("AffordabilityExpenseEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleAffordabilityExpenseEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----AffordabilityExpenseEventList-3"  + "-----**********");

    if (null != map3.get("AffordabilityExpenseReversalEventList")) {

        if(!(map3.get("AffordabilityExpenseReversalEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("AffordabilityExpenseReversalEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleAffordabilityExpenseReversalEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----AffordabilityExpenseReversalEventList-3"  + "-----**********");
    if (null != map3.get("NetworkComminglingTransactionEventList")) {

        if(!(map3.get("NetworkComminglingTransactionEventList")instanceof String)) {
            Map<String, Object> param = (Map<String, Object>) map3.get("NetworkComminglingTransactionEventList");
            param.put("ShopName",shopname);
            param.put("Area",area);
            handleNetworkComminglingTransactionEvent(param);
        }
    }
    AmazonMwsApiPaymentFinancesSchedule.log.info("***********获取-----NetworkComminglingTransactionEventList-3"  + "-----**********");
}








    /*2019 11 22

* 解析财务事件数据
ServiceFeeEventList 事件处理报表

* */

    private int handleServiceFeeEvent(Map<String, Object> map) {
        Object map5 = map.get("ServiceFeeEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayServicefeeevent payServicefeeevent;
                    payServicefeeevent = ServiceFeeEventList(ls,"",shopname,area);
                    arrayList.add(payServicefeeevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayServicefeeevent payServicefeeevent;
        payServicefeeevent = ServiceFeeEventList(map3,"",shopname,area);
        baseInsert(payServicefeeevent);
        return 0;
    }

//解析过程

    public PayServicefeeevent ServiceFeeEventList(Map<String,Object>ls,String datatype,String shop,String area){
        PayServicefeeevent payServicefeeevent = new PayServicefeeevent();
        String uuid = UuidUtils.buildUuid();
        payServicefeeevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payServicefeeevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("FeeReason")){
            payServicefeeevent.setFeereason(String.valueOf( ls.get("FeeReason")));
        }
        if (null!=ls.get("SellerSKU")){
            payServicefeeevent.setSellersku(String.valueOf( ls.get("SellerSKU")));
        }
        if (null!=ls.get("FnSKU")){
            payServicefeeevent.setFnsku(String.valueOf( ls.get("FnSKU")));
        }
        if (null!=ls.get("FeeDescription")){
            payServicefeeevent.setFeedescription(String.valueOf( ls.get("FeeDescription")));
        }
        if (null!=ls.get("ASIN")){
            payServicefeeevent.setAsin(String.valueOf( ls.get("ASIN")));
        }
        if (null!=ls.get("AmazonOrderId")){
            payServicefeeevent.setAmazonorderid(String.valueOf( ls.get("AmazonOrderId")));
        }
        if (null!=shop){
            payServicefeeevent.setShop(shop);
        }
        if (null!=area){
            payServicefeeevent.setArea(area);
        }
        if (null!=ls.get("FeeList")){
            Map<String, Object> params= (Map<String, Object>) ls.get("FeeList");
            params.put("DataType","ServiceFeeEvent-FeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }

        return payServicefeeevent;
    }





    /*2019 11 25

* 解析财务事件数据
feecomponent 事件处理报表

* */
    private int handleFeeComponent(Map<String, Object> map) {
        Object map5 = map.get("FeeComponent");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayFeecomponent payFeecomponent;
                    payFeecomponent= FeeComponent(ls,datatype,ObjectId);
                    arrayList.add(payFeecomponent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayFeecomponent payFeecomponent;
        payFeecomponent= FeeComponent(map3,datatype,ObjectId);
        baseInsert(payFeecomponent);
        return 0;
    }
    //解析数据过程
    public PayFeecomponent FeeComponent(Map<String,Object>ls,String datatype,String ObjectId){
        PayFeecomponent payFeecomponent = new PayFeecomponent();
        String uuid = UuidUtils.buildUuid();
        payFeecomponent.setId(uuid);
        if (null!=ObjectId){
            payFeecomponent.setObjectid(ObjectId);
        }
        if (null!=ls.get("FeeAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("FeeAmount");
            payFeecomponent.setCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payFeecomponent.setCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("FeeType")){
            payFeecomponent.setFeetype(String.valueOf( ls.get("FeeType")));
        }
        payFeecomponent.setDatatype(datatype);
        return payFeecomponent;

    }



   /*2019 11 25

* 解析财务事件数据
DirectPayment 事件处理报表

* */

    private int handleDirectPayment(Map<String, Object> map) {
        Object map5 = map.get("DirectPayment");
        String datatype = String.valueOf( map.get("DataType"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayDirectpayment payDirectpayment;
                    payDirectpayment=DirectPayment(ls,datatype);
                    arrayList.add(payDirectpayment);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayDirectpayment payDirectpayment;
        payDirectpayment=DirectPayment(map3,datatype);
        baseInsert(payDirectpayment);
        return 0;
    }

    //解析数据过程
    public PayDirectpayment DirectPayment(Map<String,Object>ls,String datatype) {
        PayDirectpayment payDirectpayment = new PayDirectpayment();
        if (null!=ls.get("DirectPaymentAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("DirectPaymentAmount");
            payDirectpayment.setCurrencyamount(String.valueOf(params.get("CurrencyAmount")) );
            payDirectpayment.setCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("DirectPaymentType")){
            payDirectpayment.setDirectpaymenttype(String.valueOf( ls.get("DirectPaymentType")));
        }
        payDirectpayment.setDatatype(datatype);
        return payDirectpayment;
    }


     /*2019 11 25

* 解析财务事件数据
ChargeInstrument  事件处理报表

* */

    private int handleChargeInstrument(Map<String, Object> map) {
        Object map5 = map.get("ChargeInstrument");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayChargeinstrument payChargeinstrument;
                    payChargeinstrument = ChargeInstrument(ls,datatype,ObjectId);
                    arrayList.add(payChargeinstrument);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayChargeinstrument payChargeinstrument;
        payChargeinstrument = ChargeInstrument(map3,datatype,ObjectId);
        baseInsert(payChargeinstrument);
        return 0;
    }


    //解析数据过程
    public PayChargeinstrument ChargeInstrument(Map<String,Object>ls,String datatype,String ObjectId) {
        PayChargeinstrument payChargeinstrument = new PayChargeinstrument();
        String uuid = UuidUtils.buildUuid();
        payChargeinstrument.setId(uuid);

        if (null!=ObjectId){
            payChargeinstrument.setObjectid(ObjectId);
        }
        if (null!=ls.get("Amount")){
            Map<String, String> params = (Map<String, String>) ls.get("Amount");
            payChargeinstrument.setAmountCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payChargeinstrument.setAmountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("Tail")){
            payChargeinstrument.setTail(String.valueOf( ls.get("Tail")));
        }
        if (null!=ls.get("Description")){
            payChargeinstrument.setDescription(String.valueOf( ls.get("Description")));
        }
        payChargeinstrument.setDatatype(datatype);
        return payChargeinstrument;
    }


    /*2019 11 25

* 解析财务事件数据
Pay_AdjustmentItem  事件处理报表

* */

    private int handleAdjustmentItem(Map<String, Object> map) {
        Object map5 = map.get("AdjustmentItem");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayAdjustmentitem payAdjustmentitem;
                    payAdjustmentitem=AdjustmentItem(ls,datatype,ObjectId);
                    arrayList.add(payAdjustmentitem);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayAdjustmentitem payAdjustmentitem;
        payAdjustmentitem=AdjustmentItem(map3,datatype,ObjectId);
        baseInsert(payAdjustmentitem);
        return 0;
    }



    //解析数据过程
    public PayAdjustmentitem AdjustmentItem(Map<String,Object>ls,String datatype,String ObjectId) {
        PayAdjustmentitem payAdjustmentitem = new PayAdjustmentitem();
        String uuid = UuidUtils.buildUuid();
        payAdjustmentitem.setId(uuid);
        if (null!=ObjectId){
            payAdjustmentitem.setObjectid(ObjectId);
        }
        if (null!=ls.get("PerUnitAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("PerUnitAmount");
            payAdjustmentitem.setPerunitamountCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAdjustmentitem.setPerunitamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TotalAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("TotalAmount");
            payAdjustmentitem.setTotalamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payAdjustmentitem.setTotalamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("Quantity")){
            payAdjustmentitem.setQuantity(String.valueOf( ls.get("Quantity")));
        }
        if (null!=ls.get("SellerSKU")){
            payAdjustmentitem.setSellersku(String.valueOf( ls.get("SellerSKU")));
        }
        if (null!=ls.get("FnSKU")){
            payAdjustmentitem.setFnsku(String.valueOf( ls.get("FnSKU")));
        }
        if (null!=ls.get("ProductDescription")){
            payAdjustmentitem.setFnsku(String.valueOf( ls.get("ProductDescription")));
        }
        if (null!=ls.get("ASIN")){
            payAdjustmentitem.setAsin(String.valueOf( ls.get("ASIN")));
        }
        payAdjustmentitem.setDatatype(datatype);
        return payAdjustmentitem;
    }




      /*2019 11 25

* 解析财务事件数据
ChargeComponent 事件处理报表

* */

    private int handleChargeComponent(Map<String, Object> map) {
        Object map5 = map.get("ChargeComponent");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayChargecomponent payChargecomponent;
                    payChargecomponent=ChargeComponent(ls,datatype,ObjectId);
                    arrayList.add(payChargecomponent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayChargecomponent payChargecomponent;
        payChargecomponent=ChargeComponent(map3,datatype,ObjectId);
        baseInsert(payChargecomponent);
        return 0;
    }


    //解析数据过程
    public PayChargecomponent ChargeComponent(Map<String,Object>ls,String datatype,String ObjectId) {
        PayChargecomponent payChargecomponent = new PayChargecomponent();
        String uuid = UuidUtils.buildUuid();
        payChargecomponent.setId(uuid);
        if (null!=ObjectId){
            payChargecomponent.setObjectid(ObjectId);
        }
        if (null!=ls.get("ChargeAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("ChargeAmount");
            payChargecomponent.setCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payChargecomponent.setCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("ChargeType")){
            payChargecomponent.setChargetype(String.valueOf( ls.get("ChargeType")));
        }
        payChargecomponent.setDatatype(datatype);
        return payChargecomponent;
    }






    /*2019 11 25

* 解析财务事件数据
ShipmentItem 事件处理报表

* */
    private int handleShipmentItem(Map<String, Object> map) {
        Object map5 = map.get("ShipmentItem");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayShipmentitem payShipmentitem;
                    payShipmentitem=Shipmentitem(ls,datatype,ObjectId);
                    arrayList.add(payShipmentitem);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayShipmentitem payShipmentitem;
        payShipmentitem=Shipmentitem(map3,datatype,ObjectId);
        baseInsert(payShipmentitem);
        return 0;
    }



    //解析数据过程
    public PayShipmentitem Shipmentitem(Map<String,Object>ls,String datatype,String ObjectId) {
        PayShipmentitem payShipmentitem = new PayShipmentitem();
        String uuid = UuidUtils.buildUuid();
        payShipmentitem.setId(uuid);
        if(null!=ObjectId){
            payShipmentitem.setObjectid(ObjectId);
        }
        if (null!=ls.get("CostOfPointsGranted")){
            Map<String, String> params = (Map<String, String>) ls.get("CostOfPointsGranted");
            payShipmentitem.setCostofpointsgrantedCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payShipmentitem.setCostofpointsgrantedCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("CostOfPointsReturned")){
            Map<String, String> params = (Map<String, String>) ls.get("CostOfPointsReturned");
            payShipmentitem.setCostofpointsreturnedCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payShipmentitem.setCostofpointsreturnedCurrencycode(String.valueOf( params.get("CurrencyCode")));
        }
        if (null!=ls.get("PromotionList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("PromotionList");
            params.put("DataType","PromotionList");
            params.put("ObjectId",uuid);
            handlePromotion(params);
        }
        if (null!=ls.get("PromotionAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("PromotionAdjustmentList");
            params.put("DataType","PromotionAdjustmentList");
            params.put("ObjectId",uuid);
            handlePromotion(params);
        }
        if (null!=ls.get("ItemChargeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ItemChargeList");
            params.put("DataType","ShipmentItem-ItemFeeList");
            params.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        if (null!=ls.get("ItemChargeAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ItemChargeAdjustmentList");
            params.put("DataType","ShipmentEvent-ItemChargeAdjustmentList");
            params.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        if (null!=ls.get("ItemFeeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ItemFeeList");
            params.put("DataType","ShipmentItem-ItemFeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("ItemFeeAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ItemFeeAdjustmentList");
            params.put("DataType","ShipmentItem-ItemFeeAdjustmentList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("ItemTaxWithheldList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ItemTaxWithheldList");
            Map<String, Object> params1 = (Map<String, Object>) params.get("TaxWithheldComponent");
            payShipmentitem.setTaxwithheldcomponentTaxcollectionmodel(String.valueOf( params1.get("TaxCollectionModel")));
            Map<String, Object> params2 = (Map<String, Object>) params1.get("TaxesWithheld");
            params2.put("DataType","ShipmentItem-TaxesWithheld");
            params2.put("ObjectId",uuid);
            handleChargeComponent(params2);
        }

        if (null!=ls.get("SellerSKU")){
            payShipmentitem.setSellersku(String.valueOf( ls.get("SellerSKU")));
        }
        if (null!=ls.get("OrderItemId")){
            payShipmentitem.setOrderitemid(String.valueOf(ls.get("OrderItemId")));
        }
        if (null!=ls.get("OrderAdjustmentItemId")){
            payShipmentitem.setOrderadjustmentitemid(String.valueOf( ls.get("OrderAdjustmentItemId")));
        }
        if (null!=ls.get("QuantityShipped")){
            payShipmentitem.setQuantityshipped(String.valueOf( ls.get("QuantityShipped")));
        }
        if(null!=datatype)
            payShipmentitem.setDatatype(datatype);
        return payShipmentitem;
    }






       /*2019 11 25

* 解析财务事件数据
ShipmentEvent 事件处理报表

* */

    private int handleShipmentEvent(Map<String, Object> map) {
        Object map5 = map.get("ShipmentEvent");
        String datatype = String.valueOf( map.get("DataType"));
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayShipmentevent payShipmentevent ;
                    payShipmentevent = Shipmentevent(ls,datatype,shopname,area);
                    arrayList.add(payShipmentevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayShipmentevent payShipmentevent ;
        payShipmentevent = Shipmentevent(map3,datatype,shopname,area);
        baseInsert(payShipmentevent);
        return 0;
    }

    //解析数据过程
    public PayShipmentevent Shipmentevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayShipmentevent payShipmentevent = new PayShipmentevent();
        String uuid = UuidUtils.buildUuid();
        payShipmentevent.setId(uuid);
        AmazonMwsApiPaymentFinancesSchedule.log.info("***********OrderChargeAdjustmentList1"  + "-----**********");
        if (null!=ls.get("OrderChargeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("OrderChargeList");
            params.put("DataType","ShipmentEvent-OrderChargeList");
            params.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        AmazonMwsApiPaymentFinancesSchedule.log.info("***********OrderChargeAdjustmentList2"  + "-----**********");
        if (null!=ls.get("OrderChargeAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("OrderChargeAdjustmentList");
            params.put("DataType","ShipmentEvent-OrderChargeAdjustmentList");
            params.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        AmazonMwsApiPaymentFinancesSchedule.log.info("***********OrderChargeAdjustmentList3"  + "-----**********");
        if (null!=ls.get("ShipmentFeeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ShipmentFeeList");
            params.put("DataType","ShipmentEvent-ShipmentFeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("ShipmentFeeAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ShipmentFeeAdjustmentList");
            params.put("DataType","ShipmentEvent-ShipmentFeeAdjustmentList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("OrderFeeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("OrderFeeList");
            params.put("DataType","ShipmentEvent-OrderFeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("OrderFeeAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("OrderFeeAdjustmentList");
            params.put("DataType","ShipmentEvent-OrderFeeAdjustmentList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("DirectPaymentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("DirectPaymentList");
            params.put("DataType","ShipmentEvent-DirectPaymentList");
            params.put("ObjectId",uuid);
            handleDirectPayment(params);
        }
        if (null!=ls.get("ShipmentItemList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ShipmentItemList");
            params.put("DataType","ShipmentEvent-ShipmentItemList");
            params.put("ObjectId",uuid);
            handleShipmentItem(params);
        }
        if (null!=ls.get("ShipmentItemAdjustmentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ShipmentItemAdjustmentList");
            params.put("DataType","ShipmentEvent-ShipmentItemAdjustmentList");
            params.put("ObjectId",uuid);
            handleShipmentItem(params);
        }
        if (null!=ls.get("AmazonOrderId")){
            payShipmentevent.setAmazonorderid(String.valueOf( ls.get("AmazonOrderId")));
        }
        if (null!=ls.get("SellerOrderId")){
            payShipmentevent.setSellerorderid(String.valueOf( ls.get("SellerOrderId")));
        }
        if (null!=ls.get("MarketplaceName")){
            payShipmentevent.setMarketplacename(String.valueOf( ls.get("MarketplaceName")));
        }
        if (null!=ls.get("PostedDate")){
            payShipmentevent.setPosteddate(String.valueOf(ls.get("PostedDate")));
        }
        if (null!=shop){
            payShipmentevent.setShop(shop);
        }
        if (null!=area){
            payShipmentevent.setArea(area);
        }
        if(null!=datatype)
            payShipmentevent.setDatatype(datatype);
        return payShipmentevent;
    }





       /*2019 11 25

* 解析财务事件数据
PayWithAmazonEvent 事件处理报表

* */

    private int handlePayWithAmazonEvent(Map<String, Object> map) {
        Object map5 = map.get("PayWithAmazonEvent");
        String datatype = String.valueOf( map.get("DataType"));
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayPaywithamazonevent payPaywithamazonevent;
                    payPaywithamazonevent = Paywithamazonevent(ls,datatype,shopname,area);
                    arrayList.add(payPaywithamazonevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayPaywithamazonevent payPaywithamazonevent;
        payPaywithamazonevent = Paywithamazonevent(map3,datatype,shopname,area);
        baseInsert(payPaywithamazonevent);
        return 0;
    }



    //解析数据过程
    public PayPaywithamazonevent Paywithamazonevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayPaywithamazonevent payPaywithamazonevent = new PayPaywithamazonevent();
        String uuid = UuidUtils.buildUuid();
        payPaywithamazonevent.setId(uuid);
        if (null!=ls.get("FeeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("FeeList");
            params.put("DataType","PayWithAmazonEvent-FeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("Charge")){
            Map<String, Object> params = (Map<String, Object>) ls.get("Charge");
            payPaywithamazonevent.setChargeChargetype(String.valueOf( params.get("ChargeType")));
            Map<String, Object> params1 = (Map<String, Object>) params.get("ChargeAmount");
            payPaywithamazonevent.setChargeCurrencyamount(String.valueOf( params1.get("CurrencyAmount")));
            payPaywithamazonevent.setChargeCurrencycode(String.valueOf( params1.get("CurrencyCode")));
        }

        if (null!=ls.get("SellerOrderId")){
            payPaywithamazonevent.setSellerorderid(String.valueOf( ls.get("SellerOrderId")));
        }
        if (null!=ls.get("BusinessObjectType")){
            payPaywithamazonevent.setBusinessobjecttype(String.valueOf( ls.get("BusinessObjectType")));
        }
        if (null!=ls.get("SalesChannel")){
            payPaywithamazonevent.setSaleschannel(String.valueOf( ls.get("SalesChannel")));
        }
        if (null!=ls.get("TransactionPostedDate")){
            payPaywithamazonevent.setTransactionposteddate(String.valueOf(ls.get("TransactionPostedDate")));
        }
        if (null!=ls.get("PaymentAmountType")){
            payPaywithamazonevent.setPaymentamounttype(String.valueOf( ls.get("PaymentAmountType")));
        }
        if (null!=ls.get("AmountDescription")){
            payPaywithamazonevent.setAmountdescription(String.valueOf( ls.get("AmountDescription")));
        }
        if (null!=ls.get("FulfillmentChannel")){
            payPaywithamazonevent.setFulfillmentchannel(String.valueOf( ls.get("FulfillmentChannel")));
        }
        if (null!=ls.get("StoreName")){
            payPaywithamazonevent.setStorename(String.valueOf( ls.get("StoreName")));
        }
        if (null!=shop){
            payPaywithamazonevent.setShop(shop);
        }
        if (null!=area){
            payPaywithamazonevent.setArea(area);
        }
        if(null!=datatype)
            payPaywithamazonevent.setDatatype(datatype);
        return payPaywithamazonevent;
    }





       /*2019 11 25

* 解析财务事件数据
SolutionProviderCreditEvent 事件处理报表

* */

    private int handleSolutionProviderCreditEvent(Map<String, Object> map) {
        Object map5 = map.get("SolutionProviderCreditEvent");
        String datatype = String.valueOf( map.get("DataType"));
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PaySolutionprovidercreditevent paySolutionprovidercreditevent;
                    paySolutionprovidercreditevent = Solutionprovidercreditevent(ls,datatype,shopname,area);
                    arrayList.add(paySolutionprovidercreditevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PaySolutionprovidercreditevent paySolutionprovidercreditevent;
        paySolutionprovidercreditevent = Solutionprovidercreditevent(map3,datatype,shopname,area);
        baseInsert(paySolutionprovidercreditevent);
        return 0;
    }

    //解析数据过程
    public PaySolutionprovidercreditevent Solutionprovidercreditevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PaySolutionprovidercreditevent paySolutionprovidercreditevent = new PaySolutionprovidercreditevent();
        String uuid = UuidUtils.buildUuid();
        paySolutionprovidercreditevent.setId(uuid);

        if (null!=ls.get("ProviderTransactionType")){
            paySolutionprovidercreditevent.setProvidertransactiontype(String.valueOf( ls.get("ProviderTransactionType")));
        }
        if (null!=ls.get("SellerOrderId")){
            paySolutionprovidercreditevent.setSellerorderid(String.valueOf( ls.get("SellerOrderId")));
        }
        if (null!=ls.get("MarketplaceId")){
            paySolutionprovidercreditevent.setMarketplaceid(String.valueOf( ls.get("MarketplaceId")));
        }
        if (null!=ls.get("MarketplaceCountryCode")){
            paySolutionprovidercreditevent.setMarketplacecountrycode(String.valueOf( ls.get("MarketplaceCountryCode")));
        }
        if (null!=ls.get("SellerId")){
            paySolutionprovidercreditevent.setSellerid(String.valueOf( ls.get("SellerId")));
        }
        if (null!=ls.get("SellerStoreName")){
            paySolutionprovidercreditevent.setSellerstorename(String.valueOf( ls.get("SellerStoreName")));
        }
        if (null!=ls.get("ProviderId")){
            paySolutionprovidercreditevent.setProviderid(String.valueOf( ls.get("ProviderId")));
        }
        if (null!=ls.get("ProviderStoreName")){
            paySolutionprovidercreditevent.setProviderstorename(String.valueOf( ls.get("ProviderStoreName")));
        }
        if (null!=shop){
            paySolutionprovidercreditevent.setShop(shop);
        }
        if (null!=area){
            paySolutionprovidercreditevent.setArea(area);
        }
        if(null!=datatype)
            paySolutionprovidercreditevent.setDatatype(datatype);
        return paySolutionprovidercreditevent;
    }




           /*2019 11 25

* 解析财务事件数据
RetrochargeEvent 事件处理报表

* */

    private int handleRetrochargeEvent(Map<String, Object> map) {
        Object map5 = map.get("RetrochargeEvent");
        String datatype = String.valueOf( map.get("DataType"));
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayRetrochargeevent payRetrochargeevent;
                    payRetrochargeevent=Retrochargeevent(ls,datatype,shopname,area);
                    arrayList.add(payRetrochargeevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayRetrochargeevent payRetrochargeevent;
        payRetrochargeevent=Retrochargeevent(map3,datatype,shopname,area);
        baseInsert(payRetrochargeevent);
        return 0;
    }

    //解析数据过程
    public PayRetrochargeevent Retrochargeevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayRetrochargeevent payRetrochargeevent = new PayRetrochargeevent();
        String uuid = UuidUtils.buildUuid();
        payRetrochargeevent.setId(uuid);
        if (null!=ls.get("RetrochargeTaxWithheldComponentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("RetrochargeTaxWithheldComponentList");
            payRetrochargeevent.setRetrochargetaxwithheldcomponentlistTaxcollectionmodel(String.valueOf( params.get("TaxCollectionModel")));
            Map<String, Object> params1 = (Map<String, Object>) params.get("TaxesWithheld");
            params1.put("DataType","RetrochargeEvent-TaxesWithheld");
            params1.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        if (null!=ls.get("BaseTax")){
            Map<String, String> params = (Map<String, String>) ls.get("BaseTax");
            payRetrochargeevent.setBasetaxCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payRetrochargeevent.setBasetaxCurrencycode(String.valueOf( params.get("CurrencyCode")));
        }
        if (null!=ls.get("ShippingTax")){
            Map<String, String> params = (Map<String, String>) ls.get("ShippingTax");
            payRetrochargeevent.setShippingtaxCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payRetrochargeevent.setShippingtaxCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("RetrochargeEventType")){
            payRetrochargeevent.setRetrochargeeventtype(String.valueOf( ls.get("RetrochargeEventType")));
        }
        if (null!=ls.get("AmazonOrderId")){
            payRetrochargeevent.setAmazonorderid(String.valueOf( ls.get("AmazonOrderId")));
        }
        if (null!=ls.get("MarketplaceName")){
            payRetrochargeevent.setMarketplacename(String.valueOf( ls.get("MarketplaceName")));
        }
        if (null!=ls.get("PostedDate")){
            payRetrochargeevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=shop){
            payRetrochargeevent.setShop(shop);
        }
        if (null!=area){
            payRetrochargeevent.setArea(area);
        }
        if(null!=datatype)
            payRetrochargeevent.setDatatype(datatype);
        return payRetrochargeevent;
    }

          /*2019 11 25

* 解析财务事件数据
DebtRecoveryEvent 事件处理报表

*
*/

    private int handleDebtRecoveryEvent(Map<String, Object> map) {
        Object map5 = map.get("DebtRecoveryEvent");
        String datatype = null;
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayDebtrecoveryevent payDebtrecoveryevent;
                    payDebtrecoveryevent = Debtrecoveryevent(ls,datatype,shopname,area);
                    arrayList.add(payDebtrecoveryevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayDebtrecoveryevent payDebtrecoveryevent;
        payDebtrecoveryevent = Debtrecoveryevent(map3,datatype,shopname,area);
        baseInsert(payDebtrecoveryevent);
        return 0;
    }

    //解析数据过程
    public PayDebtrecoveryevent Debtrecoveryevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayDebtrecoveryevent payDebtrecoveryevent = new PayDebtrecoveryevent();
        String uuid = UuidUtils.buildUuid();
        payDebtrecoveryevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payDebtrecoveryevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("ChargeInstrumentList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("ChargeInstrumentList");
            params.put("DataType","ChargeInstrumentList");
            params.put("ObjectId",uuid);
            handleChargeInstrument(params);
        }
        if (null!=ls.get("DebtRecoveryItemList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("DebtRecoveryItemList");
            params.put("DataType","DebtRecoveryItemList");
            params.put("ObjectId",uuid);
            handleDebtRecoveryItem(params);
        }

        if (null!=ls.get("RecoveryAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("RecoveryAmount");
            payDebtrecoveryevent.setRecoveryamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payDebtrecoveryevent.setRecoveryamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("OverPaymentCredit")){
            Map<String, String> params = (Map<String, String>) ls.get("OverPaymentCredit");
            payDebtrecoveryevent.setOverpaymentcreditCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payDebtrecoveryevent.setOverpaymentcreditCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("DebtRecoveryType")){
            payDebtrecoveryevent.setDebtrecoverytype(String.valueOf( ls.get("DebtRecoveryType")));
        }
        if (null!=shop){
            payDebtrecoveryevent.setShop(shop);
        }
        if (null!=area){
            payDebtrecoveryevent.setArea(area);
        }

        if(null!=datatype)
            payDebtrecoveryevent.setDatatype(datatype);
        return payDebtrecoveryevent;
    }

    /*2019 11 25

* 解析财务事件数据
LoanServicingEvent 事件处理报表

*
*/

    private int handleLoanServicingEvent(Map<String, Object> map) {
        Object map5 = map.get("LoanServicingEvent");
        String datatype = null;
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayLoanservicingevent payLoanservicingevent;
                    payLoanservicingevent= Loanservicingevent(map3,datatype,shopname,area);
                    arrayList.add(payLoanservicingevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayLoanservicingevent payLoanservicingevent;
        payLoanservicingevent= Loanservicingevent(map3,datatype,shopname,area);
        baseInsert(payLoanservicingevent);
        return 0;
    }


    //解析数据过程
    public PayLoanservicingevent Loanservicingevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayLoanservicingevent payLoanservicingevent = new PayLoanservicingevent();
        String uuid = UuidUtils.buildUuid();
        payLoanservicingevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payLoanservicingevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }

        if (null!=ls.get("LoanAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("LoanAmount");
            payLoanservicingevent.setLoanamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payLoanservicingevent.setLoanamountCurrencycode(String.valueOf( params.get("CurrencyCode")));
        }
        if (null!=ls.get("SourceBusinessEventType")){
            payLoanservicingevent.setSourcebusinesseventtype(String.valueOf( ls.get("SourceBusinessEventType")));
        }
        if (null!=shop){
            payLoanservicingevent.setShop(shop);
        }
        if (null!=area){
            payLoanservicingevent.setArea(area);
        }
        if(null!=datatype)
            payLoanservicingevent.setDatatype(datatype);
        return payLoanservicingevent;
    }



     /*2019 11 25

* 解析财务事件数据
AdjustmentEvent 事件处理报表

*
*/



    private int handleAdjustmentEvent(Map<String, Object> map) {
        Object map5 = map.get("AdjustmentEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayAdjustmentevent payAdjustmentevent;
                    payAdjustmentevent = Adjustmentevent(ls,datatype,shopname,area);
                    arrayList.add(payAdjustmentevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayAdjustmentevent payAdjustmentevent;
        payAdjustmentevent = Adjustmentevent(map3,datatype,shopname,area);
        baseInsert(payAdjustmentevent);
        return 0;
    }


    //解析数据过程
    public PayAdjustmentevent Adjustmentevent(Map<String,Object>ls,String datatype,String shop, String area) {
        PayAdjustmentevent payAdjustmentevent = new PayAdjustmentevent();
        String uuid = UuidUtils.buildUuid();
        payAdjustmentevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payAdjustmentevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("AdjustmentItemList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("AdjustmentItemList");
            params.put("DataType","AdjustmentItemList");
            params.put("ObjectId",uuid);
            handleAdjustmentItem(params);
        }
        if (null!=ls.get("AdjustmentAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("AdjustmentAmount");
            payAdjustmentevent.setAdjustmentamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payAdjustmentevent.setAdjustmentamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("AdjustmentType")){
            payAdjustmentevent.setAdjustmenttype(String.valueOf( ls.get("AdjustmentType")));
        }
        if (null!=shop){
            payAdjustmentevent.setShop(shop);
        }
        if (null!=area){
            payAdjustmentevent.setArea(area);
        }
        if(null!=datatype)
            payAdjustmentevent.setDatatype(datatype);
        return payAdjustmentevent;
    }


    /*2019 11 25

* 解析财务事件数据
CouponPaymentEvent 事件处理报表

*
*/
    private int handleCouponPaymentEvent(Map<String, Object> map) {
        Object map5 = map.get("CouponPaymentEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayCouponpaymentevent  payCouponpaymentevent;
                    payCouponpaymentevent =Couponpaymentevent(ls,datatype,shopname,area);
                    arrayList.add(payCouponpaymentevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayCouponpaymentevent  payCouponpaymentevent;
        payCouponpaymentevent =Couponpaymentevent(map3,datatype,shopname,area);
        baseInsert(payCouponpaymentevent);
        return 0;
    }

    //解析数据过程
    public PayCouponpaymentevent Couponpaymentevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayCouponpaymentevent  payCouponpaymentevent = new PayCouponpaymentevent();
        String uuid = UuidUtils.buildUuid();
        payCouponpaymentevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payCouponpaymentevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("FeeComponent")){
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> params1 = (Map<String, Object>) ls.get("FeeComponent");
            params.put("DataType","CouponPaymentEvent-FeeComponent");
            params.put("ObjectId",uuid);
            params.put("FeeComponent",params1);
            handleFeeComponent(params);
        }
        if (null!=ls.get("AdjustmentItemList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("AdjustmentItemList");
            params.put("DataType","CouponPaymentEvent-ChargeComponent");
            params.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        if (null!=ls.get("TotalAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("TotalAmount");
            payCouponpaymentevent.setTotalamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payCouponpaymentevent.setTotalamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PostedDate")){
            payCouponpaymentevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("CouponId")){
            payCouponpaymentevent.setCouponid(String.valueOf( ls.get("CouponId")));
        }
        if (null!=ls.get("SellerCouponDescription")){
            payCouponpaymentevent.setSellercoupondescription(String.valueOf( ls.get("SellerCouponDescription")));
        }
        if (null!=ls.get("ClipOrRedemptionCount")){
            payCouponpaymentevent.setCliporredemptioncount(String.valueOf( ls.get("ClipOrRedemptionCount")));
        }
        if (null!=ls.get("PaymentEventId")){
            payCouponpaymentevent.setPaymenteventid(String.valueOf( ls.get("PaymentEventId")));
        }
        if (null!=shop){
            payCouponpaymentevent.setShop(shop);
        }
        if (null!=area){
            payCouponpaymentevent.setArea(area);
        }

        if(null!=datatype)
            payCouponpaymentevent.setDatatype(datatype);
        return payCouponpaymentevent;
    }


    /*2019 11 25

 * 解析财务事件数据
 SAFETReimbursementEvent 事件处理报表

 *
 */
    private int handleSAFETReimbursementEvent(Map<String, Object> map) {
        Object map5 = map.get("SAFETReimbursementEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PaySafetreimbursementevent paySafetreimbursementevent;
                    paySafetreimbursementevent = Safetreimbursementevent(ls,datatype,shopname,area);
                    arrayList.add(paySafetreimbursementevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PaySafetreimbursementevent paySafetreimbursementevent;
        paySafetreimbursementevent = Safetreimbursementevent(map3,datatype,shopname,area);
        baseInsert(paySafetreimbursementevent);
        return 0;
    }


    //解析数据过程
    public PaySafetreimbursementevent Safetreimbursementevent(Map<String,Object>ls,String datatype,String shop ,String area) {
        PaySafetreimbursementevent paySafetreimbursementevent = new PaySafetreimbursementevent();
        String uuid = UuidUtils.buildUuid();
        paySafetreimbursementevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            paySafetreimbursementevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("SAFETReimbursementItemList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("SAFETReimbursementItemList");
            Map<String, Object> params1 = (Map<String, Object>) params.get("ItemChargeList");
            params1.put("DataType","SAFETReimbursementEvent-SAFETReimbursementItem");
            params1.put("ObjectId",uuid);
            handleChargeComponent(params1);
        }
        if (null!=ls.get("ReimbursedAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("ReimbursedAmount");
            paySafetreimbursementevent.setReimbursedamountCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            paySafetreimbursementevent.setReimbursedamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PostedDate")){
            paySafetreimbursementevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("SAFETClaimId")){
            paySafetreimbursementevent.setSafetclaimid(String.valueOf( ls.get("SAFETClaimId")));
        }
        if (null!=shop){
            paySafetreimbursementevent.setShop(shop);
        }
        if (null!=area){
            paySafetreimbursementevent.setArea(area);
        }

        if(null!=datatype)
            paySafetreimbursementevent.setDatatype(datatype);
        return paySafetreimbursementevent;
    }





    /*2019 11 25

* 解析财务事件数据
SellerReviewEnrollmentPaymentEvent 事件处理报表

*
*/
    private int handleSellerReviewEnrollmentPaymentEvent(Map<String, Object> map) {
        Object map5 = map.get("SellerReviewEnrollmentPaymentEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PaySellerreviewenrollmentpaymentevent paySellerreviewenrollmentpaymentevent;
                    paySellerreviewenrollmentpaymentevent=Sellerreviewenrollmentpaymentevent(ls,datatype,shopname,area);
                    arrayList.add(paySellerreviewenrollmentpaymentevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PaySellerreviewenrollmentpaymentevent paySellerreviewenrollmentpaymentevent;
        paySellerreviewenrollmentpaymentevent=Sellerreviewenrollmentpaymentevent(map3,datatype,shopname,area);
        baseInsert(paySellerreviewenrollmentpaymentevent);
        return 0;
    }

    //解析数据过程
    public PaySellerreviewenrollmentpaymentevent Sellerreviewenrollmentpaymentevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PaySellerreviewenrollmentpaymentevent paySellerreviewenrollmentpaymentevent = new PaySellerreviewenrollmentpaymentevent();
        String uuid = UuidUtils.buildUuid();
        paySellerreviewenrollmentpaymentevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            paySellerreviewenrollmentpaymentevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("ChargeComponent")){
            Map<String, Object> params1 =new HashMap<>();
            Map<String, Object> params = (Map<String, Object>) ls.get("ChargeComponent");
            params1.put("DataType","SellerReviewEnrollmentPaymentEvent-ChargeComponent");
            params1.put("ObjectId",uuid);
            params1.put("ChargeComponent",params);
            handleChargeComponent(params1);
        }
        if (null!=ls.get("FeeComponent")){
            Map<String, Object> params1 =new HashMap<>();
            Map<String, Object> params = (Map<String, Object>) ls.get("FeeComponent");
            params1.put("DataType","SellerReviewEnrollmentPaymentEvent-FeeComponent");
            params1.put("ObjectId",uuid);
            params1.put("FeeComponent",params);
            handleFeeComponent(params1);
        }
        if (null!=ls.get("TotalAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("TotalAmount");
            paySellerreviewenrollmentpaymentevent.setTotalamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            paySellerreviewenrollmentpaymentevent.setTotalamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PostedDate")){
            paySellerreviewenrollmentpaymentevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("EnrollmentId")){
            paySellerreviewenrollmentpaymentevent.setEnrollmentid(String.valueOf( ls.get("EnrollmentId")));
        }
        if (null!=ls.get("ParentASIN")){
            paySellerreviewenrollmentpaymentevent.setParentasin(String.valueOf( ls.get("ParentASIN")));
        }
        if (null!=shop){
            paySellerreviewenrollmentpaymentevent.setShop(shop);
        }
        if (null!=area){
            paySellerreviewenrollmentpaymentevent.setArea(area);
        }

        if(null!=datatype)
            paySellerreviewenrollmentpaymentevent.setDatatype(datatype);
        return paySellerreviewenrollmentpaymentevent;
    }



    /*2019 11 25

* 解析财务事件数据
FBALiquidationEvent 事件处理报表
*
*/
    private int handleFBALiquidationEvent(Map<String, Object> map) {
        Object map5 = map.get("FBALiquidationEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayFbaliquidationevent payFbaliquidationevent = new PayFbaliquidationevent();
                    payFbaliquidationevent =FBALiquidationEvent(map3,datatype,shopname,area);
                    arrayList.add(payFbaliquidationevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayFbaliquidationevent payFbaliquidationevent = new PayFbaliquidationevent();
        payFbaliquidationevent =FBALiquidationEvent(map3,datatype,shopname,area);
        baseInsert(payFbaliquidationevent);
        return 0;
    }

    //解析过程
    private PayFbaliquidationevent FBALiquidationEvent(Map<String,Object>ls,String datatype,String shop,String area){

        PayFbaliquidationevent payFbaliquidationevent = new PayFbaliquidationevent();
        String uuid = UuidUtils.buildUuid();
        payFbaliquidationevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payFbaliquidationevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("LiquidationProceedsAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("LiquidationProceedsAmount");
            payFbaliquidationevent.setLiquidationproceedsamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payFbaliquidationevent.setLiquidationproceedsamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("LiquidationFeeAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("LiquidationFeeAmount");
            payFbaliquidationevent.setLiquidationfeeamountCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payFbaliquidationevent.setLiquidationfeeamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PostedDate")){
            payFbaliquidationevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("OriginalRemovalOrderId")){
            payFbaliquidationevent.setOriginalremovalorderid(String.valueOf( ls.get("OriginalRemovalOrderId")));
        }
        if (null!=shop){
            payFbaliquidationevent.setShop(shop);
        }
        if (null!=area){
            payFbaliquidationevent.setArea(area);
        }
        if(null!=datatype)
            payFbaliquidationevent.setDatatype(datatype);
        return payFbaliquidationevent;
    }





    /*2019 11 25

* 解析财务事件数据
ImagingServicesFeeEvent 事件处理报表

*
*/
    private int handleImagingServicesFeeEvent(Map<String, Object> map) {
        Object map5 = map.get("ImagingServicesFeeEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayImagingservicesfeeevent payImagingservicesfeeevent;
                    payImagingservicesfeeevent = Imagingservicesfeeevent(ls,datatype,shopname,area);
                    arrayList.add(payImagingservicesfeeevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayImagingservicesfeeevent payImagingservicesfeeevent;
        payImagingservicesfeeevent = Imagingservicesfeeevent(map3,datatype,shopname,area);
        baseInsert(payImagingservicesfeeevent);
        return 0;
    }

    //解析数据过程
    public PayImagingservicesfeeevent Imagingservicesfeeevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayImagingservicesfeeevent payImagingservicesfeeevent=new PayImagingservicesfeeevent();
        String uuid = UuidUtils.buildUuid();
        payImagingservicesfeeevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payImagingservicesfeeevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("FeeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("FeeList");
            params.put("DataType","ImagingServicesFeeEvent-FeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("PostedDate")){
            payImagingservicesfeeevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("ImagingRequestBillingItemID")){
            payImagingservicesfeeevent.setImagingrequestbillingitemid(String.valueOf( ls.get("ImagingRequestBillingItemID")));
        }
        if (null!=ls.get("ASIN")){
            payImagingservicesfeeevent.setAsin(String.valueOf( ls.get("ASIN")));
        }
        if (null!=shop){
            payImagingservicesfeeevent.setShop(shop);
        }
        if (null!=area){
            payImagingservicesfeeevent.setArea(area);
        }
        if(null!=datatype)
            payImagingservicesfeeevent.setDatatype(datatype);
        return payImagingservicesfeeevent;
    }



    /*2019 11 25

* 解析财务事件数据
AffordabilityExpenseEvent 事件处理报表

*
*/
    private int handleAffordabilityExpenseEvent(Map<String, Object> map) {
        Object map5 = map.get("AffordabilityExpenseEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayAffordabilityexpenseevent payAffordabilityexpenseevent;
                    payAffordabilityexpenseevent = Affordabilityexpenseevent(ls,datatype,shopname,area);
                    arrayList.add(payAffordabilityexpenseevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayAffordabilityexpenseevent payAffordabilityexpenseevent;
        payAffordabilityexpenseevent = Affordabilityexpenseevent(map3,datatype,shopname,area);
        baseInsert(payAffordabilityexpenseevent);
        return 0;
    }


    //解析数据过程
    public PayAffordabilityexpenseevent Affordabilityexpenseevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayAffordabilityexpenseevent payAffordabilityexpenseevent = new PayAffordabilityexpenseevent();
        String uuid = UuidUtils.buildUuid();
        payAffordabilityexpenseevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payAffordabilityexpenseevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("BaseExpense")){
            Map<String, String> params = (Map<String, String>) ls.get("BaseExpense");
            payAffordabilityexpenseevent.setBaseexpenseCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payAffordabilityexpenseevent.setBaseexpenseCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TotalExpense")){
            Map<String, String> params = (Map<String, String>) ls.get("TotalExpense");
            payAffordabilityexpenseevent.setTotalexpenseCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpenseevent.setTotalexpenseCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxTypeIGST")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxTypeIGST");
            payAffordabilityexpenseevent.setTaxtypeigstCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpenseevent.setTaxtypeigstCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxTypeCGST")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxTypeCGST");
            payAffordabilityexpenseevent.setTaxtypecgstCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpenseevent.setTaxtypecgstCurrencycode(String.valueOf( params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxTypeSGST")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxTypeSGST");
            payAffordabilityexpenseevent.setTaxtypesgstCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpenseevent.setTaxtypesgstCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PostedDate")){
            payAffordabilityexpenseevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("TransactionType")){
            payAffordabilityexpenseevent.setTransactiontype(String.valueOf( ls.get("TransactionType")));
        }
        if (null!=ls.get("MarketplaceId")){
            payAffordabilityexpenseevent.setMarketplaceid(String.valueOf( ls.get("MarketplaceId")));
        }
        if (null!=ls.get("AmazonOrderId")){
            payAffordabilityexpenseevent.setAmazonorderid(String.valueOf( ls.get("AmazonOrderId")));
        }
        if (null!=shop){
            payAffordabilityexpenseevent.setShop(shop);
        }
        if (null!=area){
            payAffordabilityexpenseevent.setArea(area);
        }
        if(null!=datatype)
            payAffordabilityexpenseevent.setDatatype(datatype);
        return payAffordabilityexpenseevent;
    }


    /*2019 11 25

* 解析财务事件数据
AffordabilityExpenseReversalEvent 事件处理报表

*
*/
    private int handleAffordabilityExpenseReversalEvent(Map<String, Object> map) {
        Object map5 = map.get("AffordabilityExpenseReversalEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayAffordabilityexpensereversalevent payAffordabilityexpensereversalevent;
                    payAffordabilityexpensereversalevent = Affordabilityexpensereversalevent(ls,datatype,shopname,area);
                    arrayList.add(payAffordabilityexpensereversalevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayAffordabilityexpensereversalevent payAffordabilityexpensereversalevent;
        payAffordabilityexpensereversalevent = Affordabilityexpensereversalevent(map3,datatype,shopname,area);
        baseInsert(payAffordabilityexpensereversalevent);
        return 0;
    }


    //解析数据过程
    public PayAffordabilityexpensereversalevent Affordabilityexpensereversalevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayAffordabilityexpensereversalevent payAffordabilityexpensereversalevent = new PayAffordabilityexpensereversalevent();
        String uuid = UuidUtils.buildUuid();
        payAffordabilityexpensereversalevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payAffordabilityexpensereversalevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("BaseExpense")){
            Map<String, String> params = (Map<String, String>) ls.get("BaseExpense");
            payAffordabilityexpensereversalevent.setBaseexpenseCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpensereversalevent.setBaseexpenseCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TotalExpense")){
            Map<String, String> params = (Map<String, String>) ls.get("TotalExpense");
            payAffordabilityexpensereversalevent.setTotalexpenseCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpensereversalevent.setTotalexpenseCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxTypeIGST")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxTypeIGST");
            payAffordabilityexpensereversalevent.setTaxtypeigstCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpensereversalevent.setTaxtypeigstCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxTypeCGST")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxTypeCGST");
            payAffordabilityexpensereversalevent.setTaxtypecgstCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpensereversalevent.setTaxtypecgstCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxTypeSGST")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxTypeSGST");
            payAffordabilityexpensereversalevent.setTaxtypesgstCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payAffordabilityexpensereversalevent.setTaxtypesgstCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PostedDate")){
            payAffordabilityexpensereversalevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("TransactionType")){
            payAffordabilityexpensereversalevent.setTransactiontype(String.valueOf( ls.get("TransactionType")));
        }
        if (null!=ls.get("MarketplaceId")){
            payAffordabilityexpensereversalevent.setMarketplaceid(String.valueOf( ls.get("MarketplaceId")));
        }
        if (null!=ls.get("AmazonOrderId")){
            payAffordabilityexpensereversalevent.setAmazonorderid(String.valueOf( ls.get("AmazonOrderId")));
        }
        if (null!=shop){
            payAffordabilityexpensereversalevent.setShop(shop);
        }
        if (null!=area){
            payAffordabilityexpensereversalevent.setArea(area);
        }
        if(null!=datatype)
            payAffordabilityexpensereversalevent.setDatatype(datatype);
        return payAffordabilityexpensereversalevent;
    }


    /*2019 11 25

* 解析财务事件数据
NetworkComminglingTransactionEvent 事件处理报表

*
*/
    private int handleNetworkComminglingTransactionEvent(Map<String, Object> map) {
        Object map5 = map.get("NetworkComminglingTransactionEvent");
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        String datatype = null;
        if(null!=map.get("DataType")){
            datatype =String.valueOf( map.get("DataType"));
        }
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayNetworkcomminglingtransactionevent payNetworkcomminglingtransactionevent;
                    payNetworkcomminglingtransactionevent = Networkcomminglingtransactionevent(ls,datatype,shopname,area);
                    arrayList.add(payNetworkcomminglingtransactionevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayNetworkcomminglingtransactionevent payNetworkcomminglingtransactionevent;
        payNetworkcomminglingtransactionevent = Networkcomminglingtransactionevent(map3,datatype,shopname,area);
        baseInsert(payNetworkcomminglingtransactionevent);
        return 0;
    }

    //解析数据过程
    public PayNetworkcomminglingtransactionevent Networkcomminglingtransactionevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayNetworkcomminglingtransactionevent payNetworkcomminglingtransactionevent = new PayNetworkcomminglingtransactionevent();
        String uuid = UuidUtils.buildUuid();
        payNetworkcomminglingtransactionevent.setId(uuid);
        if (null!=ls.get("ObjectId")){
            payNetworkcomminglingtransactionevent.setObjectid(String.valueOf( ls.get("ObjectId")));
        }
        if (null!=ls.get("TaxExclusiveAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxExclusiveAmount");
            payNetworkcomminglingtransactionevent.setTaxexclusiveamountCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payNetworkcomminglingtransactionevent.setTaxexclusiveamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("TaxAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("TaxAmount");
            payNetworkcomminglingtransactionevent.setTaxamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payNetworkcomminglingtransactionevent.setTaxamountCurrencycode(String.valueOf( params.get("CurrencyCode")));
        }

        if (null!=ls.get("PostedDate")){
            payNetworkcomminglingtransactionevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=ls.get("TransactionType")){
            payNetworkcomminglingtransactionevent.setTransactiontype(String.valueOf( ls.get("TransactionType")));
        }
        if (null!=ls.get("MarketplaceId")){
            payNetworkcomminglingtransactionevent.setMarketplaceid(String.valueOf( ls.get("MarketplaceId")));
        }
        if (null!=ls.get("NetCoTransactionID")){
            payNetworkcomminglingtransactionevent.setNetcotransactionid(String.valueOf( ls.get("NetCoTransactionID")));
        }
        if (null!=ls.get("SwapReason")){
            payNetworkcomminglingtransactionevent.setSwapreason(String.valueOf( ls.get("SwapReason")));
        }
        if (null!=ls.get("ASIN")){
            payNetworkcomminglingtransactionevent.setAsin(String.valueOf( ls.get("ASIN")));
        }
        if(null!=shop){
            payNetworkcomminglingtransactionevent.setShop(shop);
        }
        if(null!=area){
            payNetworkcomminglingtransactionevent.setArea(area);
        }
        if(null!=datatype)
            payNetworkcomminglingtransactionevent.setDatatype(datatype);
        return payNetworkcomminglingtransactionevent;
    }




/*2019 11 25
* 解析财务事件数据
RentalTransactionEvent 事件处理报表

* */

    private int handleRentalTransactionEvent(Map<String, Object> map) {
        Object map5 = map.get("RentalTransactionEvent");
        String datatype = String.valueOf( map.get("DataType"));
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayRentaltransactionevent payRentaltransactionevent;
                    payRentaltransactionevent = Rentaltransactionevent(ls,datatype,shopname,area);
                    arrayList.add(payRentaltransactionevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayRentaltransactionevent payRentaltransactionevent;
        payRentaltransactionevent = Rentaltransactionevent(map3,datatype,shopname,area);
        baseInsert(payRentaltransactionevent);
        return 0;
    }


    //解析数据过程
    public PayRentaltransactionevent Rentaltransactionevent(Map<String,Object>ls,String datatype,String shop,String area) {
        PayRentaltransactionevent payRentaltransactionevent = new PayRentaltransactionevent();
        String uuid = UuidUtils.buildUuid();
        payRentaltransactionevent.setId(uuid);
        if (null!=ls.get("RentalChargeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("RentalChargeList");
            params.put("DataType","RentalTransactionEvent-RentalChargeList");
            params.put("ObjectId",uuid);
            handleChargeComponent(params);
        }
        if (null!=ls.get("RentalFeeList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("RentalFeeList");
            params.put("DataType","RentalTransactionEvent-RentalFeeList");
            params.put("ObjectId",uuid);
            handleFeeComponent(params);
        }
        if (null!=ls.get("RentalTaxWithheldList")){
            Map<String, Object> params = (Map<String, Object>) ls.get("RetrochargeTaxWithheldComponentList");
            payRentaltransactionevent.setRentaltaxwithheldlistTaxcollectionmodel(String.valueOf( params.get("TaxCollectionModel")));
            Map<String, Object> params1 = (Map<String, Object>) params.get("TaxesWithheld");
            params1.put("DataType","RentalTransactionEvent-TaxesWithheld");
            params1.put("ObjectId",uuid);
            handleChargeComponent(params1);
        }
        if (null!=ls.get("RentalInitialValue")){
            Map<String, String> params = (Map<String, String>) ls.get("RentalInitialValue");
            payRentaltransactionevent.setRentalinitialvalueCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payRentaltransactionevent.setRentalinitialvalueCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("RentalReimbursement")){
            Map<String, String> params = (Map<String, String>) ls.get("RentalReimbursement");
            payRentaltransactionevent.setRentalreimbursementCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payRentaltransactionevent.setRentalreimbursementCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("ExtensionLength")){
            payRentaltransactionevent.setExtensionlength(String.valueOf( ls.get("ExtensionLength")));
        }
        if (null!=ls.get("AmazonOrderId")){
            payRentaltransactionevent.setAmazonorderid(String.valueOf( ls.get("AmazonOrderId")));
        }
        if (null!=ls.get("MarketplaceName")){
            payRentaltransactionevent.setMarketplacename(String.valueOf( ls.get("MarketplaceName")));
        }
        if (null!=ls.get("PostedDate")){
            payRentaltransactionevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=shop){
            payRentaltransactionevent.setShop(shop);
        }
        if (null!=ls.get("PostedDate")){
            payRentaltransactionevent.setArea(area);
        }
        if(null!=datatype)
            payRentaltransactionevent.setDatatype(datatype);
        return payRentaltransactionevent;
    }



        /*2019 11 25

* 解析财务事件数据
ProductAdsPaymentEvent 事件处理报表

* */

    private int handleProductAdsPaymentEvent(Map<String, Object> map) {
        Object map5 = map.get("ProductAdsPaymentEvent");
        String datatype = String.valueOf( map.get("DataType"));
        String shopname = String.valueOf( map.get("ShopName"));
        String area = String.valueOf( map.get("Area"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayProductadspaymentevent payProductadspaymentevent;
                    payProductadspaymentevent = Productadspaymentevent(ls,datatype,shopname,area);
                    arrayList.add(payProductadspaymentevent);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayProductadspaymentevent payProductadspaymentevent;
        payProductadspaymentevent = Productadspaymentevent(map3,datatype,shopname,area);
        baseInsert(payProductadspaymentevent);
        return 0;
    }





    //解析数据过程
    public PayProductadspaymentevent Productadspaymentevent(Map<String,Object>ls,String datatype,String shop, String area) {
        PayProductadspaymentevent payProductadspaymentevent = new PayProductadspaymentevent();
        String uuid = UuidUtils.buildUuid();
        payProductadspaymentevent.setId(uuid);

        if (null!=ls.get("baseValue")){
            Map<String, String> params = (Map<String, String>) ls.get("baseValue");
            payProductadspaymentevent.setBasevalueCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payProductadspaymentevent.setBasevalueCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("taxValue")){
            Map<String, String> params = (Map<String, String>) ls.get("taxValue");
            payProductadspaymentevent.setTaxvalueCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payProductadspaymentevent.setTaxvalueCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("transactionValue")){
            Map<String, String> params = (Map<String, String>) ls.get("transactionValue");
            payProductadspaymentevent.setTransactionvalueCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payProductadspaymentevent.setTransactionvalueCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }

        if (null!=ls.get("transactionType")){
            payProductadspaymentevent.setTransactiontype(String.valueOf( ls.get("transactionType")));
        }

        if (null!=ls.get("PostedDate")){
            payProductadspaymentevent.setPosteddate( DateUtil.strToDate(String.valueOf(ls.get("PostedDate"))));
        }
        if (null!=shop){
            payProductadspaymentevent.setShop(shop);
        }
        if (null!=area){
            payProductadspaymentevent.setArea(area);
        }

        if(null!=datatype)
            payProductadspaymentevent.setDatatype(datatype);
        return payProductadspaymentevent;
    }





     /*2019 11 25

* 解析财务事件数据
pay_promotion 事件处理报表

* */

    private int handlePromotion(Map<String, Object> map) {
        Object map5 = map.get("Promotion");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayPromotion payPromotion;
                    payPromotion =Promotion(ls,datatype,ObjectId);
                    arrayList.add(payPromotion);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayPromotion payPromotion;
        payPromotion =Promotion(map3,datatype,ObjectId);
        baseInsert(payPromotion);
        return 0;
    }


    //解析数据过程
    public PayPromotion Promotion(Map<String,Object>ls,String datatype,String ObjectId) {
        PayPromotion payPromotion = new PayPromotion();
        String uuid = UuidUtils.buildUuid();
        payPromotion.setId(uuid);
        if (null!=ObjectId){
            payPromotion.setObjectid(ObjectId);
        }
        if (null!=ls.get("PromotionAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("PromotionAmount");
            payPromotion.setPromotionCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payPromotion.setPromotionCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("PromotionType")){
            payPromotion.setPromotionPromotiontype(String.valueOf( ls.get("PromotionType")));
        }
        if (null!=ls.get("PromotionId")){
            payPromotion.setPromotionPromotionid(String.valueOf( ls.get("PromotionId")));
        }
        payPromotion.setDatatype(datatype);
        return payPromotion;
    }


   /*2019 11 25

* 解析财务事件数据
DebtRecoveryItem 事件处理报表

* */
    private int handleDebtRecoveryItem(Map<String, Object> map) {
        Object map5 = map.get("DebtRecoveryItem");
        String datatype = String.valueOf( map.get("DataType"));
        String ObjectId = String.valueOf( map.get("ObjectId"));
        List<Object> arrayList = new ArrayList<>();
        Map<String, Object> map3 = null;
        if (map5.getClass().isArray()
                || map5 instanceof JSONArray
                || map5 instanceof JSON) {
            if(map5 instanceof JSONObject){
                map3 = (Map<String, Object>) map5;
            }else{
                List<Map<String, Object>> list = (List<Map<String, Object>>) map5;
                for (Map<String, Object> ls:list) {
                    PayDebtrecoveryitem payDebtrecoveryitem= Debtrecoveryitem(ls,datatype,ObjectId);
                    arrayList.add(payDebtrecoveryitem);
                }
                baseInsertList(arrayList);
                return 0;
            }
        } else {
            map3 = (Map<String, Object>) map5;
        }
        PayDebtrecoveryitem payDebtrecoveryitem= Debtrecoveryitem(map3,datatype,ObjectId);
        baseInsert(payDebtrecoveryitem);
        return 0;
    }


    //解析数据过程
    public PayDebtrecoveryitem Debtrecoveryitem(Map<String,Object>ls,String datatype,String ObjectId) {
        PayDebtrecoveryitem payDebtrecoveryitem = new PayDebtrecoveryitem();
        String uuid = UuidUtils.buildUuid();
        payDebtrecoveryitem.setId(uuid);
        if (null!=ObjectId){
            payDebtrecoveryitem.setObjectid(ObjectId);
        }
        if (null!=ls.get("RecoveryAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("RecoveryAmount");
            payDebtrecoveryitem.setRecoveryamountCurrencyamount(String.valueOf( params.get("CurrencyAmount")));
            payDebtrecoveryitem.setRecoveryamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("OriginalAmount")){
            Map<String, String> params = (Map<String, String>) ls.get("OriginalAmount");
            payDebtrecoveryitem.setOriginalamountCurrencyamount(String.valueOf(params.get("CurrencyAmount")));
            payDebtrecoveryitem.setOriginalamountCurrencycode(String.valueOf(params.get("CurrencyCode")));
        }
        if (null!=ls.get("GroupBeginDate")){
            payDebtrecoveryitem.setGroupbegindate((Date) ls.get("GroupBeginDate"));
        }
        if (null!=ls.get("GroupEndDate")){
            payDebtrecoveryitem.setGroupenddate((Date) ls.get("GroupEndDate"));
        }
        if(null!=datatype){
            payDebtrecoveryitem.setDatatype(datatype);
        }
        return payDebtrecoveryitem;
    }













}