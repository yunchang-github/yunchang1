package com.weiziplus.springboot.service.advertisingInventoryReport;

import com.weiziplus.springboot.mapper.advertisingInventoryReport.AdvertInventoryReportMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.InventoryReportMapper;
import com.weiziplus.springboot.mapper.salesStatisticsReport.SalesStatisticsReportMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.DO.*;
import com.weiziplus.springboot.models.VO.ChildItemAdInventoryReportVO;
import com.weiziplus.springboot.utils.*;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HandleSalesAndTrafficReportService {
    @Autowired
    AdvertInventoryReportMapper advertInventoryReportMapper;

    @Autowired
    InventoryReportMapper inventoryReportMapper;

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    SalesStatisticsReportMapper salesStatisticsReportMapper;

    public Object exportChildSalesAndTrafficReport(HttpServletResponse response, Map maps) throws Exception {
        //获得对应的数据
        //设置标志，1表示获取子体报告
        int flag = 1;
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
                    String psku = childBodyInventoryReportDO.getPSku();
                    psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                    childBodyInventoryReportDO.setPSku(psku);
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
                    //去掉ASCII码160空格
                    String psku = childBodyInventoryReportDO.getPSku();
                    psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                    childBodyInventoryReportDO.setPSku(psku);
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
                    String psku = childBodyInventoryReportDO.getPSku();
                    psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                    childBodyInventoryReportDO.setPSku(psku);
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
                    String psku = childBodyInventoryReportDO.getPSku();
                    psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                    childBodyInventoryReportDO.setPSku(psku);
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
        List<ChildBodyInventoryReportDO> childBodyInventoryReportDOs = new ArrayList<>();
        childBodyInventoryReportDOs.addAll(list1);
        childBodyInventoryReportDOs.addAll(list2);
        childBodyInventoryReportDOs.addAll(list3);
        childBodyInventoryReportDOs.addAll(list4);
        String cSku = MapUtils.getString(maps,"sku");
        String cAsin = MapUtils.getString(maps,"asin");
        String pSku = MapUtils.getString(maps,"parentSku");
        List<ChildBodyInventoryReportDO> results = new ArrayList();
        for(int i=0; i < childBodyInventoryReportDOs.size(); i++){
            Pattern pattern1 = Pattern.compile(cSku);
            Pattern pattern2 = Pattern.compile(cAsin);
            Pattern pattern3 = Pattern.compile(pSku);
            Matcher matcher1 = pattern1.matcher((childBodyInventoryReportDOs.get(i)).getCSku());
            Matcher matcher2 = pattern2.matcher((childBodyInventoryReportDOs.get(i)).getCAsin());
            Matcher matcher3 = pattern3.matcher((childBodyInventoryReportDOs.get(i)).getPSku());
            if(matcher1.find() && matcher2.find() && matcher3.find()){
                results.add(childBodyInventoryReportDOs.get(i));
            }
        }
        //定义Excel表的基本信息
        String sheetName = "子体";
        String titleName = "子体广告库存报表";
        String fileName = "子体广告库存报表-" + shop + "-" + area + ".xls";
        int columnNumber = 32;
        int[] columnWidth = {30, 30, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        //Excel表中的字段名
        String[] columnName = {"子SKU", "子asin", "父sku", "时间类型", "时间", "销量", "可售库存", "不可售库存", "待签收库存", "已签收待处理库存", "转库中库存",
                "库龄大于90天库存", "总库存", "预计可售天数", "预计每日销量", "销售单价", "广告曝光量", "广告点击量", "广告费用", "广告销售量", "广告销售额",
                "广告sku销售额", "广告有效转化率", "广告CTR", "广告CR", "广告ACoS", "广告CPC", "访客人数", "转化率", "Click/访客人数", "Ads Rev/Rev%",
                "Ads Cost/Rev%"};
        //类中的属性名
        String[] propertyName = {"cSku", "cAsin", "pSku", "dateType", "date", "sumQuantity", "afnFulfillableQuantity", "afnUnsellableQuantity",
                "afnInboundShippedQuantity", "afnInboundReceivingQuantity", "reservedFcTransfers", "sumInvAge90PlusDays", "totalInventory",
                "expected60DaysSales", "expectedDailySales", "unitPrice", "sumImpressions", "sumClicks", "sumCost", "sumAttributedUnitsOrdered",
                "sumAttributedSales", "sumAttributedSalesSameSKU", "effectiveConversionRate", "CTR", "CR", "ACoS", "CPC", "sumBuyerVisits",
                "salesViewsConversionRate", "clicksVisitsRate", "adsRevRate", "adsCostRate"};
        //处理数据
        ByteArrayInputStream byteArrayInputStream = ExcelHandleUtil.ExportAavertInventoryReportWithResponse(sheetName,
                titleName, fileName, columnNumber, columnWidth, columnName, propertyName, results, response);
        //renderBinary(byteArrayInputStream,fileName);
        return byteArrayInputStream;
    }

    /**
     * 导出父体Excel
     * */
    public Object exportParentSalesAndTrafficReport(HttpServletResponse response, Map maps) throws Exception {
        //获得对应的数据
        //设置标志，2表示获取父体报告
        int flag = 2;
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
                String psku = childBodyInventoryReportDO.getPSku();
                psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                childBodyInventoryReportDO.setPSku(psku);
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
                String psku = childBodyInventoryReportDO.getPSku();
                psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                childBodyInventoryReportDO.setPSku(psku);
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
                String psku = childBodyInventoryReportDO.getPSku();
                psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                childBodyInventoryReportDO.setPSku(psku);
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
                String psku = childBodyInventoryReportDO.getPSku();
                psku =  psku.replaceAll("[\\s\\u00A0]+","").trim();
                childBodyInventoryReportDO.setPSku(psku);
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
        List<ChildBodyInventoryReportDO> childBodyInventoryReportDOs = new ArrayList<>();
        childBodyInventoryReportDOs.addAll(list1);
        childBodyInventoryReportDOs.addAll(list2);
        childBodyInventoryReportDOs.addAll(list3);
        childBodyInventoryReportDOs.addAll(list4);
        String pSku = MapUtils.getString(maps,"parentSku");
        List<ChildBodyInventoryReportDO> results = new ArrayList();
        for(int i=0; i < childBodyInventoryReportDOs.size(); i++){
            Pattern pattern = Pattern.compile(pSku);
            Matcher matcher = pattern.matcher((childBodyInventoryReportDOs.get(i)).getPSku());
            if(matcher.find()){
                results.add(childBodyInventoryReportDOs.get(i));
            }
        }
        //定义Excel表的基本信息
        String sheetName = "父体";
        String titleName = "父体广告库存报表";
        String fileName = "父体广告库存报表-" + shop + "-" + area + ".xls";
        int columnNumber = 30;
        int[] columnWidth = {30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        //Excel表中的字段名
        String[] columnName = {"父sku", "时间类型", "时间", "销量", "可售库存", "不可售库存", "待签收库存", "已签收待处理库存", "转库中库存",
                "库龄大于90天库存", "总库存", "预计可售天数", "预计每日销量", "销售单价", "广告曝光量", "广告点击量", "广告费用", "广告销售量", "广告销售额",
                "广告sku销售额", "广告有效转化率", "广告CTR", "广告CR", "广告ACoS", "广告CPC", "访客人数", "转化率", "Click/访客人数", "Ads Rev/Rev%",
                "Ads Cost/Rev%"};
        //类中的属性名
        String[] propertyName = {"pSku", "dateType", "date", "sumQuantity", "afnFulfillableQuantity", "afnUnsellableQuantity",
                "afnInboundShippedQuantity", "afnInboundReceivingQuantity", "reservedFcTransfers", "sumInvAge90PlusDays", "totalInventory",
                "expected60DaysSales", "expectedDailySales", "unitPrice", "sumImpressions", "sumClicks", "sumCost", "sumAttributedUnitsOrdered",
                "sumAttributedSales", "sumAttributedSalesSameSKU", "effectiveConversionRate", "CTR", "CR", "ACoS", "CPC", "sumBuyerVisits",
                "salesViewsConversionRate", "clicksVisitsRate", "adsRevRate", "adsCostRate"};
        //处理数据
        ByteArrayInputStream byteArrayInputStream = ExcelHandleUtil.ExportAavertInventoryReportWithResponse(sheetName,
                titleName, fileName, columnNumber, columnWidth, columnName, propertyName, results, response);
        //renderBinary(byteArrayInputStream,fileName);
        return byteArrayInputStream;
    }
}
