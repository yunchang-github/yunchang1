package com.weiziplus.springboot.service.advertisingInventoryReport;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weiziplus.springboot.mapper.advertisingInventoryReport.*;
import com.weiziplus.springboot.mapper.salesStatisticsReport.SalesStatisticsReportMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.DO.*;
import com.weiziplus.springboot.models.VO.ChildItemAdInventoryReportVO;
import com.weiziplus.springboot.models.VO.ParentItemAdInventoryReportVO;
import com.weiziplus.springboot.utils.*;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weiziplus.springboot.base.BaseService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChildItemSalesAndTrafficService extends BaseService {

    @Autowired
    AdvertInventoryReportMapper advertInventoryReportMapper;

    @Autowired
    InventoryReportMapper inventoryReportMapper;

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    SalesStatisticsReportMapper salesStatisticsReportMapper;

    /**
     * 获取子分页数据
     * 用到all_order + detail_page_sales_and_traffic_by_child_items + manage_fba_inventory
     *
     * @param maps
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil getPageChildList(Map maps) {
        //设置标志，1表示获取子体报告
        int flag = 1;
        Integer pageNum = MapUtils.getInteger(maps, "pageNum");
        Integer pageSize = MapUtils.getInteger(maps, "pageSize");
        String date = MapUtils.getString(maps, "date");
        String type = MapUtils.getString(maps, "type");
        String shop = MapUtils.getString(maps, "shop");
        String sellerId = shopMapper.getOneInfoByName(MapUtils.getString(maps, "shop")).getSellerId();
        maps.put("sellerId", sellerId);
        //将area转换为对应的站点，用于匹配all_order表的数据
        String area = MapUtils.getString(maps, "area");
        String salesChannel = AreaCastUtil.castToSalesChannel(area);
        int domainId = AreaCastUtil.castToDomainId(area);
        maps.put("salesChannel", salesChannel);
        String firstDate = "";
        String secondDate = "";
        String thirdDate = "";
        String fourthDate = "";
        //默认时间的设置
        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        if (("".equals(date) || date == null) && type.equals("week")) {
            Integer weekOfYear = localDate.minusWeeks(4).get(weekFields.weekOfYear());
            String year = localDate.minusWeeks(4).toString().replace("-", "").substring(0, 4);
            String week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            firstDate = year + week;
            weekOfYear = localDate.minusWeeks(3).get(weekFields.weekOfYear());
            year = localDate.minusWeeks(3).toString().replace("-", "").substring(0, 4);
            week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            secondDate = year + week;
            weekOfYear = localDate.minusWeeks(2).get(weekFields.weekOfYear());
            year = localDate.minusWeeks(2).toString().replace("-", "").substring(0, 4);
            week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            thirdDate = year + week;
            weekOfYear = localDate.minusWeeks(1).get(weekFields.weekOfYear());
            year = localDate.minusWeeks(1).toString().replace("-", "").substring(0, 4);
            week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            fourthDate = year + week;
        } else if (("".equals(date) || date == null) && type.equals("month")) {
            firstDate = localDate.minusMonths(4).toString().replace("-", "").substring(0, 6);
            secondDate = localDate.minusMonths(3).toString().replace("-", "").substring(0, 6);
            thirdDate = localDate.minusMonths(2).toString().replace("-", "").substring(0, 6);
            fourthDate = localDate.minusMonths(1).toString().replace("-", "").substring(0, 6);
        } else {
            String[] dateStr = date.split(",");
            firstDate = dateStr[0];
            secondDate = dateStr[1];
            thirdDate = dateStr[2];
            fourthDate = dateStr[3];
        }
        //新的逻辑
        List<ChildBodyInventoryReportDO> list1 = new ArrayList<>();
        List<ChildBodyInventoryReportDO> list2 = new ArrayList<>();
        List<ChildBodyInventoryReportDO> list3 = new ArrayList<>();
        List<ChildBodyInventoryReportDO> list4 = new ArrayList<>();
        if (type.equals("week")) {
            //获得第一周数据
            //如果Redis里面已经存了这周数据，直接拿
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + firstDate)) {
            if (1 == 2) {   //list1 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + firstDate);
            } else {
                Integer weekOfYear = Integer.parseInt(firstDate.substring(4, 6));
                Integer year = Integer.parseInt(firstDate.substring(0, 4));
                String startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
                String endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list1 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list1.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list1.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(firstDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + firstDate, list1, 60L * 30);
            }

            //获得第二周数据
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + secondDate)) {
            if (1 == 2) {   //list2 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + secondDate);
            } else {
                Integer weekOfYear = Integer.parseInt(secondDate.substring(4, 6));
                Integer year = Integer.parseInt(secondDate.substring(0, 4));
                String startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
                String endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list2 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list2.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list2.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(secondDate);

                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + secondDate, list2, 60L * 30);
            }

            //获得第三周数据
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + thirdDate)) {
            if (1 == 2) {   //list3 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + thirdDate);
            } else {
                Integer weekOfYear = Integer.parseInt(thirdDate.substring(4, 6));
                Integer year = Integer.parseInt(thirdDate.substring(0, 4));
                String startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
                String endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list3 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list3.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list3.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(thirdDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + thirdDate, list3, 60L * 30);
            }
            //获得第四周数据
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + fourthDate)) {
            if (1 == 2) {   //list4 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + fourthDate);
            } else {
                Integer weekOfYear = Integer.parseInt(fourthDate.substring(4, 6));
                Integer year = Integer.parseInt(fourthDate.substring(0, 4));
                String startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
                String endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list4 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list4.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list4.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(fourthDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + fourthDate, list4, 60L * 30);
            }
        }
        if (type.equals("month")) {
            //获取第一个月的数据
            //如果Redis里面已经存了这周数据，直接拿
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + firstDate)) {
            if (1 == 2) {  //list1 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + firstDate);
            } else {
                Integer month = Integer.parseInt(firstDate.substring(4, 6));
                Integer year = Integer.parseInt(firstDate.substring(0, 4));
                String startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list1 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list1.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list1.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(firstDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + firstDate, list1, 60L * 30);
            }
            //获取第二个月的数据
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + secondDate)) {
            if (1 == 2) {  //list2 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + secondDate);
            } else {
                Integer month = Integer.parseInt(secondDate.substring(4, 6));
                Integer year = Integer.parseInt(secondDate.substring(0, 4));
                String startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list2 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list2.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list2.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(secondDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + secondDate, list2, 60L * 30);
            }
            //获取第三个月的数据
            //if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + thirdDate)) {
            if (1 == 2) { //list3 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + thirdDate);
            } else {
                Integer month = Integer.parseInt(thirdDate.substring(4, 6));
                Integer year = Integer.parseInt(thirdDate.substring(0, 4));
                String startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list3 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list3.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list3.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(thirdDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + thirdDate, list3, 60L * 30);
            }
            //获取第四个月的数据
            // if (RedisUtil.hasKye("childAdvertInventoryReport" + sellerId + area + type + fourthDate)) {
            if (1 == 2) {//list4 = (List<ChildBodyInventoryReportDO>) RedisUtil.get("childAdvertInventoryReport" + sellerId + area + type + fourthDate);
            } else {
                Integer month = Integer.parseInt(fourthDate.substring(4, 6));
                Integer year = Integer.parseInt(fourthDate.substring(0, 4));
                String startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
                List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
                List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
                List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
                List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
                List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
                list4 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
                for (int i = 0; i < list4.size(); i++) {
                    ChildBodyInventoryReportDO childBodyInventoryReportDO = list4.get(i);
                    childBodyInventoryReportDO.setSellerId(sellerId);
                    childBodyInventoryReportDO.setArea(area);
                    childBodyInventoryReportDO.setDateType(type);
                    childBodyInventoryReportDO.setDate(fourthDate);
                }
                //RedisUtil.set("childAdvertInventoryReport" + sellerId + area + type + fourthDate, list4, 60L * 30);
            }
        }

        List<ChildItemAdInventoryReportVO> resultList = new ArrayList();
        //把拿到的所有数据存到对应的VO对象中
        for (int i = 0; i < list1.size(); i++) {
            ChildItemAdInventoryReportVO childItemAdInventoryReportVO = new ChildItemAdInventoryReportVO();
            ChildBodyInventoryReportDO childBodyInventoryReportDO1 = list1.get(i);
            ChildBodyInventoryReportDO childBodyInventoryReportDO2 = list2.get(i);
            ChildBodyInventoryReportDO childBodyInventoryReportDO3 = list3.get(i);
            ChildBodyInventoryReportDO childBodyInventoryReportDO4 = list4.get(i);

            childItemAdInventoryReportVO.setChildSKU(childBodyInventoryReportDO1.getCSku());
            childItemAdInventoryReportVO.setChildASIN(childBodyInventoryReportDO1.getCAsin());
            childItemAdInventoryReportVO.setParentSKU(childBodyInventoryReportDO1.getPSku());

            childItemAdInventoryReportVO.setFirstSumQuantity(childBodyInventoryReportDO1.getSumQuantity());
            childItemAdInventoryReportVO.setSecondSumQuantity(childBodyInventoryReportDO2.getSumQuantity());
            childItemAdInventoryReportVO.setThirdSumQuantity(childBodyInventoryReportDO3.getSumQuantity());
            childItemAdInventoryReportVO.setFourthSumQuantity(childBodyInventoryReportDO4.getSumQuantity());

            childItemAdInventoryReportVO.setFirstUnitPrice(childBodyInventoryReportDO1.getUnitPrice());
            childItemAdInventoryReportVO.setSecondUnitPrice(childBodyInventoryReportDO2.getUnitPrice());
            childItemAdInventoryReportVO.setThirdUnitPrice(childBodyInventoryReportDO3.getUnitPrice());
            childItemAdInventoryReportVO.setFourthUnitPrice(childBodyInventoryReportDO4.getUnitPrice());

            childItemAdInventoryReportVO.setAfnFulfillableQuantity(childBodyInventoryReportDO1.getAfnFulfillableQuantity());
            childItemAdInventoryReportVO.setAfnInboundShippedQuantity(childBodyInventoryReportDO1.getAfnInboundShippedQuantity());
            childItemAdInventoryReportVO.setAfnInboundReceivingQuantity(childBodyInventoryReportDO1.getAfnInboundReceivingQuantity());
            childItemAdInventoryReportVO.setReservedFcTransfers(childBodyInventoryReportDO1.getReservedFcTransfers());
            childItemAdInventoryReportVO.setTotalInventory(childBodyInventoryReportDO1.getTotalInventory());
            childItemAdInventoryReportVO.setAfnUnsellableQuantity(childBodyInventoryReportDO1.getAfnUnsellableQuantity());
            childItemAdInventoryReportVO.setSumInvAge90PlusDays(childBodyInventoryReportDO1.getSumInvAge90PlusDays());

            childItemAdInventoryReportVO.setFirstSumImpressions(childBodyInventoryReportDO1.getSumImpressions());
            childItemAdInventoryReportVO.setSecondSumImpressions(childBodyInventoryReportDO2.getSumImpressions());
            childItemAdInventoryReportVO.setThirdSumImpressions(childBodyInventoryReportDO3.getSumImpressions());
            childItemAdInventoryReportVO.setFourthSumImpressions(childBodyInventoryReportDO4.getSumImpressions());

            childItemAdInventoryReportVO.setFirstSumClicks(childBodyInventoryReportDO1.getSumClicks());
            childItemAdInventoryReportVO.setSecondSumClicks(childBodyInventoryReportDO2.getSumClicks());
            childItemAdInventoryReportVO.setThirdSumClicks(childBodyInventoryReportDO3.getSumClicks());
            childItemAdInventoryReportVO.setFourthSumClicks(childBodyInventoryReportDO4.getSumClicks());

            childItemAdInventoryReportVO.setFirstSumCost(childBodyInventoryReportDO1.getSumCost());
            childItemAdInventoryReportVO.setSecondSumCost(childBodyInventoryReportDO2.getSumCost());
            childItemAdInventoryReportVO.setThirdSumCost(childBodyInventoryReportDO3.getSumCost());
            childItemAdInventoryReportVO.setFourthSumCost(childBodyInventoryReportDO4.getSumCost());

            childItemAdInventoryReportVO.setFirstSumAttributedUnitsOrdered(childBodyInventoryReportDO1.getSumAttributedUnitsOrdered());
            childItemAdInventoryReportVO.setSecondSumAttributedUnitsOrdered(childBodyInventoryReportDO2.getSumAttributedUnitsOrdered());
            childItemAdInventoryReportVO.setThirdSumAttributedUnitsOrdered(childBodyInventoryReportDO3.getSumAttributedUnitsOrdered());
            childItemAdInventoryReportVO.setFourthSumAttributedUnitsOrdered(childBodyInventoryReportDO4.getSumAttributedUnitsOrdered());

            childItemAdInventoryReportVO.setFirstSumAttributedSales(childBodyInventoryReportDO1.getSumAttributedSales());
            childItemAdInventoryReportVO.setSecondSumAttributedSales(childBodyInventoryReportDO2.getSumAttributedSales());
            childItemAdInventoryReportVO.setThirdSumAttributedSales(childBodyInventoryReportDO3.getSumAttributedSales());
            childItemAdInventoryReportVO.setFourthSumAttributedSales(childBodyInventoryReportDO4.getSumAttributedSales());

            childItemAdInventoryReportVO.setFirstSumAttributedSalesSameSKU(childBodyInventoryReportDO1.getSumAttributedSalesSameSKU());
            childItemAdInventoryReportVO.setSecondSumAttributedSalesSameSKU(childBodyInventoryReportDO2.getSumAttributedSalesSameSKU());
            childItemAdInventoryReportVO.setThirdSumAttributedSalesSameSKU(childBodyInventoryReportDO3.getSumAttributedSalesSameSKU());
            childItemAdInventoryReportVO.setFourthSumAttributedSalesSameSKU(childBodyInventoryReportDO4.getSumAttributedSalesSameSKU());

            childItemAdInventoryReportVO.setFirstEffectiveConversionRate(childBodyInventoryReportDO1.getEffectiveConversionRate());
            childItemAdInventoryReportVO.setSecondEffectiveConversionRate(childBodyInventoryReportDO2.getEffectiveConversionRate());
            childItemAdInventoryReportVO.setThirdEffectiveConversionRate(childBodyInventoryReportDO3.getEffectiveConversionRate());
            childItemAdInventoryReportVO.setFourthEffectiveConversionRate(childBodyInventoryReportDO4.getEffectiveConversionRate());

            childItemAdInventoryReportVO.setFirstCTR(childBodyInventoryReportDO1.getCTR());
            childItemAdInventoryReportVO.setSecondCTR(childBodyInventoryReportDO2.getCTR());
            childItemAdInventoryReportVO.setThirdCTR(childBodyInventoryReportDO3.getCTR());
            childItemAdInventoryReportVO.setFourthCTR(childBodyInventoryReportDO4.getCTR());

            childItemAdInventoryReportVO.setFirstCR(childBodyInventoryReportDO1.getCR());
            childItemAdInventoryReportVO.setSecondCR(childBodyInventoryReportDO2.getCR());
            childItemAdInventoryReportVO.setThirdCR(childBodyInventoryReportDO3.getCR());
            childItemAdInventoryReportVO.setFourthCR(childBodyInventoryReportDO4.getCR());

            childItemAdInventoryReportVO.setFirstACoS(childBodyInventoryReportDO1.getACoS());
            childItemAdInventoryReportVO.setSecondACoS(childBodyInventoryReportDO2.getACoS());
            childItemAdInventoryReportVO.setThirdACoS(childBodyInventoryReportDO3.getACoS());
            childItemAdInventoryReportVO.setFourthACoS(childBodyInventoryReportDO4.getACoS());

            childItemAdInventoryReportVO.setFirstCPC(childBodyInventoryReportDO1.getCPC());
            childItemAdInventoryReportVO.setSecondCPC(childBodyInventoryReportDO2.getCPC());
            childItemAdInventoryReportVO.setThirdCPC(childBodyInventoryReportDO3.getCPC());
            childItemAdInventoryReportVO.setFourthCPC(childBodyInventoryReportDO4.getCPC());

            childItemAdInventoryReportVO.setFirstSumBuyerVisits(childBodyInventoryReportDO1.getSumBuyerVisits());
            childItemAdInventoryReportVO.setSecondSumBuyerVisits(childBodyInventoryReportDO2.getSumBuyerVisits());
            childItemAdInventoryReportVO.setThirdSumBuyerVisits(childBodyInventoryReportDO3.getSumBuyerVisits());
            childItemAdInventoryReportVO.setFourthSumBuyerVisits(childBodyInventoryReportDO4.getSumBuyerVisits());

            childItemAdInventoryReportVO.setFirstSalesViewsConversionRate(childBodyInventoryReportDO1.getSalesViewsConversionRate());
            childItemAdInventoryReportVO.setSecondSalesViewsConversionRate(childBodyInventoryReportDO2.getSalesViewsConversionRate());
            childItemAdInventoryReportVO.setThirdSalesViewsConversionRate(childBodyInventoryReportDO3.getSalesViewsConversionRate());
            childItemAdInventoryReportVO.setFourthSalesViewsConversionRate(childBodyInventoryReportDO4.getSalesViewsConversionRate());

            childItemAdInventoryReportVO.setExpectedDailySales(childBodyInventoryReportDO1.getExpectedDailySales());
            childItemAdInventoryReportVO.setExpected60DaysSales(childBodyInventoryReportDO1.getExpected60DaysSales());
            childItemAdInventoryReportVO.setExpectedTotalInventoryAfter60Days(childBodyInventoryReportDO1.getExpectedTotalInventoryAfter60Days());
            childItemAdInventoryReportVO.setExpectedAvailability(childBodyInventoryReportDO1.getExpectedAvailability());
            childItemAdInventoryReportVO.setExpectedSaleDate(childBodyInventoryReportDO1.getExpectedSaleDate());
            childItemAdInventoryReportVO.setShortageInventoryEstimates(childBodyInventoryReportDO1.getShortageInventoryEstimates());

            childItemAdInventoryReportVO.setFirstClicksVisitsRate(childBodyInventoryReportDO1.getClicksVisitsRate());
            childItemAdInventoryReportVO.setSecondClicksVisitsRate(childBodyInventoryReportDO2.getClicksVisitsRate());
            childItemAdInventoryReportVO.setThirdClicksVisitsRate(childBodyInventoryReportDO3.getClicksVisitsRate());
            childItemAdInventoryReportVO.setFourthClicksVisitsRate(childBodyInventoryReportDO4.getClicksVisitsRate());

            childItemAdInventoryReportVO.setFirstAdsRevRate(childBodyInventoryReportDO1.getAdsRevRate());
            childItemAdInventoryReportVO.setSecondAdsRevRate(childBodyInventoryReportDO2.getAdsRevRate());
            childItemAdInventoryReportVO.setThirdAdsRevRate(childBodyInventoryReportDO3.getAdsRevRate());
            childItemAdInventoryReportVO.setFourthAdsRevRate(childBodyInventoryReportDO4.getAdsRevRate());

            childItemAdInventoryReportVO.setFirstAdsCostRate(childBodyInventoryReportDO1.getAdsCostRate());
            childItemAdInventoryReportVO.setSecondAdsCostRate(childBodyInventoryReportDO2.getAdsCostRate());
            childItemAdInventoryReportVO.setThirdAdsCostRate(childBodyInventoryReportDO3.getAdsCostRate());
            childItemAdInventoryReportVO.setFourthAdsCostRate(childBodyInventoryReportDO4.getAdsCostRate());

            childItemAdInventoryReportVO.setFirstLightningDealsWeek(childBodyInventoryReportDO1.getLightningDealsWeek());
            childItemAdInventoryReportVO.setSecondLightningDealsWeek(childBodyInventoryReportDO2.getLightningDealsWeek());
            childItemAdInventoryReportVO.setThirdLightningDealsWeek(childBodyInventoryReportDO3.getLightningDealsWeek());
            childItemAdInventoryReportVO.setFourthLightningDealsWeek(childBodyInventoryReportDO4.getLightningDealsWeek());
            //将VO对象加到resultList中
            resultList.add(childItemAdInventoryReportVO);
        }
        String cSku = MapUtils.getString(maps, "sku");
        String cAsin = MapUtils.getString(maps, "asin");
        String pSku = MapUtils.getString(maps, "parentSku");
        List<ChildItemAdInventoryReportVO> results = new ArrayList();
        for (int i = 0; i < resultList.size(); i++) {
            Pattern pattern1 = Pattern.compile(cSku);
            Pattern pattern2 = Pattern.compile(cAsin);
            Pattern pattern3 = Pattern.compile(pSku);
            Matcher matcher1 = pattern1.matcher(((ChildItemAdInventoryReportVO) resultList.get(i)).getChildSKU());
            Matcher matcher2 = pattern2.matcher(((ChildItemAdInventoryReportVO) resultList.get(i)).getChildASIN());
            Matcher matcher3 = pattern3.matcher(((ChildItemAdInventoryReportVO) resultList.get(i)).getParentSKU());
            if (matcher1.find() && matcher2.find() && matcher3.find()) {
                results.add(resultList.get(i));
            }
        }
        //获得前端传递的排序的参数值进行排序
        String field = String.valueOf(maps.get("field"));
        String sort = String.valueOf(maps.get("sort"));
        SortListUtils.sort(results, field, sort);
        PageUtil pageList = PageUtil.Pagination(pageNum, pageSize, results);
        pageList.setTotal((long) results.size());
        return ResultUtil.success(pageList);
    }

    /**
     * 获取父分页数据
     *
     * @param maps
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public ResultUtil getPageParentList(Map maps) {
        //设置标志，2表示获取父体报告
        int flag = 2;
        Integer pageNum = MapUtils.getInteger(maps, "pageNum");
        Integer pageSize = MapUtils.getInteger(maps, "pageSize");
        String date = MapUtils.getString(maps, "date");
        String type = MapUtils.getString(maps, "type");
        String shop = MapUtils.getString(maps, "shop");
        String sellerId = shopMapper.getOneInfoByName(MapUtils.getString(maps, "shop")).getSellerId();
        maps.put("sellerId", sellerId);
        //将area转换为对应的站点，用于匹配all_order表的数据
        String area = MapUtils.getString(maps, "area");
        String salesChannel = AreaCastUtil.castToSalesChannel(area);
        int domainId = AreaCastUtil.castToDomainId(area);
        maps.put("salesChannel", salesChannel);
        String firstDate = "";
        String secondDate = "";
        String thirdDate = "";
        String fourthDate = "";
        //默认时间的设置
        LocalDate localDate = LocalDate.now();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        if (("".equals(date) || date == null) && type.equals("week")) {
            Integer weekOfYear = localDate.minusWeeks(4).get(weekFields.weekOfYear());
            String year = localDate.minusWeeks(4).toString().replace("-", "").substring(0, 4);
            String week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            firstDate = year + week;
            weekOfYear = localDate.minusWeeks(3).get(weekFields.weekOfYear());
            year = localDate.minusWeeks(3).toString().replace("-", "").substring(0, 4);
            week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            secondDate = year + week;
            weekOfYear = localDate.minusWeeks(2).get(weekFields.weekOfYear());
            year = localDate.minusWeeks(2).toString().replace("-", "").substring(0, 4);
            week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            thirdDate = year + week;
            weekOfYear = localDate.minusWeeks(1).get(weekFields.weekOfYear());
            year = localDate.minusWeeks(1).toString().replace("-", "").substring(0, 4);
            week = String.valueOf(weekOfYear);
            if (weekOfYear < 10) {
                week = "0" + weekOfYear;
            }
            fourthDate = year + week;
        } else if (("".equals(date) || date == null) && type.equals("month")) {
            firstDate = localDate.minusMonths(4).toString().replace("-", "").substring(0, 6);
            secondDate = localDate.minusMonths(3).toString().replace("-", "").substring(0, 6);
            thirdDate = localDate.minusMonths(2).toString().replace("-", "").substring(0, 6);
            fourthDate = localDate.minusMonths(1).toString().replace("-", "").substring(0, 6);
        } else {
            String[] dateStr = date.split(",");
            firstDate = dateStr[0];
            secondDate = dateStr[1];
            thirdDate = dateStr[2];
            fourthDate = dateStr[3];
        }
        //新的逻辑，得到传递的值的年，月或周
        List<ChildBodyInventoryReportDO> list1 = new ArrayList<>();
        List<ChildBodyInventoryReportDO> list2 = new ArrayList<>();
        List<ChildBodyInventoryReportDO> list3 = new ArrayList<>();
        List<ChildBodyInventoryReportDO> list4 = new ArrayList<>();
        if (type.equals("week")) {
            //获得第一周数据
            Integer weekOfYear = Integer.parseInt(firstDate.substring(4, 6));
            Integer year = Integer.parseInt(firstDate.substring(0, 4));
            String startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
            String endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
            List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list1 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list1.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list1.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(firstDate);
            }
            //获得第二周数据
            weekOfYear = Integer.parseInt(secondDate.substring(4, 6));
            year = Integer.parseInt(secondDate.substring(0, 4));
            startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
            endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
            quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list2 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list2.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list2.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(secondDate);

            }
            //获得第三周数据
            weekOfYear = Integer.parseInt(thirdDate.substring(4, 6));
            year = Integer.parseInt(thirdDate.substring(0, 4));
            startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
            endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
            quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list3 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list3.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list3.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(thirdDate);
            }
            //获得第四周数据
            weekOfYear = Integer.parseInt(fourthDate.substring(4, 6));
            year = Integer.parseInt(fourthDate.substring(0, 4));
            startDate = WeekToDateUtil.getWeekStartDate(year, weekOfYear);
            endDate = WeekToDateUtil.getWeekEndDate(year, weekOfYear);
            quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list4 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list4.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list4.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(fourthDate);
            }
        }
        if (type.equals("month")) {
            //获取第一个月的数据
            Integer month = Integer.parseInt(firstDate.substring(4, 6));
            Integer year = Integer.parseInt(firstDate.substring(0, 4));
            String startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
            List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            List<InventoryDO> inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            List<AdvProductDataDO> advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            List<BuyerVisitsDO> buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            List<LightningDealsDataDO> lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list1 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list1.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list1.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(firstDate);
            }
            //获取第二个月的数据
            month = Integer.parseInt(secondDate.substring(4, 6));
            year = Integer.parseInt(secondDate.substring(0, 4));
            startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
            quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list2 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list2.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list2.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(secondDate);
            }
            //获取第三个月的数据
            month = Integer.parseInt(thirdDate.substring(4, 6));
            year = Integer.parseInt(thirdDate.substring(0, 4));
            startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
            quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list3 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list3.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list3.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(thirdDate);
            }
            //获取第四个月的数据
            month = Integer.parseInt(fourthDate.substring(4, 6));
            year = Integer.parseInt(fourthDate.substring(0, 4));
            startDate = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDate = LocalDate.of(year, month, Month.of(month).minLength()).toString();
            quantityAndUnitPriceDOList = advertInventoryReportMapper.getQuantityAndUnitPrice(shop, area, salesChannel, startDate, endDate, flag);
            inventoryDOList = advertInventoryReportMapper.getInventory(shop, area, startDate, endDate, flag);
            advProductDataDOList = advertInventoryReportMapper.getAdvProductData(shop, area, startDate, endDate, flag);
            buyerVisitsDOList = advertInventoryReportMapper.getBuyerVisits(shop, area, startDate, endDate, flag);
            lightningDealsDataDOList = advertInventoryReportMapper.getLightningDeals(shop, area, domainId, startDate, endDate, flag);
            list4 = AdvertInventoryCastUtil.castToChildBodyInventoryReportDO(quantityAndUnitPriceDOList, inventoryDOList, advProductDataDOList, buyerVisitsDOList, lightningDealsDataDOList);
            for (int i = 0; i < list4.size(); i++) {
                ChildBodyInventoryReportDO childBodyInventoryReportDO = list4.get(i);
                childBodyInventoryReportDO.setSellerId(sellerId);
                childBodyInventoryReportDO.setArea(area);
                childBodyInventoryReportDO.setDateType(type);
                childBodyInventoryReportDO.setDate(fourthDate);
            }
        }
        //额外计算每日销量,现在不好实现
//        Integer dailyAverageQuantityInWeek = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shop, area, "week");
//        Integer dailyAverageQuantityInTwoWeek = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shop, area, "twoWeek");
//        Integer dailyAverageQuantityInMonth = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shop, area, "month");
//        Double dailyAverageQuantity = dailyAverageQuantityInWeek / 7 * 0.6 + dailyAverageQuantityInTwoWeek / 14 * 0.3 + dailyAverageQuantityInMonth / 28 * 0.1;
        //获得该店铺在这几周是否有秒杀活动


        List<ParentItemAdInventoryReportVO> resultList = new ArrayList();
        //把拿到的所有数据存到对应的VO对象中
        for (int i = 0; i < list1.size(); i++) {
            ParentItemAdInventoryReportVO parentItemAdInventoryReportVO = new ParentItemAdInventoryReportVO();
            ChildBodyInventoryReportDO parentBodyInventoryReportDO1 = list1.get(i);
            ChildBodyInventoryReportDO parentBodyInventoryReportDO2 = list2.get(i);
            ChildBodyInventoryReportDO parentBodyInventoryReportDO3 = list3.get(i);
            ChildBodyInventoryReportDO parentBodyInventoryReportDO4 = list4.get(i);

            parentItemAdInventoryReportVO.setParentSKU(parentBodyInventoryReportDO1.getPSku());

            parentItemAdInventoryReportVO.setFirstSumQuantity(parentBodyInventoryReportDO1.getSumQuantity());
            parentItemAdInventoryReportVO.setSecondSumQuantity(parentBodyInventoryReportDO2.getSumQuantity());
            parentItemAdInventoryReportVO.setThirdSumQuantity(parentBodyInventoryReportDO3.getSumQuantity());
            parentItemAdInventoryReportVO.setFourthSumQuantity(parentBodyInventoryReportDO4.getSumQuantity());

            parentItemAdInventoryReportVO.setFirstUnitPrice(parentBodyInventoryReportDO1.getUnitPrice());
            parentItemAdInventoryReportVO.setSecondUnitPrice(parentBodyInventoryReportDO2.getUnitPrice());
            parentItemAdInventoryReportVO.setThirdUnitPrice(parentBodyInventoryReportDO3.getUnitPrice());
            parentItemAdInventoryReportVO.setFourthUnitPrice(parentBodyInventoryReportDO4.getUnitPrice());

            parentItemAdInventoryReportVO.setAfnFulfillableQuantity(parentBodyInventoryReportDO1.getAfnFulfillableQuantity());
            parentItemAdInventoryReportVO.setAfnInboundShippedQuantity(parentBodyInventoryReportDO1.getAfnInboundShippedQuantity());
            parentItemAdInventoryReportVO.setAfnInboundReceivingQuantity(parentBodyInventoryReportDO1.getAfnInboundReceivingQuantity());
            parentItemAdInventoryReportVO.setReservedFcTransfers(parentBodyInventoryReportDO1.getReservedFcTransfers());
            parentItemAdInventoryReportVO.setTotalInventory(parentBodyInventoryReportDO1.getTotalInventory());
            parentItemAdInventoryReportVO.setAfnUnsellableQuantity(parentBodyInventoryReportDO1.getAfnUnsellableQuantity());
            parentItemAdInventoryReportVO.setSumInvAge90PlusDays(parentBodyInventoryReportDO1.getSumInvAge90PlusDays());

            parentItemAdInventoryReportVO.setFirstSumImpressions(parentBodyInventoryReportDO1.getSumImpressions());
            parentItemAdInventoryReportVO.setSecondSumImpressions(parentBodyInventoryReportDO2.getSumImpressions());
            parentItemAdInventoryReportVO.setThirdSumImpressions(parentBodyInventoryReportDO3.getSumImpressions());
            parentItemAdInventoryReportVO.setFourthSumImpressions(parentBodyInventoryReportDO4.getSumImpressions());

            parentItemAdInventoryReportVO.setFirstSumClicks(parentBodyInventoryReportDO1.getSumClicks());
            parentItemAdInventoryReportVO.setSecondSumClicks(parentBodyInventoryReportDO2.getSumClicks());
            parentItemAdInventoryReportVO.setThirdSumClicks(parentBodyInventoryReportDO3.getSumClicks());
            parentItemAdInventoryReportVO.setFourthSumClicks(parentBodyInventoryReportDO4.getSumClicks());

            parentItemAdInventoryReportVO.setFirstSumCost(parentBodyInventoryReportDO1.getSumCost());
            parentItemAdInventoryReportVO.setSecondSumCost(parentBodyInventoryReportDO2.getSumCost());
            parentItemAdInventoryReportVO.setThirdSumCost(parentBodyInventoryReportDO3.getSumCost());
            parentItemAdInventoryReportVO.setFourthSumCost(parentBodyInventoryReportDO4.getSumCost());

            parentItemAdInventoryReportVO.setFirstSumAttributedUnitsOrdered(parentBodyInventoryReportDO1.getSumAttributedUnitsOrdered());
            parentItemAdInventoryReportVO.setSecondSumAttributedUnitsOrdered(parentBodyInventoryReportDO2.getSumAttributedUnitsOrdered());
            parentItemAdInventoryReportVO.setThirdSumAttributedUnitsOrdered(parentBodyInventoryReportDO3.getSumAttributedUnitsOrdered());
            parentItemAdInventoryReportVO.setFourthSumAttributedUnitsOrdered(parentBodyInventoryReportDO4.getSumAttributedUnitsOrdered());

            parentItemAdInventoryReportVO.setFirstSumAttributedSales(parentBodyInventoryReportDO1.getSumAttributedSales());
            parentItemAdInventoryReportVO.setSecondSumAttributedSales(parentBodyInventoryReportDO2.getSumAttributedSales());
            parentItemAdInventoryReportVO.setThirdSumAttributedSales(parentBodyInventoryReportDO3.getSumAttributedSales());
            parentItemAdInventoryReportVO.setFourthSumAttributedSales(parentBodyInventoryReportDO4.getSumAttributedSales());

            parentItemAdInventoryReportVO.setFirstSumAttributedSalesSameSKU(parentBodyInventoryReportDO1.getSumAttributedSalesSameSKU());
            parentItemAdInventoryReportVO.setSecondSumAttributedSalesSameSKU(parentBodyInventoryReportDO2.getSumAttributedSalesSameSKU());
            parentItemAdInventoryReportVO.setThirdSumAttributedSalesSameSKU(parentBodyInventoryReportDO3.getSumAttributedSalesSameSKU());
            parentItemAdInventoryReportVO.setFourthSumAttributedSalesSameSKU(parentBodyInventoryReportDO4.getSumAttributedSalesSameSKU());

            parentItemAdInventoryReportVO.setFirstEffectiveConversionRate(parentBodyInventoryReportDO1.getEffectiveConversionRate());
            parentItemAdInventoryReportVO.setSecondEffectiveConversionRate(parentBodyInventoryReportDO2.getEffectiveConversionRate());
            parentItemAdInventoryReportVO.setThirdEffectiveConversionRate(parentBodyInventoryReportDO3.getEffectiveConversionRate());
            parentItemAdInventoryReportVO.setFourthEffectiveConversionRate(parentBodyInventoryReportDO4.getEffectiveConversionRate());

            parentItemAdInventoryReportVO.setFirstCTR(parentBodyInventoryReportDO1.getCTR());
            parentItemAdInventoryReportVO.setSecondCTR(parentBodyInventoryReportDO2.getCTR());
            parentItemAdInventoryReportVO.setThirdCTR(parentBodyInventoryReportDO3.getCTR());
            parentItemAdInventoryReportVO.setFourthCTR(parentBodyInventoryReportDO4.getCTR());

            parentItemAdInventoryReportVO.setFirstCR(parentBodyInventoryReportDO1.getCR());
            parentItemAdInventoryReportVO.setSecondCR(parentBodyInventoryReportDO2.getCR());
            parentItemAdInventoryReportVO.setThirdCR(parentBodyInventoryReportDO3.getCR());
            parentItemAdInventoryReportVO.setFourthCR(parentBodyInventoryReportDO4.getCR());

            parentItemAdInventoryReportVO.setFirstACoS(parentBodyInventoryReportDO1.getACoS());
            parentItemAdInventoryReportVO.setSecondACoS(parentBodyInventoryReportDO2.getACoS());
            parentItemAdInventoryReportVO.setThirdACoS(parentBodyInventoryReportDO3.getACoS());
            parentItemAdInventoryReportVO.setFourthACoS(parentBodyInventoryReportDO4.getACoS());

            parentItemAdInventoryReportVO.setFirstCPC(parentBodyInventoryReportDO1.getCPC());
            parentItemAdInventoryReportVO.setSecondCPC(parentBodyInventoryReportDO2.getCPC());
            parentItemAdInventoryReportVO.setThirdCPC(parentBodyInventoryReportDO3.getCPC());
            parentItemAdInventoryReportVO.setFourthCPC(parentBodyInventoryReportDO4.getCPC());

            parentItemAdInventoryReportVO.setFirstSumBuyerVisits(parentBodyInventoryReportDO1.getSumBuyerVisits());
            parentItemAdInventoryReportVO.setSecondSumBuyerVisits(parentBodyInventoryReportDO2.getSumBuyerVisits());
            parentItemAdInventoryReportVO.setThirdSumBuyerVisits(parentBodyInventoryReportDO3.getSumBuyerVisits());
            parentItemAdInventoryReportVO.setFourthSumBuyerVisits(parentBodyInventoryReportDO4.getSumBuyerVisits());

            parentItemAdInventoryReportVO.setFirstSalesViewsConversionRate(parentBodyInventoryReportDO1.getSalesViewsConversionRate());
            parentItemAdInventoryReportVO.setSecondSalesViewsConversionRate(parentBodyInventoryReportDO2.getSalesViewsConversionRate());
            parentItemAdInventoryReportVO.setThirdSalesViewsConversionRate(parentBodyInventoryReportDO3.getSalesViewsConversionRate());
            parentItemAdInventoryReportVO.setFourthSalesViewsConversionRate(parentBodyInventoryReportDO4.getSalesViewsConversionRate());

            parentItemAdInventoryReportVO.setExpectedDailySales(parentBodyInventoryReportDO1.getExpectedDailySales());
            parentItemAdInventoryReportVO.setExpected60DaysSales(parentBodyInventoryReportDO1.getExpected60DaysSales());
            parentItemAdInventoryReportVO.setExpectedTotalInventoryAfter60Days(parentBodyInventoryReportDO1.getExpectedTotalInventoryAfter60Days());
            parentItemAdInventoryReportVO.setExpectedAvailability(parentBodyInventoryReportDO1.getExpectedAvailability());
            parentItemAdInventoryReportVO.setExpectedSaleDate(parentBodyInventoryReportDO1.getExpectedSaleDate());
            parentItemAdInventoryReportVO.setShortageInventoryEstimates(parentBodyInventoryReportDO1.getShortageInventoryEstimates());

            parentItemAdInventoryReportVO.setFirstClicksVisitsRate(parentBodyInventoryReportDO1.getClicksVisitsRate());
            parentItemAdInventoryReportVO.setSecondClicksVisitsRate(parentBodyInventoryReportDO2.getClicksVisitsRate());
            parentItemAdInventoryReportVO.setThirdClicksVisitsRate(parentBodyInventoryReportDO3.getClicksVisitsRate());
            parentItemAdInventoryReportVO.setFourthClicksVisitsRate(parentBodyInventoryReportDO4.getClicksVisitsRate());

            parentItemAdInventoryReportVO.setFirstAdsRevRate(parentBodyInventoryReportDO1.getAdsRevRate());
            parentItemAdInventoryReportVO.setSecondAdsRevRate(parentBodyInventoryReportDO2.getAdsRevRate());
            parentItemAdInventoryReportVO.setThirdAdsRevRate(parentBodyInventoryReportDO3.getAdsRevRate());
            parentItemAdInventoryReportVO.setFourthAdsRevRate(parentBodyInventoryReportDO4.getAdsRevRate());

            parentItemAdInventoryReportVO.setFirstAdsCostRate(parentBodyInventoryReportDO1.getAdsCostRate());
            parentItemAdInventoryReportVO.setSecondAdsCostRate(parentBodyInventoryReportDO2.getAdsCostRate());
            parentItemAdInventoryReportVO.setThirdAdsCostRate(parentBodyInventoryReportDO3.getAdsCostRate());
            parentItemAdInventoryReportVO.setFourthAdsCostRate(parentBodyInventoryReportDO4.getAdsCostRate());
            //将VO对象加到resultList中
            resultList.add(parentItemAdInventoryReportVO);
        }
        String pSku = MapUtils.getString(maps, "parentSku");
        List<ParentItemAdInventoryReportVO> results = new ArrayList();
        for (int i = 0; i < resultList.size(); i++) {
            Pattern pattern = Pattern.compile(pSku);
            Matcher matcher = pattern.matcher(((ParentItemAdInventoryReportVO) resultList.get(i)).getParentSKU());
            if (matcher.find()) {
                results.add(resultList.get(i));
            }
        }
        //获得前端传递的排序的参数值进行排序
        String field = String.valueOf(maps.get("field"));
        String sort = String.valueOf(maps.get("sort"));
        SortListUtils.sort(results, field, sort);
        PageUtil pageList = PageUtil.Pagination(pageNum, pageSize, results);
        pageList.setTotal((long) results.size());
        return ResultUtil.success(pageList);
    }
}
