package com.weiziplus.springboot.scheduled.caravanapi;

import com.alibaba.fastjson.JSONArray;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.caravan.*;
import com.weiziplus.springboot.mapper.logistics.MskuShippbatchDataMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import com.weiziplus.springboot.utils.caravan.CaravanApiUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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

/**
 * 马帮api定时任务
 *
 * @author wanglongwei
 * @data 2019/9/2 16:15
 */
@Component
@Configuration
@Service
@Slf4j
//@EnableScheduling   //马帮后面再用到
public class CaravanApiSchedule extends BaseService {

    @Autowired
    MskuShippbatchDataMapper mskuShippbatchDataMapper;

    @Autowired
    StockWarehouseDataMapper stockWarehouseDataMapper;

    @Autowired
    PurchaseMapper purchaseMapper;

    @Autowired
    MskuMapper mskuMapper;

    @Autowired
    OrderDataMapper orderDataMapper;

    @Autowired
    BillsDataMapper billsDataMapper;

    @Autowired
    AccountDataMapper accountDataMapper;

    @Autowired
    StorageLogDataMapper storageLogDataMapper;

    /**
     * 将api出错信息插入数据库
     *
     * @param taskName
     * @param errorMsg
     */
    private void handleErrorSchedule(String taskName, StringBuffer errorMsg) {
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setShop("马帮api");
        record.setArea("马帮api");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = format.format(date);
        record.setDate(format1);
        //是否处理，0:未处理,1:已处理',
        record.setIsHandle(0);
        //类型:马帮api定时任务
        record.setType(5);
        record.setName(taskName);
        record.setRemark("马帮api定时任务，出错时间：" + format1 + ",出错详情:" + errorMsg);
        record.setCreateTime(DateUtil.getFutureDateTime(0));
        baseInsert(record);
    }

    /**
     * 通用逻辑处理
     *
     * @param taskName
     */
    public void baseHandle(String taskName, String startTime, String endTime) {
        log.info("************马帮api********" + taskName + "定时任务开始******************");
        //最大出错次数
        int errorMaxNum = 3;
        StringBuffer errMsg = new StringBuffer();
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        //昨天的日期
        String lastDate = DateUtil.getFetureDate(-1);
        for (int i = 0; i < errorMaxNum; i++) {
            try {
                //数据是否处理成功
                boolean handleSuccess = false;
                switch (taskName) {
                    case "调拨发货": {
                        handleSuccess = baseHandleGetDate("msku-shippbatch-data", taskName, startTime, endTime);
                    }
                    break;
                    case "库存查询": {
                        //清除昨天的数据
                        stockWarehouseDataMapper.deleteByDate(lastDate);
                        handleSuccess = baseHandleGetDate("stock-warehouse-data", taskName, startTime, endTime);
                    }
                    break;
                    case "采购单": {
                        //清除昨天的数据
                        purchaseMapper.deleteByDate(lastDate);
                        handleSuccess = baseHandleGetDate("purchase-data", taskName, startTime, endTime);
                    }
                    break;
                    case "出入库流水": {
                        handleSuccess = baseHandleGetDate("storage-log-data", taskName, startTime, endTime);
                    }
                    break;
                    case "收付款单": {
                        handleSuccess = baseHandleGetDate("bills-data", taskName, startTime, endTime);
                    }
                    break;
                    case "MSKU列表": {
                        //清除昨天的数据
                        mskuMapper.deleteByDate(lastDate);
                        handleSuccess = baseHandleGetDate("msku-data", taskName, startTime, endTime);
                    }
                    break;
                    case "账户日志": {
                        handleSuccess = baseHandleGetDate("account-data", taskName, startTime, endTime);
                    }
                    break;
                    case "补货计划": {
                        handleSuccess = baseHandleGetDate("msku-ship-data", taskName, startTime, endTime);
                    }
                    break;
                    case "订单列表": {
                        handleSuccess = baseHandleGetDate("order-data", taskName, startTime, endTime);
                    }
                    break;
                    default: {
                    }
                }
                //数据处理成功，本地定时任务结束
                if (handleSuccess) {
                    log.info("************马帮api********" + taskName + "定时任务结束******************");
                    return;
                }
                errMsg.append("第").append(i + 1).append("次，数据获取失败，详情请看日志信息");
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            } catch (Exception e) {
                //debug打印错误详情
                log.debug("马帮api，" + taskName + "错误，详情:", e);
                log.warn("马帮api，" + taskName + "错误，详情:" + e);
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                errMsg.append("第").append(i + 1).append("次，数据获取失败，详情:").append(e);
            }
        }
        handleErrorSchedule(taskName, errMsg);
        //定时任务出错
        log.info("************马帮api********" + taskName + "定时任务结束******************");
    }

    /**
     * 通用逻辑处理
     *
     * @param moduleParam
     * @param type
     * @return
     * @throws Exception
     */
    public boolean baseHandleGetDate(String moduleParam, String type, String startTime, String endTime) throws Exception {

        //掉一遍接口获取一共有多少数据
        Map<String, Object> module = CaravanApiUtil.getClient(new HashMap<String, String>(2) {{
            put("module", moduleParam);
            put("page", "1");
            put("updateTimeStart", startTime);
            put("updateTimeEnd", endTime);
        }});

        //null代表获取数据失败，详情查看控制台
        if (null == module) {
            log.warn("马帮api获取出错");
            return false;
        }

        int pageCount = ToolUtil.valueOfInteger(StringUtil.valueOf(module.get("pageCount")));
        //错误次数
        int errorNum = 0;
        for (int i = 1; i <= pageCount; i++) {
            //延迟一秒
            Thread.sleep(1L);
            String page = String.valueOf(i);
            Map<String, Object> client = CaravanApiUtil.getClient(new HashMap<String, String>(2) {{
                put("module", moduleParam);
                put("page", page);
                put("updateTimeStart", startTime);
                put("updateTimeEnd", endTime);
            }});
            if (null == client) {
                errorNum++;
                //如果错误次数超过3次，本次处理数据失败
                if (errorNum > 3) {
                    return false;
                }
                //重新获取数据
                i -= 1;
                continue;
            }
            List<Map> data = JSONArray.parseArray(StringUtil.valueOf(client.get("data")), Map.class);
            switch (type) {
                case "调拨发货": {
                    handleAllocationAndDelivery(data);
                }
                break;
                case "库存查询": {
                    handleInventoryQuery(data);
                }
                break;
                case "采购单": {
                    handlePurchase(data);
                }
                break;
                case "出入库流水": {
                    handleStorageLogData(data);
                }
                break;
                case "收付款单": {
                    handleBillsData(data);
                }
                break;
                case "MSKU列表": {
                    handleMsku(data);
                }
                break;
                case "账户日志": {
                    handleAccountData(data);
                }
                break;
                case "补货计划": {
                    handleMskuShipData(data);
                }
                break;
                case "订单列表": {
                    handleOrderData(data);
                }
                break;
                default: {
                    log.warn("马帮api没有对应类型，类型:" + type);
                    //没有对应类型
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 调拨发货
     * 5分钟
     */
//    @Scheduled(cron = "10 0/5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void allocationAndDelivery() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date endTime = calendar.getTime();
        String end = simpleDateFormat.format(endTime);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 5);
        calendar.set(Calendar.SECOND, 0);
        Date startTime = calendar.getTime();
        String start = simpleDateFormat.format(startTime);
        baseHandle("调拨发货", start, end);
    }

    /**
     * 处理调拨发货逻辑
     *
     * @return
     * @throws Exception
     */
    private void handleAllocationAndDelivery(List<Map> meterList) throws Exception {
        MskuShippbatchData mskuShippbatchData = null;
        MskuShippbatchDataItem mskuShippbatchDataItem = null;
        MskuShippbatchPack mskuShippbatchPack = null;
        Map<String, Object> conditionMap = null;
        MskuShippbatchItemPack mskuShippbatchItemPack = null;
        Map<String, Long> cdMap = new HashMap<String, Long>();//传递用的map;
        for (Map map : meterList) {
            if (map.get("shippNo") == null) {
                break;
            }
            String shippNo = (String) map.get("shippNo");
            //调拨shippNo判断是否添加过
            mskuShippbatchData = mskuShippbatchDataMapper.getMskuShippbatchDataByShippNo(shippNo);
            if (mskuShippbatchData == null) {  //添加
                mskuShippbatchData = new MskuShippbatchData();
                mskuShippbatchData.setCompanyid((String) map.get("companyId"));
                mskuShippbatchData.setCountrycode((String) map.get("countryCode"));
                mskuShippbatchData.setExpresstime((String) map.get("expressTime"));
                mskuShippbatchData.setShippname((String) map.get("shippName"));
                mskuShippbatchData.setShippno(shippNo);
                mskuShippbatchData.setShop("");
                mskuShippbatchData.setStatus((String) map.get("status"));
                mskuShippbatchData.setTimecreate((String) map.get("timeCreate"));
                mskuShippbatchData.setTimeupdate((String) map.get("timeUpdate"));
                mskuShippbatchData.setWarehouse((String) map.get("warehouse"));
                mskuShippbatchData.setWarehouseid((String) map.get("warehouseid"));
                mskuShippbatchDataMapper.insert(mskuShippbatchData);
            } else {//修改
                mskuShippbatchData.setCompanyid((String) map.get("companyId"));
                mskuShippbatchData.setCountrycode((String) map.get("countryCode"));
                mskuShippbatchData.setExpresstime((String) map.get("expressTime"));
                mskuShippbatchData.setShippname((String) map.get("shippName"));
                mskuShippbatchData.setShippno(shippNo);
                mskuShippbatchData.setShop("");
                mskuShippbatchData.setStatus((String) map.get("status"));
                mskuShippbatchData.setTimecreate((String) map.get("timeCreate"));
                mskuShippbatchData.setTimeupdate((String) map.get("timeUpdate"));
                mskuShippbatchData.setWarehouse((String) map.get("warehouse"));
                mskuShippbatchData.setWarehouseid((String) map.get("warehouseid"));
                baseUpdate(mskuShippbatchData);
            }
            long idData = mskuShippbatchData.getId();
            if (map.get("item") != null) {
                String itemStr = map.get("item").toString();
                List<Map> itemArray = JSONArray.parseArray(itemStr, Map.class);
                for (Map itemMap : itemArray) {
                    String platformSku = (String) itemMap.get("platformSku");
                    //根据调拨id和货件sku查询
                    conditionMap = new HashMap<String, Object>();
                    conditionMap.put("idData", idData);
                    conditionMap.put("platformSku", platformSku);
                    mskuShippbatchDataItem = mskuShippbatchDataMapper.getMskuShippbatchDataByDataIdAndSku(conditionMap);
                    if (mskuShippbatchDataItem == null) {
                        mskuShippbatchDataItem = new MskuShippbatchDataItem();
                        mskuShippbatchDataItem.setApplyquantity((String) itemMap.get("applyQuantity"));
                        mskuShippbatchDataItem.setFbastockid((String) itemMap.get("fbaStockId"));
                        mskuShippbatchDataItem.setShippedquantity((String) itemMap.get("shippedQuantity"));
                        mskuShippbatchDataItem.setReceivedquantity((String) itemMap.get("receivedQuantity"));
                        mskuShippbatchDataItem.setDelieverquantity((String) itemMap.get("deliveryQuantity"));
                        mskuShippbatchDataItem.setPlatformsku(platformSku);
                        mskuShippbatchDataItem.setMskuShippbatchDataId(idData);
                        mskuShippbatchDataMapper.insertItem(mskuShippbatchDataItem);
                    } else {
                        mskuShippbatchDataItem.setApplyquantity((String) itemMap.get("applyQuantity"));
                        mskuShippbatchDataItem.setFbastockid((String) itemMap.get("fbaStockId"));
                        mskuShippbatchDataItem.setShippedquantity((String) itemMap.get("shippedQuantity"));
                        mskuShippbatchDataItem.setReceivedquantity((String) itemMap.get("receivedQuantity"));
                        mskuShippbatchDataItem.setDelieverquantity((String) itemMap.get("deliveryQuantity"));
                        mskuShippbatchDataItem.setPlatformsku(platformSku);
                        mskuShippbatchDataItem.setMskuShippbatchDataId(idData);
                        baseUpdate(mskuShippbatchDataItem);
                    }
                    cdMap.put(mskuShippbatchDataItem.getFbastockid(), mskuShippbatchDataItem.getId());
                }
            }

            //==放置箱start==
            String packStr = MapUtils.getString(map, "pack", null);
            if (StringUtil.isBlank(packStr)) {
                continue;
            }
            List<Map> packArray = JSONArray.parseArray(packStr, Map.class);
            for (Map packMap : packArray) {
                String packNo = (String) packMap.get("packNo");
                //查询箱号存在
                mskuShippbatchPack = mskuShippbatchDataMapper.getMskuShippbatchPackByPackNo(packNo);
                if (mskuShippbatchPack == null) {
                    mskuShippbatchPack = new MskuShippbatchPack();
                    mskuShippbatchPack.setHeight((String) packMap.get("height"));
                    mskuShippbatchPack.setLength((String) packMap.get("length"));
                    mskuShippbatchPack.setPackno(packNo);
                    mskuShippbatchPack.setWeight((String) packMap.get("weight"));
                    mskuShippbatchPack.setWidth((String) packMap.get("width"));
                    mskuShippbatchDataMapper.insertMskuShippbatchPack(mskuShippbatchPack);
                } else {
                    mskuShippbatchPack.setHeight((String) packMap.get("height"));
                    mskuShippbatchPack.setLength((String) packMap.get("length"));
                    mskuShippbatchPack.setPackno(packNo);
                    mskuShippbatchPack.setWeight((String) packMap.get("weight"));
                    mskuShippbatchPack.setWidth((String) packMap.get("width"));
                    baseUpdate(mskuShippbatchPack);
                }
                long packId = mskuShippbatchPack.getId();
                String skuInfo1 = StringUtil.valueOf(packMap.get("skuInfo"));
//                if (StringUtil.isBlank(skuInfo1)) {
//                    continue;
//                }
                List<Map> skuInfoArray = JSONArray.parseArray(skuInfo1, Map.class);
                for (Map skuInfo : skuInfoArray) {
                    String fbastockId = skuInfo.get("fbastockId").toString();
                    String dqt = skuInfo.get("delieverQuantity").toString();
                    Long itemId = cdMap.get(fbastockId);
					/*
					 * if (null == itemId) { continue; }
					 */
                    //查询fbastockId、idData
                    conditionMap = new HashMap<String, Object>();
                    conditionMap.put("packId", packId);
                    conditionMap.put("itemId", itemId);
                    mskuShippbatchItemPack = mskuShippbatchDataMapper.getMskuShippbatchDataBypackIdAndItemId(conditionMap);

                    if (mskuShippbatchItemPack == null) {
                        mskuShippbatchItemPack = new MskuShippbatchItemPack();
                        mskuShippbatchItemPack.setDelieverquantity(dqt);
                        mskuShippbatchItemPack.setItemid(itemId);
                        mskuShippbatchItemPack.setPackid(packId);
                        mskuShippbatchItemPack.setPackNo(packNo);
                        mskuShippbatchDataMapper.insertMskuShippbatchItemPack(mskuShippbatchItemPack);
                    } else {
                        mskuShippbatchItemPack.setDelieverquantity(dqt);
                        mskuShippbatchItemPack.setItemid(itemId);
                        mskuShippbatchItemPack.setPackid(packId);
                        mskuShippbatchItemPack.setPackNo(packNo);
                        baseUpdate(mskuShippbatchItemPack);
                    }
                }

            }

            //==放置箱end==
            //清空map
            cdMap.clear();

        }
    }

    /**
     * 库存查询
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void inventoryQuery() {
        //昨天凌晨到今天凌晨数据
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("库存查询", start, end);
    }

    /**
     * 处理库存查询
     *
     * @return
     */
    private void handleInventoryQuery(List<Map> data) throws Exception {
        List<Object> list = new ArrayList<>(data.size());
        String fetureDate = DateUtil.getFetureDate(0);
        for (Map map : data) {
            StockWarehouseData warehouseData = new StockWarehouseData();
            warehouseData.setDate(fetureDate);
            String stockId = StringUtil.valueOf(map.get("stockId"));
            String wearhouseId = StringUtil.valueOf(map.get("wearhouseId"));
            warehouseData.setCompanyid(StringUtil.valueOf(map.get("companyId")));
            warehouseData.setStockid(stockId);
            warehouseData.setStockname(StringUtil.valueOf(map.get("stockName")));
            warehouseData.setStocksku(StringUtil.valueOf(map.get("stockSku")));
            warehouseData.setWearhouseid(wearhouseId);
            warehouseData.setWarhousename(StringUtil.valueOf(map.get("warhouseName")));
            warehouseData.setStockquantity(StringUtil.valueOf(map.get("stockQuantity")));
            warehouseData.setShippingquantity(StringUtil.valueOf(map.get("shippingQuantity")));
            warehouseData.setDefaultcost(StringUtil.valueOf(map.get("defaultCost")));
            warehouseData.setTotalvalue(StringUtil.valueOf(map.get("totalValue")));
            warehouseData.setLastdepottime(StringUtil.valueOf(map.get("lastDepotTime")));
            warehouseData.setLaststoragetime(StringUtil.valueOf(map.get("lastStorageTime")));
            warehouseData.setCreatetime(StringUtil.valueOf(map.get("createTime")));
            StockWarehouseData oneInfo = stockWarehouseDataMapper.getOneInfoByStockIdAndWearhouseId(stockId, wearhouseId);
            if (null != oneInfo && null != oneInfo.getId()) {
                warehouseData.setId(oneInfo.getId());
                baseUpdate(warehouseData);
            } else {
                warehouseData.setId(null);
                list.add(warehouseData);
            }
        }
        baseInsertList(list);
    }

    /**
     * 采购单
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 2 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void purchase() {
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("采购单", start, end);
    }

    /**
     * 处理采购
     *
     * @param data
     * @throws Exception
     */
    private void handlePurchase(List<Map> data) throws Exception {
        List<Long> purchaseDataIds = new ArrayList<>();
        //item列表
        List<Object> itemsList = new ArrayList<>();
        String fetureDate = DateUtil.getFetureDate(0);
        PurchaseData purchaseData = new PurchaseData();
        for (Map map : data) {
            String purchaseId = StringUtil.valueOf(map.get("purchaseId"));
            purchaseData.setDate(fetureDate);
            purchaseData.setPurchaseid(purchaseId);
            purchaseData.setCompanyid(StringUtil.valueOf(map.get("companyId")));
            purchaseData.setGroupid(StringUtil.valueOf(map.get("groupId")));
            purchaseData.setFreightsum(StringUtil.valueOf(map.get("freightSum")));
            purchaseData.setDiscountamount(StringUtil.valueOf(map.get("discountAmount")));
            purchaseData.setTaxamount(StringUtil.valueOf(map.get("taxAmount")));
            purchaseData.setTotalamount(StringUtil.valueOf(map.get("totalAmount")));
            purchaseData.setPretaxtotalamount(StringUtil.valueOf(map.get("pretaxTotalAmount")));
            purchaseData.setQuantitysum(StringUtil.valueOf(map.get("quantitySum")));
            purchaseData.setFlag(StringUtil.valueOf(map.get("flag")));
            purchaseData.setFlagdesc(StringUtil.valueOf(map.get("flagDesc")));
            purchaseData.setNowarehousing(StringUtil.valueOf(map.get("noWarehousing")));
            purchaseData.setProviderid(StringUtil.valueOf(map.get("providerId")));
            purchaseData.setProvidername(StringUtil.valueOf(map.get("providerName")));
            purchaseData.setTargetwarehouseid(StringUtil.valueOf(map.get("targetWarehouseId")));
            purchaseData.setTargetwarehousename(StringUtil.valueOf(map.get("targetWarehouseName")));
            purchaseData.setContent(StringUtil.valueOf(map.get("content")));
            purchaseData.setCreateoperid(StringUtil.valueOf(map.get("createOperId")));
            purchaseData.setCreateopername(StringUtil.valueOf(map.get("createOperName")));
            purchaseData.setCreatetime(StringUtil.valueOf(map.get("createTime")));
            purchaseData.setUpdatetime(StringUtil.valueOf(map.get("updateTime")));
            purchaseData.setAli1688sumpayment(StringUtil.valueOf(map.get("ali1688SumPayment")));
            purchaseData.setLaststoragetime(StringUtil.valueOf(map.get("lastStorageTime")));
            purchaseData.setBuyerid(StringUtil.valueOf(map.get("buyerId")));
            purchaseData.setBuyername(StringUtil.valueOf(map.get("buyerName")));
            PurchaseData oneInfoByPurchaseId = purchaseMapper.getOneInfoByPurchaseId(purchaseId);
            Long purchaseDataId = null;
            if (null != oneInfoByPurchaseId && null != oneInfoByPurchaseId.getId()) {
                Long id = oneInfoByPurchaseId.getId();
                purchaseData.setId(id);
                baseUpdate(purchaseData);
                purchaseDataIds.add(id);
            } else {
                purchaseData.setId(null);
                baseInsert(purchaseData, true);
            }
            purchaseDataId = purchaseData.getId();
            //如果没有item，添加到list里面同一插入数据库
            if (null == map.get("item")) {
                continue;
            }
            List<Map> items = JSONArray.parseArray(StringUtil.valueOf(map.get("item")), Map.class);
            for (Map item : items) {
                PurchaseDataItem dataItem = new PurchaseDataItem();
                dataItem.setPurchaseDataId(purchaseDataId);
                dataItem.setStockid(StringUtil.valueOf(item.get("stockId")));
                dataItem.setStocksku(StringUtil.valueOf(item.get("stockSku")));
                dataItem.setStockskunamecn(StringUtil.valueOf(item.get("stockSkuNameCn")));
                dataItem.setPurchasenum(ToolUtil.valueOfInteger(StringUtil.valueOf(item.get("purchaseNum"))));
                dataItem.setEnterwarehousenum(ToolUtil.valueOfInteger(StringUtil.valueOf(item.get("enterWarehouseNum"))));
                dataItem.setWastagenum(StringUtil.valueOf(item.get("wastageNum")));
                dataItem.setSellprice(StringUtil.valueOf(item.get("sellPrice")));
                dataItem.setRemark(StringUtil.valueOf(item.get("remark")));
                dataItem.setExpressmoney(StringUtil.valueOf(item.get("expressMoney")));
                dataItem.setBuyerid(StringUtil.valueOf(item.get("buyerId")));
                dataItem.setBuyername(StringUtil.valueOf(item.get("buyerName")));
                dataItem.setPretaxunitprice(StringUtil.valueOf(item.get("pretaxUnitPrice")));
                dataItem.setPretaxenterwarehousvalue(StringUtil.valueOf(item.get("pretaxEnterWarehousValue")));
                dataItem.setIsdelete(StringUtil.valueOf(item.get("isDelete")));
                itemsList.add(dataItem);
            }
        }
        if (0 < purchaseDataIds.size()) {
            purchaseMapper.deletePurchaseDataItemByPurchaseDataIds(purchaseDataIds);
        }
        baseInsertList(itemsList);
    }

    /**
     * 出入库流水
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 3 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void storageLogData() {
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("出入库流水", start, end);
    }

    /**
     * 处理出入库流水
     *
     * @param lists
     * @throws Exception
     */
    private void handleStorageLogData(List<Map> lists) throws Exception {
        List<Object> insertList = new ArrayList<>(lists.size());
        String fetureDate = DateUtil.getFetureDate(0);
        for (Map row : lists) {
            StorageLogData data = new StorageLogData();
            data.setDate(fetureDate);
            Long logId = ToolUtil.valueOfLong(StringUtil.valueOf(row.get("id")));
            data.setLogId(logId);
            data.setCompanyid(StringUtil.valueOf(row.get("companyId")));
            data.setShopname(StringUtil.valueOf(row.get("shopName")));
            data.setStockid(StringUtil.valueOf(row.get("stockId")));
            data.setStocksku(StringUtil.valueOf(row.get("stockSKu")));
            data.setStockskuname(StringUtil.valueOf(row.get("stockSkuName")));
            data.setWearhouseid(StringUtil.valueOf(row.get("wearhouseId")));
            data.setWearhousename(StringUtil.valueOf(row.get("wearhouseName")));
            data.setCtype(StringUtil.valueOf(row.get("ctype")));
            data.setQuantityafter(StringUtil.valueOf(row.get("quantityAfter")));
            data.setQuantity(StringUtil.valueOf(row.get("quantity")));
            data.setPrice(StringUtil.valueOf(row.get("price")));
            data.setPricesum(StringUtil.valueOf(row.get("pricesum")));
            data.setOperid(StringUtil.valueOf(row.get("operId")));
            data.setOpername(StringUtil.valueOf(row.get("operName")));
            data.setDocumentnum(StringUtil.valueOf(row.get("documentNum")));
            data.setDocumenttype(StringUtil.valueOf(row.get("documentType")));
            data.setRemark(StringUtil.valueOf(row.get("remark")));
            data.setTimecreate(StringUtil.valueOf(row.get("timecreate")));
            data.setCreatedatetime(StringUtil.valueOf(row.get("createDateTime")));
            StorageLogData oneInfoByLogId = storageLogDataMapper.getOneInfoByLogId(logId);
            if (null != oneInfoByLogId && null != oneInfoByLogId.getId()) {
                data.setId(oneInfoByLogId.getId());
                baseUpdate(data);
            } else {
                data.setId(null);
                insertList.add(data);
            }
        }
        baseInsertList(insertList);
    }

    /**
     * 收付款单
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 4 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void billsData() {
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("收付款单", start, end);
    }

    /**
     * 处理收付款单
     *
     * @param lists
     * @throws Exception
     */
    private void handleBillsData(List<Map> lists) throws Exception {
        List<Object> insertList = new ArrayList<>(lists.size());
        String fetureDate = DateUtil.getFetureDate(0);
        for (Map row : lists) {
            BillsData data = new BillsData();
            String payMentOrderId = StringUtil.valueOf(row.get("payMentOrderId"));
            String orderNum = StringUtil.valueOf(row.get("orderNum"));
            String payTime = StringUtil.valueOf(row.get("payTime"));
            data.setDate(fetureDate);
            data.setCompanyid(StringUtil.valueOf(row.get("companyId")));
            data.setPaymentorderid(payMentOrderId);
            data.setOrdernum(orderNum);
            data.setAli1688orderid(StringUtil.valueOf(row.get("ali1688OrderId")));
            data.setAccountid(StringUtil.valueOf(row.get("accountId")));
            data.setOrdertype(StringUtil.valueOf(row.get("orderType")));
            data.setCurrency(StringUtil.valueOf(row.get("currency")));
            data.setPaymentterm(StringUtil.valueOf(row.get("paymentTerm")));
            data.setPaymenttermid(StringUtil.valueOf(row.get("paymentTermId")));
            data.setPaymenttermdesc(StringUtil.valueOf(row.get("paymentTermDesc")));
            data.setPaymentsupplierid(StringUtil.valueOf(row.get("paymentSupplierId")));
            data.setPaymentsuppliername(StringUtil.valueOf(row.get("paymentSupplierName")));
            data.setTotalamount(StringUtil.valueOf(row.get("totalAmount")));
            data.setAmount(StringUtil.valueOf(row.get("amount")));
            data.setStatus(StringUtil.valueOf(row.get("status")));
            data.setPrepaytime(StringUtil.valueOf(row.get("prepayTime")));
            data.setCreaterid(StringUtil.valueOf(row.get("createrId")));
            data.setCreatername(StringUtil.valueOf(row.get("createrName")));
            data.setCreatetime(StringUtil.valueOf(row.get("createTime")));
            data.setUpdatetime(StringUtil.valueOf(row.get("updateTime")));
            data.setAuditorid(StringUtil.valueOf(row.get("auditorId")));
            data.setAuditor(StringUtil.valueOf(row.get("auditor")));
            data.setPayerid(StringUtil.valueOf(row.get("payerId")));
            data.setPayername(StringUtil.valueOf(row.get("payerName")));
            data.setPaytime(payTime);
            data.setComment(StringUtil.valueOf(row.get("comment")));
            BillsData oneInfoBillsData = billsDataMapper.getOneInfoByPayMentOrderIdAndOrderNumAndPayTime(
                    payMentOrderId, orderNum, payTime);
            if (null != oneInfoBillsData && null != oneInfoBillsData.getId()) {
                data.setId(oneInfoBillsData.getId());
                baseUpdate(data);
            } else {
                data.setId(null);
                insertList.add(data);
            }
        }
        baseInsertList(insertList);
    }

    /**
     * MSKU列表
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 6 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void msku() {
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("MSKU列表", start, end);
    }

    /**
     * 处理msku列表
     *
     * @param lists
     * @throws Exception
     */
    private void handleMsku(List<Map> lists) throws Exception {
        List<Object> insertList = new ArrayList<>(lists.size());
        String fetureDate = DateUtil.getFetureDate(0);
        for (Map row : lists) {
            Msku msku = new Msku();
            msku.setDate(fetureDate);
            String shops = StringUtil.valueOf(row.get("shops"));
            String mskuRow = StringUtil.valueOf(row.get("msku"));
            String amazonsite = StringUtil.valueOf(row.get("amazonsite"));
            msku.setCompanyid(StringUtil.valueOf(row.get("companyId")));
            msku.setShops(shops);
            msku.setShopsname(StringUtil.valueOf(row.get("shopsName")));
            msku.setMsku(mskuRow);
            msku.setCommoditylinks(StringUtil.valueOf(row.get("commodityLinks")));
            msku.setStockid(StringUtil.valueOf(row.get("stockId")));
            msku.setStocksku(StringUtil.valueOf(row.get("stockSku")));
            msku.setAmazonsite(amazonsite);
            msku.setGroundingtime(StringUtil.valueOf(row.get("groundingTime")));
            msku.setDeveloperid(StringUtil.valueOf(row.get("developerId")));
            msku.setDevelopername(StringUtil.valueOf(row.get("developerName")));
            msku.setCreatetime(StringUtil.valueOf(row.get("createTime")));
            msku.setUpdatetime(StringUtil.valueOf(row.get("updateTime")));
            msku.setStatus(StringUtil.valueOf(row.get("status")));
            msku.setStatusdesc(StringUtil.valueOf(row.get("statusdesc")));
            Msku oneInfo = mskuMapper.getOneInfoByShopsAndMskuAndAmazonsite(shops, mskuRow, amazonsite);
            if (null != oneInfo && null != oneInfo.getId()) {
                msku.setId(oneInfo.getId());
                baseUpdate(msku);
            } else {
                msku.setId(null);
                insertList.add(msku);
            }
        }
        baseInsertList(insertList);
    }

    /**
     * 账户日志
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 7 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void accountData() {
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("账户日志", start, end);
    }

    /**
     * 处理账户日志
     *
     * @param lists
     * @throws Exception
     */
    private void handleAccountData(List<Map> lists) throws Exception {
        List<Object> insertList = new ArrayList<>(lists.size());
        String fetureDate = DateUtil.getFetureDate(0);
        for (Map row : lists) {
            AccountData data = new AccountData();
            data.setDate(fetureDate);
            String accountId = StringUtil.valueOf(row.get("accountId"));
            String orderId = StringUtil.valueOf(row.get("orderId"));
            String modifyTime = StringUtil.valueOf(row.get("modifyTime"));
            data.setCompanyid(StringUtil.valueOf(row.get("companyId")));
            data.setAccountid(accountId);
            data.setAccountname(StringUtil.valueOf(row.get("accountName")));
            data.setRevenueexpenditure(StringUtil.valueOf(row.get("revenueExpenditure")));
            data.setType(StringUtil.valueOf(row.get("type")));
            data.setOrderid(orderId);
            data.setVariable(StringUtil.valueOf(row.get("variable")));
            data.setBalance(StringUtil.valueOf(row.get("balance")));
            data.setCurrency(StringUtil.valueOf(row.get("currency")));
            data.setCreaterid(StringUtil.valueOf(row.get("createrId")));
            data.setCreatername(StringUtil.valueOf(row.get("createrName")));
            data.setConfirmorid(StringUtil.valueOf(row.get("confirmorId")));
            data.setConfirmorname(StringUtil.valueOf(row.get("confirmorName")));
            data.setModifytime(modifyTime);
            data.setComment(StringUtil.valueOf(row.get("comment")));
            data.setCreatedatatime(StringUtil.valueOf(row.get("createDataTime")));
            AccountData accountIdOneInfo = accountDataMapper.getOneInfoByAccountIdAndOrderIdAndModifyTime(
                    accountId, orderId, modifyTime);
            if (null != accountIdOneInfo && null != accountIdOneInfo.getId()) {
                data.setId(accountIdOneInfo.getId());
                baseUpdate(data);
            } else {
                data.setId(null);
                insertList.add(data);
            }
        }
        baseInsertList(insertList);
    }

    /**
     * 补货计划
     * 5分钟
     */
//    @Scheduled(cron = "20 0/5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void mskuShipData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date endTime = calendar.getTime();
        String end = simpleDateFormat.format(endTime);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 5);
        calendar.set(Calendar.SECOND, 0);
        Date startTime = calendar.getTime();
        String start = simpleDateFormat.format(startTime);
        baseHandle("补货计划", start, end);
    }

    /**
     * 处理补货计划
     *
     * @param lists
     * @throws Exception
     */
    private void handleMskuShipData(List<Map> lists) throws Exception {
        List<Object> insertList = new ArrayList<>(lists.size());
        String futureDateTime = DateUtil.getFutureDateTime(0);
        for (Map row : lists) {
            MskuShipData data = new MskuShipData();
            data.setDate(futureDateTime);
            data.setCompanyid(StringUtil.valueOf(row.get("companyId")));
            data.setFbastockid(StringUtil.valueOf(row.get("fbaStockId")));
            data.setFbastocksku(StringUtil.valueOf(row.get("fbaStockSku")));
            data.setApplyquantity(StringUtil.valueOf(row.get("applyQuantity")));
            data.setMsku(StringUtil.valueOf(row.get("msku")));
            data.setShopid(StringUtil.valueOf(row.get("shopid")));
            data.setShopname(StringUtil.valueOf(row.get("shopName")));
            data.setAmazonsite(StringUtil.valueOf(row.get("amazonsite")));
            data.setReplenishnum(StringUtil.valueOf(row.get("replenishNum")));
            data.setTimecreate(StringUtil.valueOf(row.get("timeCreate")));
            data.setTimeupdate(StringUtil.valueOf(row.get("timeUpdate")));
            data.setStatus(StringUtil.valueOf(row.get("status")));
            insertList.add(data);
        }
        baseInsertList(insertList);
    }

    /**
     * 订单列表
     * 每天24:00需更新一次，并添加更新日期保存
     */
//    @Scheduled(cron = "0 8 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void orderData() {
        String startDate = DateUtil.getFetureDate(-1);
        String start = startDate + " 00:00:00";
        String endDate = DateUtil.getFetureDate(0);
        String end = endDate + " 00:00:00";
        baseHandle("订单列表", start, end);
    }

    /**
     * 处理订单列表
     *
     * @param lists
     * @throws Exception
     */
    private void handleOrderData(List<Map> lists) throws Exception {
        List<Long> orderIds = new ArrayList<>();
        List<Object> itemsList = new ArrayList<>();
        OrderData data = new OrderData();
        String fetureDate = DateUtil.getFetureDate(0);
        for (Map row : lists) {
            data.setDate(fetureDate);
            data.setCompanyid(StringUtil.valueOf(row.get("companyId")));
            String platformOrderId = StringUtil.valueOf(row.get("platformOrderId"));
            data.setPlatformorderid(platformOrderId);
            data.setSalesrecordnumber(StringUtil.valueOf(row.get("salesRecordNumber")));
            data.setPaidtime(StringUtil.valueOf(row.get("paidTime")));
            data.setUpdatetime(StringUtil.valueOf(row.get("updateTime")));
            data.setShopid(StringUtil.valueOf(row.get("shopId")));
            data.setShopname(StringUtil.valueOf(row.get("shopName")));
            data.setPlatformid(StringUtil.valueOf(row.get("platformId")));
            data.setPlatformname(StringUtil.valueOf(row.get("platformName")));
            data.setOrderstatus(StringUtil.valueOf(row.get("orderStatus")));
            data.setOrderstatuscn(StringUtil.valueOf(row.get("orderStatusCn")));
            data.setItemtotalorigin(StringUtil.valueOf(row.get("itemTotalOrigin")));
            data.setOrdertotalorigin(StringUtil.valueOf(row.get("orderTotalOrigin")));
            data.setCurrencyid(StringUtil.valueOf(row.get("currencyId")));
            data.setOrderweight(StringUtil.valueOf(row.get("orderWeight")));
            data.setIsreturned(StringUtil.valueOf(row.get("isReturned")));
            data.setExpresstime(StringUtil.valueOf(row.get("expressTime")));
            data.setSkunum(StringUtil.valueOf(row.get("skuNum")));
            OrderData oneInfoByPlatformOrderId = orderDataMapper.getOneInfoByPlatformOrderId(platformOrderId);
            Long orderId = null;
            if (null != oneInfoByPlatformOrderId && null != oneInfoByPlatformOrderId.getId()) {
                Long id = oneInfoByPlatformOrderId.getId();
                data.setId(id);
                baseUpdate(data);
                orderIds.add(id);
            } else {
                data.setId(null);
                baseInsert(data, true);
            }
            orderId = data.getId();
            if (null == row.get("item")) {
                continue;
            }
            List<Map> items = JSONArray.parseArray(StringUtil.valueOf(row.get("item")), Map.class);
            for (Map item : items) {
                OrderDataItem dataItem = new OrderDataItem();
                dataItem.setOrderId(orderId);
                dataItem.setStockwarehouseid(StringUtil.valueOf(item.get("stockWarehouseId")));
                dataItem.setStockwarehousename(StringUtil.valueOf(item.get("stockWarehouseName")));
                dataItem.setRefuntmoney(StringUtil.valueOf(item.get("refuntMoney")));
                dataItem.setItemweight(StringUtil.valueOf(item.get("itemWeight")));
                dataItem.setStocksku(StringUtil.valueOf(item.get("stockSku")));
                dataItem.setItemquantity(StringUtil.valueOf(item.get("itemQuantity")));
                dataItem.setPlatformsku(StringUtil.valueOf(item.get("platformSku")));
                dataItem.setPlatformquantity(StringUtil.valueOf(item.get("platformQuantity")));
                dataItem.setAsin(StringUtil.valueOf(item.get("asin")));
                dataItem.setStatus(StringUtil.valueOf(item.get("status")));
                itemsList.add(dataItem);
            }
        }
        if (0 < orderIds.size()) {
            orderDataMapper.deleteOrderDataItemByOrderIds(orderIds);
        }
        baseInsertList(itemsList);
    }
}
