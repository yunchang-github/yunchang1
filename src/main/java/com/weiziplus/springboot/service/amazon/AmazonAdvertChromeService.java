package com.weiziplus.springboot.service.amazon;

import com.alibaba.fastjson.JSON;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.DetailPageSalesAndTrafficByChildItemsMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.DetailPageSalesAndTrafficByParentItemsMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.DetailPageSalesAndTrafficMapper;
import com.weiziplus.springboot.mapper.seckill.SeckillItemMapper;
import com.weiziplus.springboot.mapper.seckill.SeckillMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.models.DO.DatailPageSalesAndTrafficNullValueDateDO;
import com.weiziplus.springboot.models.DO.DetailPagesDO;
import com.weiziplus.springboot.service.shop.AreaService;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.amazon.SalesTrafficMatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/8/15 14:42
 */
/*
 * 稍作修改以适应更新后的数据库表
 * --苏建东
 * */
@Slf4j
@Service
public class AmazonAdvertChromeService extends BaseService {

    @Autowired
    DetailPageSalesAndTrafficMapper detailPageSalesAndTrafficMapper;

    @Autowired
    DetailPageSalesAndTrafficByParentItemsMapper detailPageSalesAndTrafficByParentItemsMapper;

    @Autowired
    DetailPageSalesAndTrafficByChildItemsMapper detailPageSalesAndTrafficByChildItemsMapper;

    @Autowired
    SeckillMapper seckillMapper;

    @Autowired
    SeckillItemMapper seckillItemMapper;

    @Autowired
    AreaService areaService;

    @Autowired
    AreaMapper areaMapper;

    @Autowired
    ShopMapper shopMapper;


    /**
     * 总体报表数据录入
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil detailPageSalesAndTraffic(DetailPagesDO detailPagesDO) {
        String sellerId = CryptoUtil.encode(detailPagesDO.getSellerId());
        if (StringUtil.isBlank(detailPagesDO.getAreaUrl())){
            return ResultUtil.error("国家代码为空");
        }
        String countryCode = areaMapper.getMwsCountryCodeByAreaUrl(detailPagesDO.getAreaUrl());
        Shop shop = shopMapper.getOneInfoBySellerId(sellerId);
        if (shop == null || StringUtil.isBlank(sellerId)){
            return ResultUtil.error("网店为空");
        }
        String shopName = shop.getShopName();
        String date = detailPagesDO.getDate();
        List<List<String>> data = detailPagesDO.getData();
        List<String> column = detailPagesDO.getColumn();
        if (StringUtil.isBlank(countryCode)) {
            return ResultUtil.error("国家代码为空");
        }
        if (data == null) {
            return ResultUtil.error("数据为空");
        }
        if (StringUtil.isBlank(date)) {
            return ResultUtil.error("日期为空");
        }
        //去除字符串中的HTML标签和转义字符的转换
        log.info("----------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "的前端数据准备入库--------------");
        log.info("店铺：" + shopName + "，区域：" + countryCode + "的报表字段为：" + detailPagesDO.getColumn().toString());
        for (int i = 0; i < data.size(); i++) {
            List<String> oneData = data.get(i);
            for (int j = 0; j < oneData.size(); j++) {
                String str = oneData.get(j).replaceAll("</?[^>]+>", "");
                str = str.replaceAll("&quot;", "\"");
                str = str.replace("'", "\'");
                oneData.set(j, str);
            }
        }
        if (0 == data.size()) {
            DatailPageSalesAndTrafficNullValueDateDO dateDO = new DatailPageSalesAndTrafficNullValueDateDO();
            dateDO.setShopName(shopName);
            dateDO.setSellerId(sellerId);
            dateDO.setArea(countryCode);
            dateDO.setTableName("detail_page_sales_and_traffic");
            dateDO.setDate(date);
            dateDO.setCreateDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if (null != detailPageSalesAndTrafficMapper.selectDateFromNullValueDate(dateDO)){
                return ResultUtil.success("当前日期的数据为空，且空值日期表中已经记录");
            }
            detailPageSalesAndTrafficMapper.insertNullValueDate(dateDO);
            return ResultUtil.success("当前日期的数据为空，导入数据库中的空值日期表");
        }
        //判断当前数据是否是已经存在的数据
        Integer dateSum = detailPageSalesAndTrafficMapper.getDateSum(date, sellerId, countryCode);
        if (data.size() <= dateSum) {
            return ResultUtil.error("当前日期的数据已存在");
        } else {//如果缺数据，则把当前日期的数据删除重新导入
            detailPageSalesAndTrafficMapper.deleteDateSum(sellerId, countryCode, date);
        }
        //准备做存储操作
        int count = 0;
        List<DetailPageSalesAndTraffic> detailPageSalesAndTrafficList = new ArrayList<DetailPageSalesAndTraffic>();
        for (List<String> oneData : data) {
            count++;
            try {
                DetailPageSalesAndTraffic detailPageSalesAndTraffic = SalesTrafficMatchUtil.columnMatch(column, oneData);
                detailPageSalesAndTraffic.setShopName(shopName);
                detailPageSalesAndTraffic.setCountryCode(countryCode);
                detailPageSalesAndTraffic.setSellerId(sellerId);
                detailPageSalesAndTraffic.setDate(date);
                detailPageSalesAndTraffic.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                detailPageSalesAndTrafficList.add(detailPageSalesAndTraffic);
            } catch (Exception e) {
                log.warn("店铺：" + shopName + "，区域：" + countryCode + "第" + count + "条数据失败，失败数据为：" + oneData.toString());
                log.error("失败信息为：" + e);
            }
        }
        int rows = detailPageSalesAndTrafficMapper.insertListSelective(detailPageSalesAndTrafficList);
        log.info("------------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "插入" + rows + "条数据成功-----------------");
        if (rows != data.size()) {
            return ResultUtil.error("插入数据出现错误");
        }
        return ResultUtil.success("导入成功");
    }

    /**
     * 父体报表数据录入
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil detailSalesTrafficByParentItems(DetailPagesDO detailPagesDO) {
        String sellerId = CryptoUtil.encode(detailPagesDO.getSellerId());
        if (StringUtil.isBlank(detailPagesDO.getAreaUrl())){
            return ResultUtil.error("国家代码为空");
        }
        String countryCode = areaMapper.getMwsCountryCodeByAreaUrl(detailPagesDO.getAreaUrl());
        Shop shop = shopMapper.getOneInfoBySellerId(sellerId);
        if (shop == null || StringUtil.isBlank(sellerId)){
            return ResultUtil.error("网店为空");
        }
        String shopName = shop.getShopName();
        String date = detailPagesDO.getDate();
        List<List<String>> data = detailPagesDO.getData();
        List<String> column = detailPagesDO.getColumn();
        if (StringUtil.isBlank(countryCode)) {
            return ResultUtil.error("国家代码为空");
        }
        if (data == null) {
            return ResultUtil.error("数据为空");
        }
        if (StringUtil.isBlank(date)) {
            return ResultUtil.error("日期为空");
        }
        //去除字符串中的HTML标签和转义字符的转换
        log.info("----------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "的前端数据准备入库--------------");
        log.info("店铺：" + shopName + "，区域：" + countryCode + "的报表字段为：" + detailPagesDO.getColumn().toString());
        for (int i = 0; i < data.size(); i++) {
            List<String> oneData = data.get(i);
            for (int j = 0; j < oneData.size(); j++) {
                String str = oneData.get(j).replaceAll("</?[^>]+>", "");
                str = str.replaceAll("&quot;", "\"");
                oneData.set(j, str);
            }
        }
        if (0 == data.size()) {
            DatailPageSalesAndTrafficNullValueDateDO dateDO = new DatailPageSalesAndTrafficNullValueDateDO();
            dateDO.setShopName(shopName);
            dateDO.setSellerId(sellerId);
            dateDO.setArea(countryCode);
            dateDO.setTableName("detail_page_sales_and_traffic_by_parent_items");
            dateDO.setDate(date);
            dateDO.setCreateDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if (null != detailPageSalesAndTrafficMapper.selectDateFromNullValueDate(dateDO)){
                return ResultUtil.success("当前日期的数据为空，且空值日期表中已经记录");
            }
            detailPageSalesAndTrafficMapper.insertNullValueDate(dateDO);
            return ResultUtil.success("当前日期的数据为空，导入数据库中的空值日期表");
        }
        //判断当前数据是否是已经存在的数据
        Integer dateSum = detailPageSalesAndTrafficByParentItemsMapper.getDateSum(date, sellerId, countryCode);
        if (data.size() <= dateSum) {
            return ResultUtil.error("当前日期的数据已存在");
        } else {//如果缺数据，则把当前日期的数据删除重新导入
            detailPageSalesAndTrafficByParentItemsMapper.deleteDateSum(sellerId, countryCode, date);
        }
        //准备做存储操作
        List<DetailPageSalesAndTrafficByParentItems> detailPageSalesAndTrafficByParentItemsList = new ArrayList<DetailPageSalesAndTrafficByParentItems>();
        int count = 0;
        for (List<String> oneData : data) {
            count++;
            try {
                DetailPageSalesAndTrafficByParentItems detailPageSalesAndTrafficByParentItems = SalesTrafficMatchUtil.columnMatchByParentItems(column, oneData);
                detailPageSalesAndTrafficByParentItems.setShopName(shopName);
                detailPageSalesAndTrafficByParentItems.setCountryCode(countryCode);
                detailPageSalesAndTrafficByParentItems.setSellerId(sellerId);
                detailPageSalesAndTrafficByParentItems.setDate(date);
                detailPageSalesAndTrafficByParentItems.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                detailPageSalesAndTrafficByParentItemsList.add(detailPageSalesAndTrafficByParentItems);
            } catch (Exception e) {
                log.warn("店铺：" + shopName + "，区域：" + countryCode + "第" + count + "条数据失败，失败数据为：" + oneData.toString());
                log.error("失败信息为：" + e);
            }
        }
        int rows = detailPageSalesAndTrafficByParentItemsMapper.insertListSelective(detailPageSalesAndTrafficByParentItemsList);
        log.info("------------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "插入" + rows + "条数据成功-----------------");
        if (rows != data.size()) {
            return ResultUtil.error("插入数据出现错误");
        }
        return ResultUtil.success("导入成功");
    }

    /**
     * 子体报表数据录入
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil detailSalesTrafficByChildItems(DetailPagesDO detailPagesDO) {
        String sellerId = CryptoUtil.encode(detailPagesDO.getSellerId());
        if (StringUtil.isBlank(detailPagesDO.getAreaUrl())){
            return ResultUtil.error("国家代码为空");
        }
        String countryCode = areaMapper.getMwsCountryCodeByAreaUrl(detailPagesDO.getAreaUrl());
        Shop shop = shopMapper.getOneInfoBySellerId(sellerId);
        if (shop == null || StringUtil.isBlank(sellerId)){
            return ResultUtil.error("网店为空");
        }
        String shopName = shop.getShopName();
        String date = detailPagesDO.getDate();
        List<List<String>> data = detailPagesDO.getData();
        List<String> column = detailPagesDO.getColumn();
        if (StringUtil.isBlank(countryCode)) {
            return ResultUtil.error("国家代码为空");
        }
        if (data == null) {
            return ResultUtil.error("数据为空");
        }
        if (StringUtil.isBlank(date)) {
            return ResultUtil.error("日期为空");
        }
        //去除字符串中的HTML标签和转义字符的转换
        log.info("----------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "的前端数据准备入库--------------");
        log.info("店铺：" + shopName + "，区域：" + countryCode + "的报表字段为：" + detailPagesDO.getColumn().toString());
        for (int i = 0; i < data.size(); i++) {
            List<String> oneData = data.get(i);
            for (int j = 0; j < oneData.size(); j++) {
                String str = oneData.get(j).replaceAll("</?[^>]+>", "");
                str = str.replaceAll("&quot;", "\"");
                oneData.set(j, str);
            }
        }
        if (0 == data.size()) {
            DatailPageSalesAndTrafficNullValueDateDO dateDO = new DatailPageSalesAndTrafficNullValueDateDO();
            dateDO.setShopName(shopName);
            dateDO.setSellerId(sellerId);
            dateDO.setArea(countryCode);
            dateDO.setTableName("detail_page_sales_and_traffic_by_child_items");
            dateDO.setDate(date);
            dateDO.setCreateDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if (null != detailPageSalesAndTrafficMapper.selectDateFromNullValueDate(dateDO)){
                log.info("----------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "的前端数据为空，且空值表有数据--------------");
                return ResultUtil.success("当前日期的数据为空，且空值日期表中已经记录");
            }
            detailPageSalesAndTrafficMapper.insertNullValueDate(dateDO);
            log.info("----------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "的前端数据为空，空值表无数据--------------");
            return ResultUtil.success("当前日期的数据为空，导入数据库中的空值日期表");
        }
        //判断当前数据是否是已经存在的数据
        Integer dateSum = detailPageSalesAndTrafficByChildItemsMapper.getDateSum(date, sellerId, countryCode);
        if (data.size() <= dateSum) {
            return ResultUtil.error("当前日期的数据已存在");
        } else {//如果缺数据，则把当前日期的数据删除重新导入
            detailPageSalesAndTrafficByChildItemsMapper.deleteDateSum(sellerId, countryCode, date);
        }
        //准备做存储操作
        List<DetailPageSalesAndTrafficByChildItems> detailPageSalesAndTrafficByChildItemsList = new ArrayList<DetailPageSalesAndTrafficByChildItems>();
        int count = 0;
        for (List<String> oneData : data) {
            count++;
            try {
                DetailPageSalesAndTrafficByChildItems detailPageSalesAndTrafficByChildItems = SalesTrafficMatchUtil.columnMatchByChildItems(column, oneData);
                detailPageSalesAndTrafficByChildItems.setShopName(shopName);
                detailPageSalesAndTrafficByChildItems.setCountryCode(countryCode);
                detailPageSalesAndTrafficByChildItems.setSellerId(sellerId);
                detailPageSalesAndTrafficByChildItems.setDate(date);
                detailPageSalesAndTrafficByChildItems.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                detailPageSalesAndTrafficByChildItemsList.add(detailPageSalesAndTrafficByChildItems);
            } catch (Exception e) {
                log.warn("店铺：" + shopName + "，区域：" + countryCode + "第" + count + "条数据失败，失败数据为：" + oneData.toString());
                log.error("失败信息为：" + e);
            }
        }
        int rows = detailPageSalesAndTrafficByChildItemsMapper.insertListSelective(detailPageSalesAndTrafficByChildItemsList);
        log.info("------------------店铺：" + shopName + "，区域：" + countryCode + "的" + detailPagesDO.getDate() + "插入" + rows + "条数据成功-----------------");
        if (rows != data.size()) {
            return ResultUtil.error("插入数据出现错误");
        }
        return ResultUtil.success("导入成功");
    }

    /**
     * 获取秒杀总数量
     *
     * @param shop
     * @param area
     * @return
     */
/*    public ResultUtil getSeckillNum(String shop, String area) {
        if (StringUtil.isBlank(shop)) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(area)) {
            return ResultUtil.error("区域不能为空");
        }
        Area oneInfoByName1 = areaMapper.getOneInfoByMarketplaceId(area);

        if (ObjectUtils.isEmpty(oneInfoByName1)) {
            return ResultUtil.error("国家代码不正确");
        }

        area = oneInfoByName1.getMwsCountryCode();

        if (!areaService.getMwsCountryCodeList().contains(area)) {
            return ResultUtil.error("国家代码不正确");
        }
        return ResultUtil.success(seckillMapper.getSeckillNumByShopAndArea(shop, area));
    }*/

    /**
     * 活动秒杀情况
     *
     * @param shop
     * @param area
     * @param data
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil seckill(String shop, String area, String data) {//这里area 前端传过来的是MarketplaceId
        if (StringUtil.isBlank(shop)) {
            return ResultUtil.error("网店为空");
        }
        if (StringUtil.isBlank(area)) {
            return ResultUtil.error("国家代码为空");
        }

        Area oneInfoByName1 = areaMapper.getOneInfoByMarketplaceId(area);

        if (ObjectUtils.isEmpty(oneInfoByName1)) {
            return ResultUtil.error("国家代码不正确");
        }

        area = oneInfoByName1.getMwsCountryCode();

        if (StringUtil.isBlank(data)) {
            return ResultUtil.error("数据为空");
        }

        Map<String, Object> row = (Map<String, Object>) JSON.parse(data);
        Object itemsObject = row.get("items");
        if (null == itemsObject) {
            return ResultUtil.success("商品列表为空");
        }
        Seckill seckill = new Seckill();
        String uuid = StringUtil.createUUID();
        seckill.setId(uuid);
        seckill.setShop(shop);
        seckill.setArea(area);
        String campaignId = StringUtil.valueOf(row.get("campaignId"));
        if (StringUtil.isBlank(campaignId)) {
            return ResultUtil.error("campaignId不能为空");
        }
        seckill.setCampaignId(campaignId);
        Integer typeInteger = ToolUtil.valueOfInteger(StringUtil.valueOf(row.get("type")));
        if (null == typeInteger) {
            return ResultUtil.error("类型不能为空");
        }
        //类型，0：已结束，1：即将开始
        if (0 != typeInteger && 1 != typeInteger) {
            return ResultUtil.error("类型不能为空");
        }
        seckill.setType(typeInteger);
        seckill.setDate(DateUtil.getFutureDateTime(0));
        seckill.setContent(StringUtil.valueOf(row.get("content")));
        seckill.setStartTime(StringUtil.valueOf(row.get("startTime")));
        seckill.setEndTime(StringUtil.valueOf(row.get("endTime")));
        seckill.setFee(ToolUtil.valueOfDouble(StringUtil.valueOf(row.get("fee"))));
        List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObject;
        List<Object> itemInsertList = new ArrayList<>(items.size());
        for (Map<String, Object> map : items) {
            SeckillItem item = new SeckillItem();
            item.setSeckillId(uuid);
            item.setAsin(StringUtil.valueOf(map.get("asin")));
            item.setSku(StringUtil.valueOf(map.get("sku")));
            if (0 == typeInteger) {
                item.setPromotionPrice(ToolUtil.valueOfDouble(StringUtil.valueOf(map.get("promotionPrice"))));
                item.setQuantityNum(ToolUtil.valueOfInteger(StringUtil.valueOf(map.get("quantityNum"))));
            } else {
                item.setGoodsPrice(ToolUtil.valueOfDouble(StringUtil.valueOf(map.get("goodsPrice"))));
                item.setPromotionPrice(ToolUtil.valueOfDouble(StringUtil.valueOf(map.get("promotionPrice"))));
                item.setPartakeNum(ToolUtil.valueOfInteger(StringUtil.valueOf(map.get("partakeNum"))));
            }
            itemInsertList.add(item);
        }
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        //  Seckill oneInfoByCampaignId = seckillMapper.getOneInfoByCampaignId(campaignId);
        Seckill oneInfoByCampaignId = null;
        try {
            if (null != oneInfoByCampaignId && null != oneInfoByCampaignId.getId()) {
                seckill.setId(oneInfoByCampaignId.getId());
                baseUpdate(seckill);
                seckillItemMapper.deleteItemBySeckillId(oneInfoByCampaignId.getId());
            } else {
                baseInsert(seckill);
            }
            baseInsertList(itemInsertList);
            return ResultUtil.success();
        } catch (Exception e) {
            log.warn("秒杀数据获取失败，详情:" + e);
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
    }
}
