package com.weiziplus.springboot.scheduled.mwsapi;

import com.weiziplus.springboot.mapper.advertisingInventoryReport.InventoryAgeMapper;
import com.weiziplus.springboot.mapper.data.record.DataGetErrorRecordMapper;
import com.weiziplus.springboot.mapper.scheduled.*;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import com.weiziplus.springboot.utils.amazon.AmazonMwsApi;
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

import java.text.SimpleDateFormat;
import java.util.*;

import static com.weiziplus.springboot.utils.DateUtil.*;

/**
 * 亚马逊api定时任务----------定时任务必须try-catch
 *
 * @author wanglongwei
 * @data 2019/7/15 16:27
 */
@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonMwsApiEveryDaySchedule extends AmazonMwsApiBaseSchedule {

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
    DataGetErrorRecordMapper dataGetErrorRecordMapper;

    @Autowired
    AreaMapper areaMapper;

    /**
     * 通用逻辑处理
     *
     * @param taskName
     */
    protected void baseHandle(String taskName, int type) {
        log.info("***********获取------" + taskName + "-----定时任务开始**********");
        List<Shop> shopList = shopMapper.getAllList();
        try {
            for (Shop shop:shopList) {
            // shop和area的map（包含了这个店铺的所有信息以及区域信息，shop 和 area）
            Map<String, Object> map = beginScheduleTaskValidate(taskName,shop);
            if (null == map) {
                log.warn("×××××" + taskName + "×××定时任务出错××××××××××××××××××");
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
                //尝试去除关于EU欧洲五国的area对象---苏建东
                if ("EU".equals(area.getMwsCountryCode())){
                    continue;
                }
                //创建事务还原点
                Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                try {
                    params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
                    params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
                    params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
                    if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                        params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
                    }
                    // 获取报告数据
                    List<List<String>> lists = handleGetReport(taskName, area, secretKey, params);
                    if (null == lists) {
                        //回滚事务
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        errorNum++;
                        errorMsg.append("第").append(errorNum).append("次出错,详情请看日志信息;");
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
                    if (1 >= lists.size()) {
                        errorNum = 0;
                        errorMsg = new StringBuffer();
                        continue;
                    }
                    handleInsert(taskName, lists, shopMap, area);
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
            log.warn("×××××" + taskName + "×××定时任务出错××××××××××详情:" + e);
        } finally {
            log.info("***********获取------" + taskName + "-----定时任务结束**********");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String taskEndTime = df.format(new Date());
            List<DataGetErrorRecord> dataGetErrorRecordList = dataGetErrorRecordMapper.getListByTaskName(taskName, taskEndTime);
            if (null == dataGetErrorRecordList || dataGetErrorRecordList.size() == 0) {
                log.info("***********获取------" + taskName + "-----定时任务全部数据以获取**********");
                return;
            }
            try {
                Thread.sleep(1800000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (DataGetErrorRecord errorData : dataGetErrorRecordList) {
                Shop shop = shopMapper.getOneInfoByName(errorData.getShop());
                String errorTaskName = errorData.getName();
                String taskArea = errorData.getArea();
                int isRightSign = baseHandleByErrorTask(errorTaskName, shop, taskArea);
                if (isRightSign == 0) {
                    dataGetErrorRecordMapper.updateIsHandle(errorData.getId(), 1);
                }
            }
            List<DataGetErrorRecord> dataGetErrorRecordResultList = dataGetErrorRecordMapper.getListByTaskName(taskName, taskEndTime);
            if (null == dataGetErrorRecordResultList || dataGetErrorRecordResultList.size() == 0) {
                log.info("***********获取------" + taskName + "-----错误任务重新成功，已拿到数据**********");
            }
        }
    }

    /**
     * 通用逻辑处理之出现错误的任务重新调用
     *
     * @param taskName
     * @param shop
     * @return 0：获取数据成功，1：获取数据失败
     */
    protected int baseHandleByErrorTask(String taskName, Shop shop, String taskArea) {
        log.info("***********获取------" + taskName + "-----错误处理任务开始**********");
        try {
            Map<String, Object> map = beginScheduleTaskValidate(taskName, shop);
            if (null == map) {
                log.warn("×××××" + taskName + "×××错误处理任务出错××××××××××××××××××");
                return 1;
            }
            Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
            String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
            String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
            String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
            String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
            Map<String, String> params = new HashMap<>(3);
            int errorNum = 0;
            StringBuffer errorMsg = new StringBuffer();
            Area area = areaMapper.getAreaByMWSCountryCode(taskArea);
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            try {
                params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
                params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
                params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
                if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                    params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
                }
                List<List<String>> lists = handleGetReport(taskName, area, secretKey, params);

                if (null == lists) {
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
//                                index--;
                        Thread.sleep(600 * errorNum);
                    }
                }
                if (1 >= lists.size()) {
                    errorNum = 0;
                    errorMsg = new StringBuffer();
                }
                handleInsert(taskName, lists, shopMap, area);
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
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                }
            }
//                }
        } catch (Exception e) {
            log.warn("×××××" + taskName + "×××错误处理任务出错××××××××××详情:" + e);
            return 1;
        } finally {
            log.info("***********获取------" + taskName + "-----错误处理任务结束**********");
            return 0;
        }
    }

    /**
     * 处理获取报告数据
     *
     * @param taskName
     * @param area
     * @param secretKey
     * @param params
     * @return
     */
    public List<List<String>> handleGetReport(String taskName, Area area, String secretKey, Map<String, String> params) throws Exception {
        List<List<String>> lists = null;
        switch (taskName) {
            case "库龄": {
                params.put("StartDate",AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-1)))));
                params.put("EndDate",AmazonMwsUntil.urlEncode(timeToISO8601((getDate()))));
                lists = handleInventoryAgeGetReport(area, secretKey, params);
            }
            break;
            case "每日库存记录": {
                lists = handleEveryDayInventoryRecordsGetReport(area, secretKey, params);
            }
            break;
            case "FBA customer returns": {
                lists = handleFbaCustomerReturnsGetReport(area, secretKey, params);
            }
            break;
            case "费用预览": {
                params.put("StartDate",AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-1)))));
                params.put("EndDate",AmazonMwsUntil.urlEncode(timeToISO8601((getDate()))));
                lists = handleFeePreviewGetReport(area, secretKey, params);
            }
            break;
            case "盘库": {
                lists = handleInventoryGetReport(area, secretKey, params);
            }
            break;
            case "移除": {
                lists = handleRemovalOrderDetailGetReport(area, secretKey, params);
            }
            break;
            case "移除货件详情": {
                lists = handleRemovalShipmentDetailGetReport(area, secretKey, params);
            }
            break;
            case "已完成订单": {
                lists = handleCompleteOrderGetReport(area, secretKey, params);
            }
            break;
            default: {
            }
        }
        return lists;
    }

    /**
     * 处理插入数据
     *
     * @param taskName
     * @param lists
     * @param shopMap
     * @param area
     * @throws Exception
     */
    public void handleInsert(String taskName, List<List<String>> lists, Map<String, Object> shopMap, Area area) throws Exception {
        List<Object> insertList = null;
        switch (taskName) {
            case "库龄": {
                insertList = handleInventoryAge(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "每日库存记录": {
                insertList = handleEveryDayInventoryRecords(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "FBA customer returns": {
                insertList = handleFbaCustomerReturns(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "费用预览": {
                handleFeePreview(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "盘库": {
                insertList = handleInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "移除": {
                handleRemovalOrderDetail(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "移除货件详情": {
                insertList = handleRemovalShipmentDetail(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "已完成订单": {
                insertList = handleCompleteOrder(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            default: {
            }
        }
        baseInsertList(insertList);
    }

    /**
     * 库龄  每天凌晨2分执行
     */
    //@Scheduled(cron = "0 51 18 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void inventoryAge() {
        baseHandle(TASK_NAME[3], 2);
    }

    /**
     * 处理库龄请求参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     */
    private List<List<String>> handleInventoryAgeGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_FBA_INVENTORY_AGED_DATA_");
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     * @throws Exception
     */
    public List<Object> handleInventoryAge(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int snapshotDateIndex = Integer.valueOf(titleMap.get("snapshot-date").toString());
        //获取数据库里面最新的日期
        String latestDay = inventoryAgeMapper.getLatestDay(shop,area);
        if (!StringUtil.isBlank(latestDay)) {
            List<String> firstList = lists.get(1);
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            if (DateUtil.compateTime(latestDay, firstList.get(snapshotDateIndex)) >= 0) {
                return null;
            }
        }
        int skuIndex = Integer.valueOf(titleMap.get("sku").toString());
        int fnskuIndex = Integer.valueOf(titleMap.get("fnsku").toString());
        int asinIndex = Integer.valueOf(titleMap.get("asin").toString());
        int productNameIndex = Integer.valueOf(titleMap.get("product-name").toString());
        int conditionIndex = Integer.valueOf(titleMap.get("condition").toString());
        int avaliableQuantitySellableIndex = Integer.valueOf(titleMap.get("avaliable-quantity(sellable)").toString());
        int qtyWithRemovalsInProgressIndex = Integer.valueOf(titleMap.get("qty-with-removals-in-progress").toString());
        int invAge0To90DaysIndex = Integer.valueOf(titleMap.get("inv-age-0-to-90-days").toString());
        int invAge91To180DaysIndex = Integer.valueOf(titleMap.get("inv-age-91-to-180-days").toString());
        int invAge181To270DaysIndex = Integer.valueOf(titleMap.get("inv-age-181-to-270-days").toString());
        int invAge271To365DaysIndex = Integer.valueOf(titleMap.get("inv-age-271-to-365-days").toString());
        int invAge365PlusDaysIndex = Integer.valueOf(titleMap.get("inv-age-365-plus-days").toString());
        int currencyIndex = Integer.valueOf(titleMap.get("currency").toString());
        int qtyToBeChargedLtsf6MoIndex = Integer.valueOf(titleMap.get("qty-to-be-charged-ltsf-6-mo").toString());
        int projectedLtsf6MoIndex = Integer.valueOf(titleMap.get("projected-ltsf-6-mo").toString());
        int qtyToBeChargedLtsf12MoIndex = Integer.valueOf(titleMap.get("qty-to-be-charged-ltsf-12-mo").toString());
        int projectedLtsf12MoIndex = Integer.valueOf(titleMap.get("projected-ltsf-12-mo").toString());
        int unitsShippedLast7DaysIndex = Integer.valueOf(titleMap.get("units-shipped-last-7-days").toString());
        int unitsShippedLast30DaysIndex = Integer.valueOf(titleMap.get("units-shipped-last-30-days").toString());
        int unitsShippedLast60DaysIndex = Integer.valueOf(titleMap.get("units-shipped-last-60-days").toString());
        int unitsShippedLast90DaysIndex = Integer.valueOf(titleMap.get("units-shipped-last-90-days").toString());
        int alertIndex = Integer.valueOf(titleMap.get("alert").toString());
        int yourPriceIndex = Integer.valueOf(titleMap.get("your-price").toString());
        int salesPriceIndex = Integer.valueOf(titleMap.get("sales_price").toString());
        int lowestPriceNewIndex = Integer.valueOf(titleMap.get("lowest_price_new").toString());
        int lowestPriceUsedIndex = Integer.valueOf(titleMap.get("lowest_price_used").toString());
        int recommendedActionIndex = Integer.valueOf(titleMap.get("Recommended action").toString());
        int healthyInventoryLevelIndex = 9999;
        if (null != titleMap.get("Healthy Inventory Level")) {
            healthyInventoryLevelIndex = Integer.valueOf(String.valueOf(titleMap.get("Healthy Inventory Level")));
        }
        int recommendedSalesPriceIndex = 9999;
        if (null != titleMap.get("Recommended sales price")) {
            recommendedSalesPriceIndex = Integer.valueOf(String.valueOf(titleMap.get("Recommended sales price")));
        }
        int recommendedSaleDurationIndex = 9999;
        if (null != titleMap.get("Recommended sale duration (days)")) {
            recommendedSaleDurationIndex = Integer.valueOf(String.valueOf(titleMap.get("Recommended sale duration (days)")));
        }
        int recommendedRemovalQuantityIndex = 9999;
        if (null != titleMap.get("Recommended Removal Quantity")) {
            recommendedRemovalQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("Recommended Removal Quantity")));
        }
        int estimatedCoseSavingsOfRemovalIndex = 9999;
        if (null != titleMap.get("Estimated cost savings of removal")) {
            estimatedCoseSavingsOfRemovalIndex = Integer.valueOf(String.valueOf(titleMap.get("Estimated cost savings of removal")));
        }
        int sellThroughIndex = 9999;
        if (null != titleMap.get("sell-through")) {
            sellThroughIndex = Integer.valueOf(String.valueOf(titleMap.get("sell-through")));
        }
        int cubicFeetIndex = 9999;
        if (null != titleMap.get("cubic-feet")) {
            cubicFeetIndex = Integer.valueOf(String.valueOf(titleMap.get("cubic-feet")));
        }
        int storageTypeIndex = 9999;
        if (null != titleMap.get("storage-type")) {
            storageTypeIndex = Integer.valueOf(String.valueOf(titleMap.get("storage-type")));
        }
//        int recommendedSalesPriceIndex = Integer.valueOf(String.valueOf(titleMap.get("Recommended sales price")));
//        int recommendedSaleDurationIndex = Integer.valueOf(String.valueOf(titleMap.get("Recommended sale duration (days)")));
//        int recommendedRemovalQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("Recommended Removal Quantity")));
//        int estimatedCoseSavingsOfRemovalIndex = Integer.valueOf(String.valueOf(titleMap.get("Estimated cost savings of removal")));
//        int sellThroughIndex = Integer.valueOf(String.valueOf(titleMap.get("sell-through")));
//        int cubicFeetIndex = Integer.valueOf(String.valueOf(titleMap.get("cubic-feet")));
//        int storageTypeIndex = Integer.valueOf(String.valueOf(titleMap.get("storage-type")));
        List<Object> list = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            InventoryAge inventoryAge = new InventoryAge();
            List<String> stringList = lists.get(i);
            String currencySign = stringList.get(currencyIndex);
            for(int index=0;index<stringList.size();index++){
                if(stringList.get(index).contains(currencySign)){
                    if(stringList.get(index).equals(currencySign)){
                        continue;
                    }
                    stringList.set(index,stringList.get(index).substring(currencySign.length()+1));
                }
            }
            inventoryAge.setShop(shop);
            inventoryAge.setArea(area);
            inventoryAge.setSnapshotDate(stringList.get(snapshotDateIndex));
            inventoryAge.setSku(stringList.get(skuIndex));
            inventoryAge.setFnsku(stringList.get(fnskuIndex));
            inventoryAge.setAsin(stringList.get(asinIndex));
            inventoryAge.setProductName(stringList.get(productNameIndex));
            inventoryAge.setCondition(stringList.get(conditionIndex));
            inventoryAge.setAvaliableQuantitySellable(ToolUtil.valueOfInteger(stringList.get(avaliableQuantitySellableIndex)));
            inventoryAge.setQtyWithRemovalsInProgress(ToolUtil.valueOfInteger(stringList.get(qtyWithRemovalsInProgressIndex)));
            inventoryAge.setInvAge0To90Days(ToolUtil.valueOfInteger(stringList.get(invAge0To90DaysIndex)));
            inventoryAge.setInvAge91To180Days(ToolUtil.valueOfInteger(stringList.get(invAge91To180DaysIndex)));
            inventoryAge.setInvAge181To270Days(ToolUtil.valueOfInteger(stringList.get(invAge181To270DaysIndex)));
            inventoryAge.setInvAge271To365Days(ToolUtil.valueOfInteger(stringList.get(invAge271To365DaysIndex)));
            inventoryAge.setInvAge365PlusDays(ToolUtil.valueOfInteger(stringList.get(invAge365PlusDaysIndex)));
            inventoryAge.setCurrency(stringList.get(currencyIndex));
            inventoryAge.setQtyToBeChargedLtsf6Mo(ToolUtil.valueOfInteger(stringList.get(qtyToBeChargedLtsf6MoIndex)));
            inventoryAge.setProjectedLtsf6Mo(ToolUtil.valueOfDouble(stringList.get(projectedLtsf6MoIndex)));
            inventoryAge.setQtyToBeChargedLtsf12Mo(ToolUtil.valueOfInteger(stringList.get(qtyToBeChargedLtsf12MoIndex)));
            inventoryAge.setProjectedLtsf12Mo(ToolUtil.valueOfDouble(stringList.get(projectedLtsf12MoIndex)));
            inventoryAge.setUnitsShippedLast7Days(ToolUtil.valueOfInteger(stringList.get(unitsShippedLast7DaysIndex)));
            inventoryAge.setUnitsShippedLast30Days(ToolUtil.valueOfInteger(stringList.get(unitsShippedLast30DaysIndex)));
            inventoryAge.setUnitsShippedLast60Days(ToolUtil.valueOfInteger(stringList.get(unitsShippedLast60DaysIndex)));
            inventoryAge.setUnitsShippedLast90Days(ToolUtil.valueOfInteger(stringList.get(unitsShippedLast90DaysIndex)));
            inventoryAge.setAlert(stringList.get(alertIndex));
            inventoryAge.setYourPrice(stringList.get(yourPriceIndex));
            inventoryAge.setSalesPrice(stringList.get(salesPriceIndex));
            inventoryAge.setLowestPriceNew(ToolUtil.valueOfDouble(stringList.get(lowestPriceNewIndex)));
            inventoryAge.setLowestPriceUsed(ToolUtil.valueOfDouble(stringList.get(lowestPriceUsedIndex)));
            inventoryAge.setRecommendedAction(stringList.get(recommendedActionIndex));
            if (healthyInventoryLevelIndex != 9999) {
                inventoryAge.setHealthyInventoryLevel(ToolUtil.valueOfInteger(stringList.get(healthyInventoryLevelIndex)));
            }
            if (recommendedSalesPriceIndex != 9999) {
                inventoryAge.setRecommendedSalesPrice(ToolUtil.valueOfDouble(stringList.get(recommendedSalesPriceIndex)));
            }
            if (recommendedSaleDurationIndex != 9999) {
                inventoryAge.setRecommendedSaleDurationDays(ToolUtil.valueOfInteger(stringList.get(recommendedSaleDurationIndex)));
            }
            if (recommendedRemovalQuantityIndex != 9999) {
                inventoryAge.setRecommendedRemovalQuantity(ToolUtil.valueOfInteger(stringList.get(recommendedRemovalQuantityIndex)));
            }
            if (estimatedCoseSavingsOfRemovalIndex != 9999) {
                inventoryAge.setEstimatedCostSavingsOfRemoval(stringList.get(estimatedCoseSavingsOfRemovalIndex));
            }
            if (sellThroughIndex != 9999) {
                inventoryAge.setSellThrough(stringList.get(sellThroughIndex));
            }
            if (cubicFeetIndex != 9999) {
                inventoryAge.setCubicFeet(stringList.get(cubicFeetIndex));
            }
            if (storageTypeIndex != 9999) {
                inventoryAge.setStorageType(stringList.get(storageTypeIndex));
            }
            list.add(inventoryAge);
        }
        return list;
    }

    /**
     * 每日库存记录
     * 1:时间问题
     */
//    @Scheduled(cron = "0 4 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void everyDayInventoryRecords() {
        baseHandle(TASK_NAME[4], 2);
    }

    /**
     * 每日库存记录参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleEveryDayInventoryRecordsGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        //前天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 00:00:00");
        //前天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_CURRENT_INVENTORY_DATA_", new HashMap<String, String>(2) {{
                    put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                    put("endDate", AmazonMwsUntil.urlEncode(endDate));
                }});
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     * @throws Exception
     */
    public List<Object> handleEveryDayInventoryRecords(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int snapshotDateIndex = Integer.valueOf(String.valueOf(titleMap.get("snapshot-date")));
        //获取数据库里面最新的日期
        String latestDay = everyDayInventoryRecordsMapper.getLatestDay(shop,area);
        if (!StringUtil.isBlank(latestDay)) {
            List<String> firstList = lists.get(1);
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            if (DateUtil.compateTime(latestDay, DateUtil.ISO8601ToGMT_8(firstList.get(snapshotDateIndex))) >= 0) {
                return null;
            }
        }
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int quantityIndex = Integer.valueOf(String.valueOf(titleMap.get("quantity")));
        int fulfillmentCenterIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center-id")));
        int detailedDispositionIndex = Integer.valueOf(String.valueOf(titleMap.get("detailed-disposition")));
        int countryIndex = Integer.valueOf(String.valueOf(titleMap.get("country")));
        List<Object> list = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            EveryDayInventoryRecords records = new EveryDayInventoryRecords();
            List<String> stringList = lists.get(i);
            records.setShop(shop);
            records.setArea(area);
            records.setSnapshotDate(DateUtil.ISO8601ToGMT_8(stringList.get(snapshotDateIndex)));
            records.setFnsku(stringList.get(fnskuIndex));
            records.setSku(stringList.get(skuIndex));
            records.setProductName(stringList.get(productNameIndex));
            records.setQuantity(ToolUtil.valueOfInteger(stringList.get(quantityIndex)));
            records.setFulfillmentCenterId(stringList.get(fulfillmentCenterIdIndex));
            records.setDetailedDisposition(stringList.get(detailedDispositionIndex));
            records.setCountry(stringList.get(countryIndex));
            list.add(records);
        }
        return list;
    }

    /**
     * FBA customer returns
     */
//    @Scheduled(cron = "0 6 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void fbaCustomerReturns() {
        baseHandle(TASK_NAME[5], 2);
    }

    /**
     * FBA customer returns参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleFbaCustomerReturnsGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        //前天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 00:00:00");
        //前天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_CUSTOMER_RETURNS_DATA_", new HashMap<String, String>(2) {{
                    put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                    put("endDate", AmazonMwsUntil.urlEncode(endDate));
                }});
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     * @throws Exception
     */
    public List<Object> handleFbaCustomerReturns(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        String latestDay = fbaCustomerReturnsMapper.getLatestDay(shop,area);
        int returnDateIndex = Integer.valueOf(String.valueOf(titleMap.get("return-date")));
        if (!StringUtil.isBlank(latestDay)) {
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            if (DateUtil.compateTime(latestDay, DateUtil.ISO8601ToGMT_8(lists.get(0).get(returnDateIndex))) >= 0) {
                return null;
            }
        }
        int orderIdIndex = Integer.valueOf(String.valueOf(titleMap.get("order-id")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int asinIndex = Integer.valueOf(String.valueOf(titleMap.get("asin")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int quantityIndex = Integer.valueOf(String.valueOf(titleMap.get("quantity")));
        int fulfillmentCenterIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center-id")));
        int detailedDispositionIndex = Integer.valueOf(String.valueOf(titleMap.get("detailed-disposition")));
        int reasonIndex = Integer.valueOf(String.valueOf(titleMap.get("reason")));
        int statusIndex = Integer.valueOf(String.valueOf(titleMap.get("status")));
        int licensePlateNumberIndex = Integer.valueOf(String.valueOf(titleMap.get("license-plate-number")));
        int customerCommentsIndex = Integer.valueOf(String.valueOf(titleMap.get("customer-comments")));
        List<Object> list = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            FbaCustomerReturns fbaCustomerReturns = new FbaCustomerReturns();
            List<String> stringList = lists.get(i);
            fbaCustomerReturns.setShop(shop);
            fbaCustomerReturns.setArea(area);
            fbaCustomerReturns.setReturnDate(DateUtil.ISO8601ToGMT_8(stringList.get(returnDateIndex)));
            fbaCustomerReturns.setOrderId(stringList.get(orderIdIndex));
            fbaCustomerReturns.setSku(stringList.get(skuIndex));
            fbaCustomerReturns.setAsin(stringList.get(asinIndex));
            fbaCustomerReturns.setFnsku(stringList.get(fnskuIndex));
            fbaCustomerReturns.setProductName(stringList.get(productNameIndex));
            fbaCustomerReturns.setQuantity(ToolUtil.valueOfInteger(stringList.get(quantityIndex)));
            fbaCustomerReturns.setFulfillmentCenterId(stringList.get(fulfillmentCenterIdIndex));
            fbaCustomerReturns.setDetailedDisposition(stringList.get(detailedDispositionIndex));
            fbaCustomerReturns.setReason(stringList.get(reasonIndex));
            fbaCustomerReturns.setStatus(stringList.get(statusIndex));
            fbaCustomerReturns.setLicensePlateNumber(stringList.get(licensePlateNumberIndex));
            fbaCustomerReturns.setCustomerComments(stringList.get(customerCommentsIndex));
            list.add(fbaCustomerReturns);
        }
        return list;
    }

    /**
     * 费用预览
     * 目前是不管怎么样都加上时间插入
     */
//    @Scheduled(cron = "0 8 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void feePreview() {
        baseHandle(TASK_NAME[8], 2);
    }

    /**
     * 费用预览参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleFeePreviewGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_ESTIMATED_FBA_FEES_TXT_DATA_");
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @throws Exception
     */
    public void handleFeePreview(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int fnSkuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int asinIndex = Integer.valueOf(String.valueOf(titleMap.get("asin")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int productGroupIndex = Integer.valueOf(String.valueOf(titleMap.get("product-group")));
        int brandIndex = Integer.valueOf(String.valueOf(titleMap.get("brand")));
        int fulfilledByIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfilled-by")));
        int yourPriceIndex = Integer.valueOf(String.valueOf(titleMap.get("your-price")));
        int salesPriceIndex = Integer.valueOf(String.valueOf(titleMap.get("sales-price")));
        int longestSideIndex = Integer.valueOf(String.valueOf(titleMap.get("longest-side")));
        int medianSideIndex = Integer.valueOf(String.valueOf(titleMap.get("median-side")));
        int shortestSideIndex = Integer.valueOf(String.valueOf(titleMap.get("shortest-side")));
        int lengthAndGirthIndex = Integer.valueOf(String.valueOf(titleMap.get("length-and-girth")));
        int unitOfDimensionIndex = Integer.valueOf(String.valueOf(titleMap.get("unit-of-dimension")));
        int itemPackageWeightIndex = Integer.valueOf(String.valueOf(titleMap.get("item-package-weight")));
        int unitOfWeightIndex = Integer.valueOf(String.valueOf(titleMap.get("unit-of-weight")));
        int currencyIndex = Integer.valueOf(String.valueOf(titleMap.get("currency")));
        int estimatedFeeTotalIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-fee-total")));
        int estimatedReferralFeePerUnitIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-referral-fee-per-unit")));
        int estimatedVariableClosingFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-variable-closing-fee")));
        int estimatedPickPackFeePerUnitIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-pick-pack-fee-per-unit")));
        int estimatedWeightHandlingFeePerUnitIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-weight-handling-fee-per-unit")));
        int expectedFulfillmentFeePerUnitIndex = Integer.valueOf(String.valueOf(titleMap.get("expected-fulfillment-fee-per-unit")));
        for (int i = 1; i < lists.size(); i++) {
            FeePreview feePreview = new FeePreview();
            List<String> row = lists.get(i);
            feePreview.setShop(shop);
            feePreview.setArea(area);
            feePreview.setSku(row.get(skuIndex));
            feePreview.setFnsku(row.get(fnSkuIndex));
            feePreview.setAsin(row.get(asinIndex));
            feePreview.setProductName(row.get(productNameIndex));
            feePreview.setProductGroup(row.get(productGroupIndex));
            feePreview.setBrand(row.get(brandIndex));
            feePreview.setFulfilledBy(row.get(fulfilledByIndex));
            feePreview.setYourPrice(ToolUtil.valueOfDouble(row.get(yourPriceIndex)));
            feePreview.setSalesPrice(ToolUtil.valueOfDouble(row.get(salesPriceIndex)));
            feePreview.setLongestSide(ToolUtil.valueOfDouble(row.get(longestSideIndex)));
            feePreview.setMedianSide(ToolUtil.valueOfDouble(row.get(medianSideIndex)));
            feePreview.setShortestSide(ToolUtil.valueOfDouble(row.get(shortestSideIndex)));
            feePreview.setLengthAndGirth(ToolUtil.valueOfDouble(row.get(lengthAndGirthIndex)));
            feePreview.setUnitOfDimension(row.get(unitOfDimensionIndex));
            feePreview.setItemPackageWeight(ToolUtil.valueOfDouble(row.get(itemPackageWeightIndex)));
            feePreview.setUnitOfWeight(row.get(unitOfWeightIndex));
            feePreview.setCurrency(row.get(currencyIndex));
            feePreview.setEstimatedFeeTotal(row.get(estimatedFeeTotalIndex));
            feePreview.setEstimatedReferralFeePerUnit(row.get(estimatedReferralFeePerUnitIndex));
            feePreview.setEstimatedVariableClosingFee(row.get(estimatedVariableClosingFeeIndex));
            feePreview.setEstimatedPickPackFeePerUnit(row.get(estimatedPickPackFeePerUnitIndex));
            feePreview.setEstimatedWeightHandlingFeePerUnit(row.get(estimatedWeightHandlingFeePerUnitIndex));
            feePreview.setExpectedFulfillmentFeePerUnit(row.get(expectedFulfillmentFeePerUnitIndex));
            feePreview.setCreateTime(DateUtil.getDate());
            if (FIVE_EUROPEAN_COUNTRIES_COUNTRY_CODE.contains(area)) {
                if (null != titleMap.get("expected-efn-fulfilment-fee-per-unit-uk")) {
                    feePreview.setExpectedEfnFulfilmentFeePerUnitUk(String.valueOf(titleMap.get("expected-efn-fulfilment-fee-per-unit-uk")));
                }
                if (null != titleMap.get("expected-efn-fulfilment-fee-per-unit-de")) {
                    feePreview.setExpectedEfnFulfilmentFeePerUnitDe(String.valueOf(titleMap.get("expected-efn-fulfilment-fee-per-unit-de")));
                }
                if (null != titleMap.get("expected-efn-fulfilment-fee-per-unit-fr")) {
                    feePreview.setExpectedEfnFulfilmentFeePerUnitFr(String.valueOf(titleMap.get("expected-efn-fulfilment-fee-per-unit-fr")));
                }
                if (null != titleMap.get("expected-efn-fulfilment-fee-per-unit-it")) {
                    feePreview.setExpectedEfnFulfilmentFeePerUnitIt(String.valueOf(titleMap.get("expected-efn-fulfilment-fee-per-unit-it")));
                }
                if (null != titleMap.get("expected-efn-fulfilment-fee-per-unit-es")) {
                    feePreview.setExpectedEfnFulfilmentFeePerUnitEs(String.valueOf(titleMap.get("expected-efn-fulfilment-fee-per-unit-es")));
                }
            }
            baseInsert(feePreview);
        }
    }

    /**
     * 盘库 每天零点十分更新
     */
//    @Scheduled(cron = "0 10 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void inventory() {
        baseHandle(TASK_NAME[11], 2);
    }

    /**
     * 盘库参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleInventoryGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //前天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 00:00:00");
        //前天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_INVENTORY_ADJUSTMENTS_DATA_", new HashMap<String, String>(2) {{
                    put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                    put("endDate", AmazonMwsUntil.urlEncode(endDate));
                }});
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     * @throws Exception
     */
    public List<Object> handleInventory(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int adjustedDateIndex = Integer.valueOf(String.valueOf(titleMap.get("adjusted-date")));
        String latestDay = inventoryMapper.getLatestDay(shop,area);
        if (!StringUtil.isBlank(latestDay)) {
            List<String> firstList = lists.get(1);
            if (DateUtil.compateTime(latestDay, DateUtil.ISO8601ToGMT_8(firstList.get(adjustedDateIndex))) >= 0) {
                return null;
            }
        }
        int transactionItemIdIndex = Integer.valueOf(String.valueOf(titleMap.get("transaction-item-id")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int fulfillmentCenterIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center-id")));
        int quantityIndex = Integer.valueOf(String.valueOf(titleMap.get("quantity")));
        int reasonIndex = Integer.valueOf(String.valueOf(titleMap.get("reason")));
        int dispositionIndex = Integer.valueOf(String.valueOf(titleMap.get("disposition")));
        List<Object> list = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            Inventory inventory = new Inventory();
            List<String> row = lists.get(i);
            inventory.setShop(shop);
            inventory.setArea(area);
            inventory.setAdjustedDate(DateUtil.ISO8601ToGMT_8(row.get(adjustedDateIndex)));
            inventory.setTransactionItemId(row.get(transactionItemIdIndex));
            inventory.setFnsku(row.get(fnskuIndex));
            inventory.setSku(row.get(skuIndex));
            inventory.setProductName(row.get(productNameIndex));
            inventory.setFulfillmentCenterId(row.get(fulfillmentCenterIdIndex));
            inventory.setQuantity(ToolUtil.valueOfInteger(row.get(quantityIndex)));
            inventory.setReason(row.get(reasonIndex));
            inventory.setDisposition(row.get(dispositionIndex));
            list.add(inventory);
        }
        return list;
    }

    /**
     * 移除
     */
//    @Scheduled(cron = "0 12 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void removalOrderDetail() {
        baseHandle(TASK_NAME[12], 2);
    }

    /**
     * 移除参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleRemovalOrderDetailGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //前天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 00:00:00");
        //前天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_REMOVAL_ORDER_DETAIL_DATA_", new HashMap<String, String>(2) {{
                    put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                    put("endDate", AmazonMwsUntil.urlEncode(endDate));
                }});
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @throws Exception
     */
    public void handleRemovalOrderDetail(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int requestDateIndex = Integer.valueOf(String.valueOf(titleMap.get("request-date")));
        int orderIdIndex = Integer.valueOf(String.valueOf(titleMap.get("order-id")));
        int orderTypeIndex = Integer.valueOf(String.valueOf(titleMap.get("order-type")));
        int orderStatusIndex = Integer.valueOf(String.valueOf(titleMap.get("order-status")));
        int lastUpdatedDateIndex = Integer.valueOf(String.valueOf(titleMap.get("last-updated-date")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int dispositionIndex = Integer.valueOf(String.valueOf(titleMap.get("disposition")));
        int requestedQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("requested-quantity")));
        int cancelledQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("cancelled-quantity")));
        int disposedQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("disposed-quantity")));
        int shippedQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("shipped-quantity")));
        int inProcessQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("in-process-quantity")));
        int removalFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("removal-fee")));
        int currencyIndex = Integer.valueOf(String.valueOf(titleMap.get("currency")));
        List<Object> insertList = new ArrayList<>();
        for (int i = 1; i < lists.size(); i++) {
            RemovalOrderDetail detail = new RemovalOrderDetail();
            List<String> row = lists.get(i);
            detail.setOrderId(row.get(orderIdIndex));
            detail.setSku(row.get(skuIndex));
            detail.setFnsku(row.get(fnskuIndex));
            detail.setDisposition(row.get(dispositionIndex));
            detail.setLastUpdatedDate(DateUtil.ISO8601ToGMT_8(row.get(lastUpdatedDateIndex)));
            RemovalOrderDetail oneInfoByOrderIdAndSkuAndFnSkuAndDisposition = removalOrderDetailMapper.getOneInfoByOrderIdAndSkuAndFnSkuAndDisposition(detail.getOrderId(), detail.getSku(),
                    detail.getFnsku(), detail.getDisposition());
            //如果存在该条数据
            if (null != oneInfoByOrderIdAndSkuAndFnSkuAndDisposition && null != oneInfoByOrderIdAndSkuAndFnSkuAndDisposition.getId()) {
                //如果最后更新时间和本次获取的最后更新时间一致
                if (oneInfoByOrderIdAndSkuAndFnSkuAndDisposition.getLastUpdatedDate().equals(detail.getLastUpdatedDate())) {
                    continue;
                }
            }
            detail.setShop(shop);
            detail.setArea(area);
            detail.setRequestDate(DateUtil.ISO8601ToGMT_8(row.get(requestDateIndex)));
            detail.setOrderType(row.get(orderTypeIndex));
            detail.setOrderStatus(row.get(orderStatusIndex));
            detail.setRequestedQuantity(ToolUtil.valueOfInteger(row.get(requestedQuantityIndex)));
            detail.setCancelledQuantity(ToolUtil.valueOfInteger(row.get(cancelledQuantityIndex)));
            detail.setDisposedQuantity(row.get(disposedQuantityIndex));
            detail.setShippedQuantity(ToolUtil.valueOfInteger(row.get(shippedQuantityIndex)));
            detail.setInProcessQuantity(ToolUtil.valueOfInteger(row.get(inProcessQuantityIndex)));
            detail.setRemovalFee(row.get(removalFeeIndex));
            detail.setCurrency(row.get(currencyIndex));
            if (null == oneInfoByOrderIdAndSkuAndFnSkuAndDisposition || null == oneInfoByOrderIdAndSkuAndFnSkuAndDisposition.getId()) {
                detail.setId(null);
                insertList.add(detail);
            } else {
                detail.setId(oneInfoByOrderIdAndSkuAndFnSkuAndDisposition.getId());
                baseUpdate(detail);
            }
            baseInsertList(insertList);
        }
    }

    /**
     * 移除货件详情
     */
//    @Scheduled(cron = "0 14 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void removalShipmentDetail() {
        baseHandle(TASK_NAME[13], 2);
    }

    /**
     * 移除货件参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleRemovalShipmentDetailGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //前天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 00:00:00");
        //前天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 23:59:59");
        return AmazonMwsApi.getReport2(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_REMOVAL_SHIPMENT_DETAIL_DATA_", new HashMap<String, String>(2) {{
                    put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                    put("endDate", AmazonMwsUntil.urlEncode(endDate));
                }});
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     * @throws Exception
     */
    public List<Object> handleRemovalShipmentDetail(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int requestDateIndex = Integer.valueOf(String.valueOf(titleMap.get("request-date")));
        String latestDay = removalShipmentDetailMapper.getLatestDay(shop,area);
        if (!StringUtil.isBlank(latestDay)) {
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            if (DateUtil.compateTime(latestDay, DateUtil.ISO8601ToGMT_8(lists.get(1).get(requestDateIndex))) >= 0) {
                return null;
            }
        }
        int orderIdIndex = Integer.valueOf(String.valueOf(titleMap.get("order-id")));
        int shipmentDateIndex = Integer.valueOf(String.valueOf(titleMap.get("shipment-date")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int dispositionIndex = Integer.valueOf(String.valueOf(titleMap.get("disposition")));
        int shippedQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("shipped-quantity")));
        int carrierIndex = Integer.valueOf(String.valueOf(titleMap.get("carrier")));
        int trackingNumberIndex = Integer.valueOf(String.valueOf(titleMap.get("tracking-number")));
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            RemovalShipmentDetail detail = new RemovalShipmentDetail();
            List<String> row = lists.get(i);
            detail.setShop(shop);
            detail.setArea(area);
            detail.setRequestDate(DateUtil.ISO8601ToGMT_8(row.get(requestDateIndex)));
            detail.setOrderId(row.get(orderIdIndex));
            detail.setShipmentDate(DateUtil.ISO8601ToGMT_8(row.get(shipmentDateIndex)));
            detail.setSku(row.get(skuIndex));
            detail.setFnsku(row.get(fnskuIndex));
            detail.setDisposition(row.get(dispositionIndex));
            detail.setShippedQuantity(ToolUtil.valueOfInteger(row.get(shippedQuantityIndex)));
            detail.setCarrier(row.get(carrierIndex));
            detail.setTrackingNumber(row.get(trackingNumberIndex));
            insertList.add(detail);
        }
        return insertList;
    }

    /**
     * 已完成订单
     * 1:时间待定---后台获取到数据每次只能获取上一个月的数据
     */
//    @Scheduled(cron = "0 16 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder() {
        baseHandle(TASK_NAME[14], 2);
    }

    /**
     * 已完成订单参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleCompleteOrderGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //前天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 00:00:00");
        //前天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-2) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_CUSTOMER_SHIPMENT_SALES_DATA_", new HashMap<String, String>(2) {{
                    put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                    put("endDate", AmazonMwsUntil.urlEncode(endDate));
                }});
    }




    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     * @throws Exception
     */
    public List<Object> handleCompleteOrder(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int shipmentDateIndex = Integer.valueOf(String.valueOf(titleMap.get("shipment-date")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int asinIndex = Integer.valueOf(String.valueOf(titleMap.get("asin")));
        int fulfillmentCenterIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center-id")));
        int quantityIndex = Integer.valueOf(String.valueOf(titleMap.get("quantity")));
        int amazonOrderIdIndex = Integer.valueOf(String.valueOf(titleMap.get("amazon-order-id")));
        int currencyIndex = Integer.valueOf(String.valueOf(titleMap.get("currency")));
        int itemPricePerUnitIndex = Integer.valueOf(String.valueOf(titleMap.get("item-price-per-unit")));
        int shippingPriceIndex = Integer.valueOf(String.valueOf(titleMap.get("shipping-price")));
        int giftWrapPriceIndex = Integer.valueOf(String.valueOf(titleMap.get("gift-wrap-price")));
        int shipCityIndex = Integer.valueOf(String.valueOf(titleMap.get("ship-city")));
        int shipStateIndex = Integer.valueOf(String.valueOf(titleMap.get("ship-state")));
        int shipPostalCodeIndex = Integer.valueOf(String.valueOf(titleMap.get("ship-postal-code")));
        //获取数据库里面最新的日期
        String latestDay = completeOrderMapper.getLatestDay(shop,area);
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            List<String> list = lists.get(i);
            if (!StringUtil.isBlank(latestDay)) {
                //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
                if (DateUtil.compateTime(latestDay, DateUtil.ISO8601ToGMT_8(list.get(shipmentDateIndex))) >= 0) {
                    continue;
                }
            }
            CompleteOrder completeOrder = new CompleteOrder();
            completeOrder.setShop(shop);
            completeOrder.setArea(area);
            //转为西八区时间
            completeOrder.setShipmentDate(DateUtil.ISO8601ToGMT_8(list.get(shipmentDateIndex)));
            completeOrder.setSku(list.get(skuIndex));
            completeOrder.setFnsku(list.get(fnskuIndex));
            completeOrder.setAsin(list.get(asinIndex));
            completeOrder.setFulfillmentCenterId(list.get(fulfillmentCenterIdIndex));
            completeOrder.setQuantity(list.get(quantityIndex));
            completeOrder.setAmazonOrderId(list.get(amazonOrderIdIndex));
            completeOrder.setCurrency(list.get(currencyIndex));
            completeOrder.setItemPricePerUnit(ToolUtil.valueOfDouble(list.get(itemPricePerUnitIndex)));
            completeOrder.setShippingPrice(ToolUtil.valueOfDouble(list.get(shippingPriceIndex)));
            completeOrder.setGiftWrapPrice(ToolUtil.valueOfDouble(list.get(giftWrapPriceIndex)));
            completeOrder.setShipCity(list.get(shipCityIndex));
            completeOrder.setShipState(list.get(shipStateIndex));
            completeOrder.setShipPostalCode(list.get(shipPostalCodeIndex));
            insertList.add(completeOrder);
        }
        return insertList;
    }
}
