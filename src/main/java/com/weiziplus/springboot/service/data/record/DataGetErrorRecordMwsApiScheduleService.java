package com.weiziplus.springboot.service.data.record;

import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.DataGetErrorRecord;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.scheduled.mwsapi.AmazonMwsApiBaseSchedule;
import com.weiziplus.springboot.scheduled.mwsapi.AmazonMwsApiEveryDaySchedule;
import com.weiziplus.springboot.scheduled.mwsapi.AmazonMwsApiSchedule;
import com.weiziplus.springboot.scheduled.mwsapi.AmazonMwsApiTwoHourSchedule;
import com.weiziplus.springboot.service.shop.AreaService;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.amazon.AmazonMwsApi;
import com.weiziplus.springboot.utils.amazon.AmazonMwsUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/8/23 11:30
 */
@Slf4j
@Service
public class DataGetErrorRecordMwsApiScheduleService extends AmazonMwsApiBaseSchedule {

    @Autowired
    AmazonMwsApiEveryDaySchedule amazonMwsApiEveryDaySchedule;

    @Autowired
    AmazonMwsApiTwoHourSchedule amazonMwsApiTwoHourSchedule;

    @Autowired
    AmazonMwsApiSchedule amazonMwsApiSchedule;

    @Autowired
    AreaService areaService;

    @Autowired
    ShopMapper shopMapper;

    /**
     * 修复亚马逊MwsApi定时任务
     *
     * @param dataGetErrorRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataMwsApi(DataGetErrorRecord dataGetErrorRecord) {
        if (StringUtil.isBlank(dataGetErrorRecord.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getArea())) {
            return ResultUtil.error("国家代码不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getName())) {
            return ResultUtil.error("name不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(dataGetErrorRecord.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        Shop oneInfoByName = shopMapper.getOneInfoByName(dataGetErrorRecord.getShop());
        if(ObjectUtils.isEmpty(oneInfoByName)){
            return ResultUtil.error("店铺不存在");
        }

        Map<String, Object> shopMap = baseFindByClassAndId(Shop.class, oneInfoByName.getId());
        if (null == shopMap) {
            //数据为空证明后台网店配置错误
            return ResultUtil.error("请检查网店配置");
        }
        //如果当前网店名字和传过来的不一致，决绝本次请求
        if (!dataGetErrorRecord.getShop().equals(String.valueOf(shopMap.get(COLUMN_SHOP_NAME)))) {
            return ResultUtil.error("请求服务器错误");
        }
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("亚马逊MwsApi定时任务修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> oldMap = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(oldMap.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        //配置文件检验失败
        Map<String, Object> map = beginScheduleTaskValidate(null,oneInfoByName);
        if (null == map) {
            return ResultUtil.error("请检查店铺为:" + String.valueOf(shopMap.get(COLUMN_SHOP_NAME)) + "的服务器所在的配置文件是否正确");
        }
        //具体字段参考AmazonMwsApiBaseSchedule-----TASK_NAME---MwsApi定时任务任务名称
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            switch (dataGetErrorRecord.getName()) {
                case "预留库存": {
                    return handleReservedInventory(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map);
                }
                case "管理亚马逊库存": {
                    return handleManageFbaInventory(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map);
                }
                case "all order": {
                    return handleAllOrder(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map);
                }
                case "库龄": {
                    return handleInventoryAge(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map);
                }
                case "每日库存记录": {
                    return handleEveryDayInventoryRecords(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "FBA customer returns": {
                    return handleFbaCustomerReturns(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "长期仓储费": {
                    return handleLongTermStorageFeeCharges(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map);
                }
                case "费用预览": {
                    return handleFeePreview(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map);
                }
                case "已接收库存": {
                    return handleReceiveInventory(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "每月库存记录": {
                    return handleEveryMonthInventoryRecords(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "盘库": {
                    return handleInventory(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "移除": {
                    return handleRemovalOrderDetail(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "移除货件详情": {
                    return handleRemovalShipmentDetail(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                case "已完成订单": {
                    return handleCompleteOrder(dataGetErrorRecord.getArea(), dataGetErrorRecord.getId(), map, dataGetErrorRecord.getDate());
                }
                default: {
                    return ResultUtil.error("name错误");
                }
            }
        } catch (Exception e) {
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            log.warn(dataGetErrorRecord.getName() + "修复失败，详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
    }

    /**
     * 处理预留库存
     *
     * @return
     */
    private ResultUtil handleReservedInventory(String areaName, Long id, Map<String, Object> map) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_RESERVED_INVENTORY_DATA_");
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }

            List<Object> insertList = amazonMwsApiTwoHourSchedule.handleReservedInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 管理亚马逊库存
     *
     * @param map
     * @return
     * @throws Exception
     */
    private ResultUtil handleManageFbaInventory(String areaName, Long id, Map<String, Object> map) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_FBA_MYI_UNSUPPRESSED_INVENTORY_DATA_");
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> insertList = amazonMwsApiTwoHourSchedule.handleManageFbaInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 处理all order
     *
     * @param map
     * @return
     * @throws Exception
     */
    private ResultUtil handleAllOrder(String areaName, Long id, Map<String, Object> map) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_ORDER_DATE_");
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> insertList = amazonMwsApiTwoHourSchedule.handleAllOrder(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 处理库龄
     *
     * @param map
     * @return
     * @throws Exception
     */
    private ResultUtil handleInventoryAge(String areaName, Long id, Map<String, Object> map) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params, "_GET_FBA_INVENTORY_AGED_DATA_");
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> insertList = amazonMwsApiEveryDaySchedule.handleInventoryAge(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 处理每日库存记录
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleEveryDayInventoryRecords(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间前天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 00:00:00");
            //出错时间前天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_CURRENT_INVENTORY_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> insertList = amazonMwsApiEveryDaySchedule.handleEveryDayInventoryRecords(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 处理FBA customer returns
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleFbaCustomerReturns(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间前天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 00:00:00");
            //出错时间前天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_CUSTOMER_RETURNS_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> insertList = amazonMwsApiEveryDaySchedule.handleFbaCustomerReturns(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 处理长期仓储费
     *
     * @param map
     * @return
     * @throws Exception
     */
    private ResultUtil handleLongTermStorageFeeCharges(String areaName, Long id, Map<String, Object> map) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //默认返回最近一个月的数据
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_LONGTERM_STORAGE_FEE_CHARGES_DATA_");
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> insertList = amazonMwsApiSchedule.handleLongTermStorageFeeCharges(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(insertList);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 处理费用预览
     *
     * @param map
     * @return
     * @throws Exception
     */
    private ResultUtil handleFeePreview(String areaName, Long id, Map<String, Object> map) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_ESTIMATED_FBA_FEES_TXT_DATA_");
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            amazonMwsApiEveryDaySchedule.handleFeePreview(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 已接收库存
     *
     * @param map
     * @return
     * @throws Exception
     */
    private ResultUtil handleReceiveInventory(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间昨天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -1) + " 00:00:00");
            //出错时间昨天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -1) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_INVENTORY_RECEIPTS_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> list = amazonMwsApiSchedule.handleReceiveInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(list);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 每月库存记录
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleEveryMonthInventoryRecords(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //上个月第一天凌晨---因为数据获取是每月10号，所以取30天为上一个月的某一天
            String startDate = DateUtil.timeToISO8601(DateUtil.getFirstTimeMonth(DateUtil.getDateToFutureDate(date, -30)) + " 00:00:00");
            //上个月最后一天午夜---因为数据获取是每月10号，所以取30天为上一个月的某一天
            String endDate = DateUtil.timeToISO8601(DateUtil.getLastTimeMonth(DateUtil.getDateToFutureDate(date, -30)) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_MONTHLY_INVENTORY_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> list = amazonMwsApiSchedule.handleEveryMonthInventoryRecords(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(list);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 盘库
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleInventory(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间前天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 00:00:00");
            //出错时间前天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_INVENTORY_ADJUSTMENTS_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            List<Object> list = amazonMwsApiEveryDaySchedule.handleInventory(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
            baseInsertList(list);
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 移除
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleRemovalOrderDetail(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间前天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 00:00:00");
            //出错时间前天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_REMOVAL_ORDER_DETAIL_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            amazonMwsApiEveryDaySchedule.handleRemovalOrderDetail(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 移除货件详情
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleRemovalShipmentDetail(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间前天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 00:00:00");
            //出错时间前天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport2(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_REMOVAL_SHIPMENT_DETAIL_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            amazonMwsApiEveryDaySchedule.handleRemovalShipmentDetail(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 已完成订单
     *
     * @param map
     * @param date
     * @return
     * @throws Exception
     */
    private ResultUtil handleCompleteOrder(String areaName, Long id, Map<String, Object> map, String date) throws Exception {
        Map<String, Object> shopMap = (Map<String, Object>) map.get("shop");
        String sellerId = String.valueOf(shopMap.get(COLUMN_SELLER_ID));
        String mwsAuthToken = String.valueOf(shopMap.get(COLUMN_MWS_AUTH_TOKEN));
        String awsAccessKeyId = String.valueOf(shopMap.get(COLUMN_AWS_ACCESS_KEY_ID));
        String secretKey = String.valueOf(shopMap.get(COLUMN_SECRET_KEY));
        List<Area> areaList = (List<Area>) map.get("area");
        Map<String, String> params = new HashMap<>(3);
        for (Area area : areaList) {
            if (!areaName.equals(area.getAreaName())) {
                continue;
            }
            params.put(PARAM_AWS_ACCESS_KEY_ID, AmazonMwsUntil.urlEncode(awsAccessKeyId));
            params.put(PARAM_MWS_AUTH_TOKEN, AmazonMwsUntil.urlEncode(mwsAuthToken));
            params.put(PARAM_SELLER_ID, AmazonMwsUntil.urlEncode(sellerId));
            if (!NOT_MARKETPLACE_ID_LIST_COUNTRY_CODE.contains(area.getMwsCountryCode())) {
                params.put(PARAM_MARKETPLACE_ID_LIST_ID, AmazonMwsUntil.urlEncode(area.getMarketplaceId()));
            }
            //出错时间前天凌晨
            String startDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 00:00:00");
            //出错时间前天午夜
            String endDate = DateUtil.timeToISO8601(DateUtil.dateToFutureDate(date, -2) + " 23:59:59");
            List<List<String>> lists = AmazonMwsApi.getReport(area.getMwsEndPoint(), secretKey, params
                    , "_GET_FBA_FULFILLMENT_CUSTOMER_SHIPMENT_SALES_DATA_", new HashMap<String, String>(2) {{
                        put("StartDate", AmazonMwsUntil.urlEncode(startDate));
                        put("endDate", AmazonMwsUntil.urlEncode(endDate));
                    }});
            if (null == lists) {
                return ResultUtil.error("数据获取失败，请重试");
            }
            if (1 >= lists.size()) {
                return ResultUtil.success();
            }
            amazonMwsApiEveryDaySchedule.handleCompleteOrder(lists, String.valueOf(shopMap.get(COLUMN_SHOP_NAME)), area.getMwsCountryCode());
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(id);
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }
}
