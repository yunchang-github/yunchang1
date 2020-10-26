package com.weiziplus.springboot.scheduled.mwsapi;

import com.weiziplus.springboot.mapper.advertisingInventoryReport.ReservedInventoryMapper;
import com.weiziplus.springboot.mapper.data.record.DataGetErrorRecordMapper;
import com.weiziplus.springboot.mapper.scheduled.AllOrderMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.utils.DateUtil;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.weiziplus.springboot.utils.DateUtil.*;

/**
 * @author wanglongwei
 * @data 2019/8/21 15:15
 */
@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonMwsApiTwoHourSchedule extends AmazonMwsApiBaseSchedule {

    @Autowired
    AllOrderMapper allOrderMapper;

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    ReservedInventoryMapper reservedInventoryMapper;

    @Autowired
    DataGetErrorRecordMapper dataGetErrorRecordMapper;

    @Autowired
    AreaMapper areaMapper;

    /**
     * 通用逻辑处理
     *
     * @param taskName
     */
    protected void baseHandle(String taskName, int type, String taskStartTimes, String taskEndTimes) {
        log.info("***********获取------" + taskName + "-----定时任务开始**********");
        List<Shop> shopList = shopMapper.getAllList();
        try {
            for (Shop shop : shopList) {
                Map<String, Object> map = beginScheduleTaskValidate(taskName, shop);
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
                    if ("EU".equals(area.getMwsCountryCode())) {
                        log.warn("该店铺:" + shop.getShopName() + "有欧洲五国的地区信息，略过该区域" + "任务名称为" + taskName );
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
                        List<List<String>> lists = handleGetReport(taskName, area, secretKey, params, taskStartTimes, taskEndTimes);

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
                                Thread.sleep(600 * errorNum);
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
                int isRightSign = baseHandleByErrorTask(errorTaskName, shop, taskArea, taskStartTimes, taskEndTimes);
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
    protected int baseHandleByErrorTask(String taskName, Shop shop, String taskArea, String taskStartTimes, String taskEndTimes) {
        log.info("***********获取------" + taskName + "-----错误处理任务开始**********"+"店铺名为："+shop +"区域名为："+taskArea);
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
                List<List<String>> lists = handleGetReport(taskName, area, secretKey, params, taskStartTimes, taskEndTimes);

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
            log.info("***********获取------" + taskName + "-----错误处理任务结束**********"+"店铺名为："+shop +"区域名为："+taskArea);
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
    public List<List<String>> handleGetReport(String taskName, Area area, String secretKey, Map<String, String> params, String taskStartTimes, String taskEndTimes) throws Exception {
        List<List<String>> lists = null;
        switch (taskName) {
            case "预留库存": {
                lists = handleReservedInventoryGetReport(area, secretKey, params);
            }
            break;
            case "管理亚马逊库存": {
                lists = handleManageFbaInventoryGetReport(area, secretKey, params);
            }
            break;
            case "all order": {
                params.put("StartDate", taskStartTimes);
                params.put("EndDate", taskEndTimes);
                lists = handleAllOrderGetReport(area, secretKey, params);
            }
            break;
            default: {
            }
        }
        return lists;
    }

    /**
     * @param taskName
     * @param lists
     * @param shopMap
     * @param area
     * @throws Exception
     */
    public void handleInsert(String taskName, List<List<String>> lists, Map<String, Object> shopMap, Area area) throws Exception {
        List<Object> insertList = null;
        switch (taskName) {
            case "预留库存": {
                insertList = handleReservedInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "管理亚马逊库存": {
                insertList = handleManageFbaInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            case "all order": {
                insertList = handleAllOrder(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            }
            break;
            default: {
            }
        }
        try{
            baseInsertList(insertList);
        }catch (Exception e){
            log.error("插入数据是出现错误，"+e);
        }
    }


    /**
     * 预留库存
     * 1:每天六點執行
     */
    //@Scheduled(cron = "0 0 3 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void reservedInventory() {
        baseHandle(TASK_NAME[0], 1, new String(), new String());
    }

    /**
     * 预留库存
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleReservedInventoryGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_RESERVED_INVENTORY_DATA_");
    }

    /**
     * 将获取到的数据转为实体对象数组
     *
     * @param lists
     * @param shop
     * @param area
     * @return
     */
    public List<Object> handleReservedInventory(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int skuIndex = Integer.valueOf(titleMap.get("sku").toString());
        int fnskuIndex = Integer.parseInt(titleMap.get("fnsku").toString());
        int asinIndex = Integer.valueOf(titleMap.get("asin").toString());
        int productNameIndex = Integer.valueOf(titleMap.get("product-name").toString());
        int reservedQtyIndex = Integer.valueOf(titleMap.get("reserved_qty").toString());
        int reservedCustomerordersIndex = Integer.valueOf(titleMap.get("reserved_customerorders").toString());
        int reservedFcTransfersIndex = Integer.valueOf(titleMap.get("reserved_fc-transfers").toString());
        int reservedFcProcessingIndex = Integer.valueOf(titleMap.get("reserved_fc-processing").toString());
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            ReservedInventory reservedInventory = new ReservedInventory();
            List<String> stringList = lists.get(i);
            reservedInventory.setShop(shop);
            reservedInventory.setArea(area);
            reservedInventory.setSku(stringList.get(skuIndex));
            reservedInventory.setFnsku(stringList.get(fnskuIndex));
            reservedInventory.setAsin(stringList.get(asinIndex));
            reservedInventory.setProductName(stringList.get(productNameIndex));
            reservedInventory.setReservedQty(ToolUtil.valueOfInteger(stringList.get(reservedQtyIndex)));
            reservedInventory.setReservedCustomerorders(ToolUtil.valueOfInteger(stringList.get(reservedCustomerordersIndex)));
            reservedInventory.setReservedFcTransfers(ToolUtil.valueOfInteger(stringList.get(reservedFcTransfersIndex)));
            reservedInventory.setReservedFcProcessing(ToolUtil.valueOfInteger(stringList.get(reservedFcProcessingIndex)));
            reservedInventory.setCreateTime(DateUtil.getDate());
            reservedInventory.setSellerId(shopMapper.getOneInfoByName(shop).getSellerId());
            insertList.add(reservedInventory);
        }
        return insertList;
    }

    /**
     * 管理亚马逊库存
     * 1:每次爬取的时间时区问题
     */
    //@Scheduled(cron = "0 15 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void manageFbaInventory() {
        baseHandle(TASK_NAME[1], 1, new String(), new String());
    }

    /**
     * 管理亚马逊库存参数
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleManageFbaInventoryGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_FBA_MYI_UNSUPPRESSED_INVENTORY_DATA_");
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
    public List<Object> handleManageFbaInventory(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i), i);
        }
        int skuIndex = Integer.valueOf(titleMap.get("sku").toString());
        int fnskuIndex = Integer.parseInt(titleMap.get("fnsku").toString());
        int asinIndex = Integer.valueOf(titleMap.get("asin").toString());
        int productNameIndex = Integer.valueOf(titleMap.get("product-name").toString());
        int conditionNameIndex = Integer.valueOf(titleMap.get("condition").toString());
        int yourPriceIndex = Integer.valueOf(titleMap.get("your-price").toString());
        int mfnListingExistsIndex = Integer.valueOf(titleMap.get("mfn-listing-exists").toString());
        int mfnFulfillableQuantityIndex = Integer.valueOf(titleMap.get("mfn-fulfillable-quantity").toString());
        int afnListingExistsIndex = Integer.valueOf(titleMap.get("afn-listing-exists").toString());
        int afnWarehouseQuantityIndex = Integer.valueOf(titleMap.get("afn-warehouse-quantity").toString());
        int afnFulfillableQuantityIndex = Integer.valueOf(titleMap.get("afn-fulfillable-quantity").toString());
        int afnUnsellableQuantityIndex = Integer.valueOf(titleMap.get("afn-unsellable-quantity").toString());
        int afnReservedQuantityIndex = Integer.valueOf(titleMap.get("afn-reserved-quantity").toString());
        int afnTotalQuantityIndex = Integer.valueOf(titleMap.get("afn-total-quantity").toString());
        int perUnitVolumeIndex = Integer.valueOf(titleMap.get("per-unit-volume").toString());
        int afnInboundWorkingQuantityIndex = Integer.valueOf(titleMap.get("afn-inbound-working-quantity").toString());
        int afnInboundShippedQuantityIndex = Integer.valueOf(titleMap.get("afn-inbound-shipped-quantity").toString());
        int afnInboundReceivingQuantityIndex = Integer.valueOf(titleMap.get("afn-inbound-receiving-quantity").toString());
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            ManageFbaInventory manageFbaInventory = new ManageFbaInventory();
            List<String> stringList = lists.get(i);
            manageFbaInventory.setShop(shop);
            manageFbaInventory.setArea(area);
            manageFbaInventory.setSku(stringList.get(skuIndex));
            manageFbaInventory.setFnsku(stringList.get(fnskuIndex));
            manageFbaInventory.setAsin(stringList.get(asinIndex));
            manageFbaInventory.setProductName(stringList.get(productNameIndex));
            manageFbaInventory.setCondition(stringList.get(conditionNameIndex));
            manageFbaInventory.setYourPrice(stringList.get(yourPriceIndex));
            manageFbaInventory.setMfnListingExists(stringList.get(mfnListingExistsIndex));
            manageFbaInventory.setMfnFulfillableQuantity(stringList.get(mfnFulfillableQuantityIndex));
            manageFbaInventory.setAfnListingExists(stringList.get(afnListingExistsIndex));
            manageFbaInventory.setAfnWarehouseQuantity(stringList.get(afnWarehouseQuantityIndex));
            manageFbaInventory.setAfnFulfillableQuantity(stringList.get(afnFulfillableQuantityIndex));
            manageFbaInventory.setAfnUnsellableQuantity(stringList.get(afnUnsellableQuantityIndex));
            manageFbaInventory.setAfnReservedQuantity(stringList.get(afnReservedQuantityIndex));
            manageFbaInventory.setAfnTotalQuantity(stringList.get(afnTotalQuantityIndex));
            manageFbaInventory.setPerUnitVolume(stringList.get(perUnitVolumeIndex));
            manageFbaInventory.setAfnInboundWorkingQuantity(stringList.get(afnInboundWorkingQuantityIndex));
            manageFbaInventory.setAfnInboundShippedQuantity(stringList.get(afnInboundShippedQuantityIndex));
            manageFbaInventory.setAfnInboundReceivingQuantity(stringList.get(afnInboundReceivingQuantityIndex));
            manageFbaInventory.setCreateTime(DateUtil.getDate());
            manageFbaInventory.setSellerId(shopMapper.getOneInfoByName(shop).getSellerId());
            insertList.add(manageFbaInventory);
        }
        return insertList;
    }

    /**
     * all order
     * 1:不需要五分钟请求一次,有缓存机制拿不到最新数据---目前一天请求一次
     */
    //@Scheduled(cron = "0 05 02 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void allOrder() throws ParseException {
        String taskStartTimes = AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-30))));
        String taskEndTimes = AmazonMwsUntil.urlEncode(timeToISO8601((getDate())));
        baseHandle(TASK_NAME[2], 1, taskStartTimes, taskEndTimes);
    }

    /**
     * all order
     * 获取all order 的历史数据
     */
//    @Scheduled(cron = "0 05 21 * * ?")
//    @Scheduled(cron = "0 20 17 11 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void allOrderHistory() throws ParseException {
        int endDays = 0;
        int startDays = 0;
        for (int index = 0; index < 24; index++) {
            endDays += 30;
            startDays = endDays + 30;
            String taskStartTimes = AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-startDays))));
            String taskEndTimes = AmazonMwsUntil.urlEncode(timeToISO8601((getFutureDateDay(-endDays))));
            baseHandle(TASK_NAME[2], 1, taskStartTimes, taskEndTimes);
            try {
                Thread.sleep(1800000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * all order
     *
     * @param area
     * @param secretKey
     * @param params
     * @return
     * @throws Exception
     */
    private List<List<String>> handleAllOrderGetReport(Area area, String secretKey, Map<String, String> params) throws Exception {
        return AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_ORDER_DATE_");
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
    public List<Object> handleAllOrder(List<List<String>> lists, String shop, String area) throws Exception {
        List<String> titleList = lists.get(0);
        Map<String, Object> titleMap = new HashMap<>(titleList.size());
        for (int i = 0; i < titleList.size(); i++) {
            titleMap.put(titleList.get(i).trim(), i);
        }
        int amazonOrderIdIndex = Integer.valueOf(titleMap.get("amazon-order-id").toString());
        int merchantOrderIdIndex = Integer.valueOf(titleMap.get("merchant-order-id").toString());
        int purchaseDateIndex = Integer.valueOf(titleMap.get("purchase-date").toString());
        int lastUpdatedDateIndex = Integer.valueOf(titleMap.get("last-updated-date").toString());
        int orderStatusIndex = Integer.valueOf(titleMap.get("order-status").toString());
        int fulfillmentChannelIndex = Integer.valueOf(titleMap.get("fulfillment-channel").toString());
        int salesChannelIndex = Integer.valueOf(titleMap.get("sales-channel").toString());
        int orderChannelIndex = Integer.valueOf(titleMap.get("order-channel").toString());
        int urlIndex = Integer.valueOf(titleMap.get("url").toString());
        int shipServiceLevelIndex = Integer.valueOf(titleMap.get("ship-service-level").toString());
        int productNameIndex = Integer.valueOf(titleMap.get("product-name").toString());
        int skuIndex = Integer.valueOf(titleMap.get("sku").toString());
        int asinIndex = Integer.valueOf(titleMap.get("asin").toString());
        int itemStatusIndex = Integer.valueOf(titleMap.get("item-status").toString());
        int quantityIndex = Integer.valueOf(titleMap.get("quantity").toString());
        int currencyIndex = Integer.valueOf(titleMap.get("currency").toString());
        int itemPriceIndex = Integer.valueOf(titleMap.get("item-price").toString());
        int itemTaxIndex = Integer.valueOf(titleMap.get("item-tax").toString());
        int shippingPriceIndex = Integer.valueOf(titleMap.get("shipping-price").toString());
        int shippingTaxIndex = Integer.valueOf(titleMap.get("shipping-tax").toString());
        int giftWrapPriceIndex = Integer.valueOf(titleMap.get("gift-wrap-price").toString());
        int giftWrapTaxIndex = Integer.valueOf(titleMap.get("gift-wrap-tax").toString());
        int itemPromotionDiscountIndex = Integer.valueOf(titleMap.get("item-promotion-discount").toString());
        int shipPromotionDiscountIndex = Integer.valueOf(titleMap.get("ship-promotion-discount").toString());
        //测试代码
        System.out.println("ship city Index:" + titleMap.get("ship-city").toString());
        log.info("ship city Index:" + titleMap.get("ship-city").toString());
//        int shipCityIndex = Integer.valueOf(titleMap.get("ship-city").toString());
        int shipStateIndex = Integer.valueOf(titleMap.get("ship-state").toString());
        int shipPostalCodeIndex = Integer.valueOf(titleMap.get("ship-postal-code").toString());
        int shipCountryIndex = Integer.valueOf(titleMap.get("ship-country").toString());
        int promotionIdsIndex = Integer.valueOf(titleMap.get("promotion-ids").toString());
        int isBusinessOrderIndex = 9999;
        if (null != titleMap.get("is-business-order")) {
            isBusinessOrderIndex = Integer.valueOf(titleMap.get("is-business-order").toString());
        }
        int purchaseOrderNumberIndex = 9999;
        if (null != titleMap.get("purchase-order-number")) {
            purchaseOrderNumberIndex = Integer.valueOf(titleMap.get("purchase-order-number").toString());
        }
        int priceDesignationIndex = 9999;
        if (null != titleMap.get("price-designation")) {
            priceDesignationIndex = Integer.valueOf(titleMap.get("price-designation").toString());
        }
        int customizedUrlIndex = 9999;
        if (null != titleMap.get("customized-url")) {
            customizedUrlIndex = Integer.valueOf(titleMap.get("customized-url").toString());
        }
        int customizedPageIndex = 9999;
        if (null != titleMap.get("customized-page")) {
            customizedPageIndex = Integer.valueOf(titleMap.get("customized-page").toString());
        }
        int isSoldByAbIndex = 9999;
        if (null != titleMap.get("is-sold-by-ab")) {
            isSoldByAbIndex = Integer.valueOf(titleMap.get("is-sold-by-ab").toString());
        }
        List<Object> insertList = new ArrayList<>(lists.size());
        for (int i = 1; i < lists.size(); i++) {
            List<String> stringList = lists.get(i);
            AllOrder allOrder = new AllOrder();
            allOrder.setAmazonOrderId(stringList.get(amazonOrderIdIndex));
            allOrder.setSku(stringList.get(skuIndex));
            allOrder.setAsin(stringList.get(asinIndex));
            allOrder.setLastUpdatedDate(DateUtil.ISO8601ToGMT_8(stringList.get(lastUpdatedDateIndex)));
            AllOrder oneInfoByAmazonOrderIdAndSkuAndAsin = allOrderMapper.getOneInfoByAmazonOrderIdAndSkuAndAsin(
                    allOrder.getAmazonOrderId(), allOrder.getSku(), allOrder.getAsin(), area);
            //如果存在该条数据
            if (null != oneInfoByAmazonOrderIdAndSkuAndAsin && null != oneInfoByAmazonOrderIdAndSkuAndAsin.getId()) {
                //如果最后更新时间和本次获取的最后更新时间一致
                if (oneInfoByAmazonOrderIdAndSkuAndAsin.getLastUpdatedDate().equals(allOrder.getLastUpdatedDate())) {
                    continue;
                }
            }
            allOrder.setShop(shop);
            allOrder.setArea(area);
            allOrder.setMerchantOrderId(stringList.get(merchantOrderIdIndex));
            allOrder.setPurchaseDate(DateUtil.ISO8601ToGMT_8(stringList.get(purchaseDateIndex)));
            allOrder.setOrderStatus(stringList.get(orderStatusIndex));
            allOrder.setFulfillmentChannel(stringList.get(fulfillmentChannelIndex));
            allOrder.setSalesChannel(stringList.get(salesChannelIndex));
            allOrder.setOrderChannel(stringList.get(orderChannelIndex));
            allOrder.setUrl(stringList.get(urlIndex));
            allOrder.setShipServiceLevel(stringList.get(shipServiceLevelIndex));
            allOrder.setProductName(stringList.get(productNameIndex));
            allOrder.setItemStatus(stringList.get(itemStatusIndex));
            allOrder.setQuantity(stringList.get(quantityIndex));
            allOrder.setCurrency(stringList.get(currencyIndex));
            allOrder.setItemPrice(stringList.get(itemPriceIndex));
            allOrder.setItemTax(stringList.get(itemTaxIndex));
            allOrder.setShippingPrice(stringList.get(shippingPriceIndex));
            allOrder.setShippingTax(stringList.get(shippingTaxIndex));
            allOrder.setGiftWrapPrice(stringList.get(giftWrapPriceIndex));
            allOrder.setGiftWrapTax(stringList.get(giftWrapTaxIndex));
            allOrder.setItemPromotionDiscount(stringList.get(itemPromotionDiscountIndex));
            allOrder.setShipPromotionDiscount(stringList.get(shipPromotionDiscountIndex));
            //测试代码
//            System.out.println("ship city:" + stringList.get(shipCityIndex));
//            log.info("ship city:" + stringList.get(shipCityIndex));
//            allOrder.setShipCity(stringList.get(shipCityIndex));
            allOrder.setShipState(stringList.get(shipStateIndex));
            allOrder.setShipPostalCode(stringList.get(shipPostalCodeIndex));
            allOrder.setShipCountry(stringList.get(shipCountryIndex));
            allOrder.setPromotionIds(stringList.get(promotionIdsIndex));
            if (isBusinessOrderIndex != 9999) {
                allOrder.setIsBusinessOrder(stringList.get(isBusinessOrderIndex));
            }

            if (purchaseOrderNumberIndex != 9999) {
                allOrder.setPurchaseOrderNumber(stringList.get(purchaseOrderNumberIndex));
            }

            if (priceDesignationIndex != 9999) {
                allOrder.setPriceDesignation(stringList.get(priceDesignationIndex));
            }

            if (customizedUrlIndex != 9999) {
                allOrder.setPriceDesignation(stringList.get(customizedUrlIndex));
            }

            if (customizedPageIndex != 9999) {
                allOrder.setPriceDesignation(stringList.get(customizedPageIndex));
            }

            if (isSoldByAbIndex != 9999) {
                allOrder.setPriceDesignation(stringList.get(isSoldByAbIndex));
            }

            allOrder.setCreateTime(DateUtil.getDate());
            allOrder.setSellerId(shopMapper.getOneInfoByName(shop).getSellerId());
            //如果没有该数据---插入数据
            if (null == oneInfoByAmazonOrderIdAndSkuAndAsin || null == oneInfoByAmazonOrderIdAndSkuAndAsin.getId()) {
                allOrder.setId(null);
                insertList.add(allOrder);
            } else {
                allOrder.setId(oneInfoByAmazonOrderIdAndSkuAndAsin.getId());
                baseUpdate(allOrder);
            }
        }
        return insertList;
    }
}
