package com.weiziplus.springboot.scheduled.mwsapi;

import com.weiziplus.springboot.mapper.scheduled.*;
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

import java.util.*;

/**
 * @author WeiziPlus
 */
@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonMwsApiSchedule extends AmazonMwsApiBaseSchedule {

    @Autowired
    MonthlyStorageFeesMapper monthlyStorageFeesMapper;

    @Autowired
    LongTermStorageFeeChargesMapper longTermStorageFeeChargesMapper;

    @Autowired
    EveryMonthInventoryRecordsMapper everyMonthInventoryRecordsMapper;

    @Autowired
    ShopMapper shopMapper;

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
            }}
        } catch (Exception e) {
            log.warn("×××××" + taskName + "×××定时任务出错××××××××××详情:" + e);
        } finally {
            log.info("***********获取------" + taskName + "-----定时任务结束**********");
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
            case "月度仓储费": {
                lists = handleMonthlyStorageFeesGetReport(area, secretKey, params);
            }
            break;
            case "长期仓储费": {
                lists = handleLongTermStorageFeeChargesGetReport(area, secretKey, params);
            }
            break;
            case "已接收库存": {
                lists = handleReceiveInventoryGetReport(area, secretKey, params);
            }
            break;
            case "每月库存记录": {
                lists = handleEveryMonthInventoryRecordsGetReport(area, secretKey, params);
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
            case "月度仓储费": {
                insertList = handleMonthlyStorageFees(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "长期仓储费": {
                insertList = handleLongTermStorageFeeCharges(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "已接收库存": {
                insertList = handleReceiveInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "每月库存记录": {
                insertList = handleEveryMonthInventoryRecords(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            default: {
            }
        }
        baseInsertList(insertList);
    }

    /**
     * 月度仓储费
     */
//    @Scheduled(cron = "0 1 1 10 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void monthlyStorageFees() {
        baseHandle(TASK_NAME[6], 0);
    }

    /**
     * 月度仓储费参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleMonthlyStorageFeesGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //上个月第一天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFirstTimeMonth(DateUtil.getFutureDateDay(-30)) + " 00:00:00");
        //上个月最后一天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getLastTimeMonth(DateUtil.getFutureDateDay(-30)) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_STORAGE_FEE_CHARGES_DATA_", new HashMap<String, String>(2) {{
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
    public List<Object> handleMonthlyStorageFees(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int monthOfChargeIndex = Integer.valueOf(String.valueOf(titleMap.get("month-of-charge")));
        //获取数据库里面最新的日期
        String latestDay = monthlyStorageFeesMapper.getLatestDay(shop,area);
        if (!StringUtil.isBlank(latestDay)) {
            List<String> firstList = lists.get(1);
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            if (DateUtil.compateTime(latestDay, firstList.get(monthOfChargeIndex)) >= 0) {
                return null;
            }
        }
        int asinIndex = Integer.valueOf(String.valueOf(titleMap.get("asin")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int fulfillmentCenterIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center")));
        int countryCodeIndex = Integer.valueOf(String.valueOf(titleMap.get("country-code")));
        int longestSideIndex = Integer.valueOf(String.valueOf(titleMap.get("longest-side")));
        int medianSideIndex = Integer.valueOf(String.valueOf(titleMap.get("median-side")));
        int shortestSideIndex = Integer.valueOf(String.valueOf(titleMap.get("shortest-side")));
        int measurementUnitsIndex = Integer.valueOf(String.valueOf(titleMap.get("measurement-units")));
        int weightIndex = Integer.valueOf(String.valueOf(titleMap.get("weight")));
        int weightUnitsIndex = Integer.valueOf(String.valueOf(titleMap.get("weight-units")));
        int itemVolumeIndex = Integer.valueOf(String.valueOf(titleMap.get("item-volume")));
        int volumeUnitsIndex = Integer.valueOf(String.valueOf(titleMap.get("volume-units")));
        int averageQuantityOnHandIndex = Integer.valueOf(String.valueOf(titleMap.get("average-quantity-on-hand")));
        int averageQuantityPendingRemovalIndex = Integer.valueOf(String.valueOf(titleMap.get("average-quantity-pending-removal")));
        int estimatedTotalItemVolumeIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-total-item-volume")));
        int storageRateIndex = Integer.valueOf(String.valueOf(titleMap.get("storage-rate")));
        int currencyIndex = Integer.valueOf(String.valueOf(titleMap.get("currency")));
        int estimatedMonthlyStorageFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("estimated-monthly-storage-fee")));
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            MonthlyStorageFees fees = new MonthlyStorageFees();
            List<String> row = lists.get(i);
            fees.setShop(shop);
            fees.setArea(area);
            fees.setAsin(row.get(asinIndex));
            fees.setFnsku(row.get(fnskuIndex));
            fees.setProductName(row.get(productNameIndex));
            fees.setFulfillmentCenter(row.get(fulfillmentCenterIndex));
            fees.setCountryCode(row.get(countryCodeIndex));
            fees.setLongestSide(ToolUtil.valueOfDouble(row.get(longestSideIndex)));
            fees.setMedianSide(ToolUtil.valueOfDouble(row.get(medianSideIndex)));
            fees.setShortestSide(ToolUtil.valueOfDouble(row.get(shortestSideIndex)));
            fees.setMeasurementUnits(row.get(measurementUnitsIndex));
            fees.setWeight(ToolUtil.valueOfDouble(row.get(weightIndex)));
            fees.setWeightUnits(row.get(weightUnitsIndex));
            fees.setItemVolume(ToolUtil.valueOfDouble(row.get(itemVolumeIndex)));
            fees.setVolumeUnits(row.get(volumeUnitsIndex));
            fees.setAverageQuantityOnHand(ToolUtil.valueOfDouble(row.get(averageQuantityOnHandIndex)));
            fees.setAverageQuantityPendingRemoval(ToolUtil.valueOfDouble(row.get(averageQuantityPendingRemovalIndex)));
            fees.setEstimatedTotalItemVolume(ToolUtil.valueOfDouble(row.get(estimatedTotalItemVolumeIndex)));
            fees.setMonthOfCharge(row.get(monthOfChargeIndex));
            fees.setStorageRate(ToolUtil.valueOfDouble(row.get(storageRateIndex)));
            fees.setCurrency(row.get(currencyIndex));
            fees.setEstimatedMonthlyStorageFee(ToolUtil.valueOfDouble(row.get(estimatedMonthlyStorageFeeIndex)));
            insertList.add(fees);
        }
        return insertList;
    }

    /**
     * 长期仓储费
     */
//    @Scheduled(cron = "0 1 1 20 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void longTermStorageFeeCharges() {
        baseHandle(TASK_NAME[7], 0);
    }

    /**
     * 长期仓储费
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleLongTermStorageFeeChargesGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //上个月第一天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFirstTimeMonth(DateUtil.getFutureDateDay(-30)) + " 00:00:00");
        //上个月最后一天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getLastTimeMonth(DateUtil.getFutureDateDay(-30)) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_LONGTERM_STORAGE_FEE_CHARGES_DATA_", new HashMap<String, String>(2) {{
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
    public List<Object> handleLongTermStorageFeeCharges(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        //获取数据库里面最新的日期
        String latestDay = longTermStorageFeeChargesMapper.getLatestDay(shop,area);
        int snapshotDateIndex = Integer.valueOf(String.valueOf(titleMap.get("snapshot-date")));
        if (!StringUtil.isBlank(latestDay)) {
            //如果当前获取的数据的时间和数据库时间一致或者早于数据库时间，本次获取数据将跳过
            if (DateUtil.compateTime(latestDay, DateUtil.ISO8601ToGMT_8(lists.get(0).get(snapshotDateIndex))) >= 0) {
                return null;
            }
        }
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int fnSkuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int asinIndex = Integer.valueOf(String.valueOf(titleMap.get("asin")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int conditionIndex = Integer.valueOf(String.valueOf(titleMap.get("condition")));
        int qtyCharged12MoLongTermStorageFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("qty-charged-12-mo-long-term-storage-fee")));
        int perUnitVolumeIndex = Integer.valueOf(String.valueOf(titleMap.get("per-unit-volume")));
        int currencyIndex = Integer.valueOf(String.valueOf(titleMap.get("currency")));
        int w12MoLongTermsStorageFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("12-mo-long-terms-storage-fee")));
        int qtyCharged6MoLongTermStorageFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("qty-charged-6-mo-long-term-storage-fee")));
        int w6MoLongTermsStorageFeeIndex = Integer.valueOf(String.valueOf(titleMap.get("6-mo-long-terms-storage-fee")));
        int volumeUnitIndex = Integer.valueOf(String.valueOf(titleMap.get("volume-unit")));
        int countryIndex = Integer.valueOf(String.valueOf(titleMap.get("country")));
        int enrolledInSmallAndLightIndex = Integer.valueOf(String.valueOf(titleMap.get("enrolled-in-small-and-light")));
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            LongTermStorageFeeCharges charges = new LongTermStorageFeeCharges();
            List<String> row = lists.get(i);
            charges.setShop(shop);
            charges.setArea(area);
            charges.setSnapshotDate(DateUtil.ISO8601ToGMT_8(row.get(snapshotDateIndex)));
            charges.setSku(row.get(skuIndex));
            charges.setFnsku(row.get(fnSkuIndex));
            charges.setAsin(row.get(asinIndex));
            charges.setProductName(row.get(productNameIndex));
            charges.setCondition(row.get(conditionIndex));
            charges.setQtyCharged12MoLongTermStorageFee(ToolUtil.valueOfInteger(row.get(qtyCharged12MoLongTermStorageFeeIndex)));
            charges.setPerUnitVolume(ToolUtil.valueOfDouble(row.get(perUnitVolumeIndex)));
            charges.setCurrency(row.get(currencyIndex));
            charges.setColumn12MoLongTermsStorageFee(ToolUtil.valueOfDouble(row.get(w12MoLongTermsStorageFeeIndex)));
            charges.setQtyCharged6MoLongTermStorageFee(ToolUtil.valueOfInteger(row.get(qtyCharged6MoLongTermStorageFeeIndex)));
            charges.setColumn6MoLongTermsStorageFee(ToolUtil.valueOfDouble(row.get(w6MoLongTermsStorageFeeIndex)));
            charges.setVolumeUnit(row.get(volumeUnitIndex));
            charges.setCountry(row.get(countryIndex));
            charges.setEnrolledInSmallAndLight(row.get(enrolledInSmallAndLightIndex));
            insertList.add(charges);
        }
        return insertList;
    }

    /**
     * 已接收库存  每天6点45执行
     * 一天只获取一次
     */
//    @Scheduled(cron = "0 45 6 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void receiveInventory() {
        baseHandle(TASK_NAME[9], 0);
    }

    /**
     * 已接收库存参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleReceiveInventoryGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {

        //昨天凌晨
        String startDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-1) + " 00:00:00");
        //昨天午夜
        String endDate = DateUtil.timeToISO8601(DateUtil.getFetureDate(-1) + " 23:59:59");
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_INVENTORY_RECEIPTS_DATA_", new HashMap<String, String>(2) {{
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
    public List<Object> handleReceiveInventory(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int receivedDateIndex = Integer.valueOf(String.valueOf(titleMap.get("received-date")));
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int quantityIndex = Integer.valueOf(String.valueOf(titleMap.get("quantity")));
        int fbaShipmentIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fba-shipment-id")));
        int fulfillmentCenterIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center-id")));
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            ReceiveInventory inventory = new ReceiveInventory();
            List<String> row = lists.get(i);
            inventory.setShop(shop);
            inventory.setArea(area);
            inventory.setReceivedDate(DateUtil.ISO8601ToGMT_8(row.get(receivedDateIndex)));
            inventory.setFnsku(row.get(fnskuIndex));
            inventory.setSku(row.get(skuIndex));
            inventory.setProductName(row.get(productNameIndex));
            inventory.setQuantity(ToolUtil.valueOfInteger(row.get(quantityIndex)));
            inventory.setFbaShipmentId(row.get(fbaShipmentIdIndex));
            inventory.setFulfillmentCenterId(row.get(fulfillmentCenterIdIndex));
            insertList.add(inventory);
        }
        return insertList;
    }

    /**
     * 每月库存记录
     */
//    @Scheduled(cron = "0 3 1 10 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void everyMonthInventoryRecords() {
        baseHandle(TASK_NAME[10], 0);
    }

    /**
     * 每月库存记录参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleEveryMonthInventoryRecordsGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        //上个月第一天凌晨---因为数据获取是每月10号，所以取30天为上一个月的某一天
        String startDate = DateUtil.timeToISO8601(DateUtil.getFirstTimeMonth(DateUtil.getFutureDateDay(-30)) + " 00:00:00");
        //上个月最后一天午夜---因为数据获取是每月10号，所以取30天为上一个月的某一天
        String endDate = DateUtil.timeToISO8601(DateUtil.getLastTimeMonth(DateUtil.getFutureDateDay(-30)) + " 23:59:59");

        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                , "_GET_FBA_FULFILLMENT_MONTHLY_INVENTORY_DATA_", new HashMap<String, String>(2) {{
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
    public List<Object> handleEveryMonthInventoryRecords(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int monthIndex = Integer.valueOf(String.valueOf(titleMap.get("month")));
        String latestDay = everyMonthInventoryRecordsMapper.getLatestDay(shop,area);
        if (!StringUtil.isBlank(latestDay)) {
            List<String> firstList = lists.get(1);
            String m = firstList.get(monthIndex);
            if (DateUtil.compateTime(latestDay, (m.substring(3, 7) + "-" + m.substring(0, 2))) >= 0) {
                return null;
            }
        }
        int fnskuIndex = Integer.valueOf(String.valueOf(titleMap.get("fnsku")));
        int skuIndex = Integer.valueOf(String.valueOf(titleMap.get("sku")));
        int productNameIndex = Integer.valueOf(String.valueOf(titleMap.get("product-name")));
        int averageQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("average-quantity")));
        int endQuantityIndex = Integer.valueOf(String.valueOf(titleMap.get("end-quantity")));
        int fulfillmentCenterIdIndex = Integer.valueOf(String.valueOf(titleMap.get("fulfillment-center-id")));
        int detailedDispositionIndex = Integer.valueOf(String.valueOf(titleMap.get("detailed-disposition")));
        int countryIndex = Integer.valueOf(String.valueOf(titleMap.get("country")));
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            EveryMonthInventoryRecords records = new EveryMonthInventoryRecords();
            List<String> row = lists.get(i);
            records.setShop(shop);
            records.setArea(area);
            String month = row.get(monthIndex);
            records.setMonth(month.substring(3, 7) + "-" + month.substring(0, 2));
            records.setFnsku(row.get(fnskuIndex));
            records.setSku(row.get(skuIndex));
            records.setProductName(row.get(productNameIndex));
            records.setAverageQuantity(ToolUtil.valueOfDouble(row.get(averageQuantityIndex)));
            records.setEndQuantity(ToolUtil.valueOfInteger(row.get(endQuantityIndex)));
            records.setFulfillmentCenterId(row.get(fulfillmentCenterIdIndex));
            records.setDetailedDisposition(row.get(detailedDispositionIndex));
            records.setCountry(row.get(countryIndex));
            insertList.add(records);
        }
        return insertList;
    }
}
