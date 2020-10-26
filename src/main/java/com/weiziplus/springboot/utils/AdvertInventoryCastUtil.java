package com.weiziplus.springboot.utils;

import com.weiziplus.springboot.models.DO.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvertInventoryCastUtil {

    public static List<ChildBodyInventoryReportDO> castToChildBodyInventoryReportDO(List<QuantityAndUnitPriceDO> quantityAndUnitPriceDOList, List<InventoryDO> inventoryDOList, List<AdvProductDataDO> advProductDataDOList, List<BuyerVisitsDO> buyerVisitsDOList,List<LightningDealsDataDO> lightningDealsDataDOList){
        List<ChildBodyInventoryReportDO> childBodyInventoryReportDOList = new ArrayList<>();
        for (int i = 0; i < quantityAndUnitPriceDOList.size(); i++) {
            ChildBodyInventoryReportDO childBodyInventoryReportDO = new ChildBodyInventoryReportDO();
            QuantityAndUnitPriceDO quantityAndUnitPriceDO = quantityAndUnitPriceDOList.get(i);
            InventoryDO inventoryDO = inventoryDOList.get(i);
            AdvProductDataDO advProductDataDO = advProductDataDOList.get(i);
            BuyerVisitsDO buyerVisitsDO = buyerVisitsDOList.get(i);
            LightningDealsDataDO lightningDealsDataDO = lightningDealsDataDOList.get(i);

            Integer sumQuantity = quantityAndUnitPriceDO.getSumQuantity();
            BigDecimal unitPrice = quantityAndUnitPriceDO.getUnitPrice();
            BigDecimal sumSalesAmount = quantityAndUnitPriceDO.getSumSalesAmount();
            Integer expectedDailySales = quantityAndUnitPriceDO.getExpectedDailySales();
            Integer expected60DaysSales = quantityAndUnitPriceDO.getExpected60DaysSales();

            Integer afnFulfillableQuantity = inventoryDO.getAfnFulfillableQuantity();
            Integer afnInboundShippedQuantity = inventoryDO.getAfnInboundShippedQuantity();
            Integer afnInboundReceivingQuantity = inventoryDO.getAfnInboundReceivingQuantity();
            Integer reservedFcTransfers = inventoryDO.getReservedFcTransfers();
            Integer totalInventory = inventoryDO.getTotalInventory();
            Integer afnUnsellableQuantity = inventoryDO.getAfnUnsellableQuantity();
            Integer sumInvAge90PlusDays = inventoryDO.getSumInvAge90PlusDays();

            BigDecimal sumImpressions = advProductDataDO.getSumImpressions();
            BigDecimal sumClicks = advProductDataDO.getSumClicks();
            BigDecimal sumCost = advProductDataDO.getSumCost();
            BigDecimal sumAttributedUnitsOrdered = advProductDataDO.getSumAttributedUnitsOrdered();
            BigDecimal sumAttributedSales = advProductDataDO.getSumAttributedSales();
            BigDecimal sumAttributedSalesSameSKU = advProductDataDO.getSumAttributedSalesSameSKU();
            String effectiveConversionRate = advProductDataDO.getEffectiveConversionRate();
            String CTR = advProductDataDO.getCTR();
            String CR = advProductDataDO.getCR();
            String ACoS = advProductDataDO.getACoS();
            String CPC = advProductDataDO.getCPC();

            BigDecimal sumBuyerVisits = buyerVisitsDO.getSumBuyerVisits();

            Integer lightningDealsWeek = lightningDealsDataDO.getWeekOfYear();

            childBodyInventoryReportDO.setCSku(quantityAndUnitPriceDO.getCSku());
            childBodyInventoryReportDO.setCAsin(quantityAndUnitPriceDO.getCAsin());
            childBodyInventoryReportDO.setPSku(quantityAndUnitPriceDO.getPSku());
            childBodyInventoryReportDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            childBodyInventoryReportDO.setSumQuantity(sumQuantity);
            childBodyInventoryReportDO.setUnitPrice(unitPrice);
            childBodyInventoryReportDO.setAfnFulfillableQuantity(afnFulfillableQuantity);
            childBodyInventoryReportDO.setAfnInboundShippedQuantity(afnInboundShippedQuantity);
            childBodyInventoryReportDO.setAfnInboundReceivingQuantity(afnInboundReceivingQuantity);
            childBodyInventoryReportDO.setReservedFcTransfers(reservedFcTransfers);
            childBodyInventoryReportDO.setTotalInventory(totalInventory);
            childBodyInventoryReportDO.setAfnUnsellableQuantity(afnUnsellableQuantity);
            childBodyInventoryReportDO.setSumInvAge90PlusDays(sumInvAge90PlusDays);
            childBodyInventoryReportDO.setSumImpressions(sumImpressions);
            childBodyInventoryReportDO.setSumClicks(sumClicks);
            childBodyInventoryReportDO.setSumCost(sumCost);
            childBodyInventoryReportDO.setSumAttributedUnitsOrdered(sumAttributedUnitsOrdered);
            childBodyInventoryReportDO.setSumAttributedSales(sumAttributedSales);
            childBodyInventoryReportDO.setSumAttributedSalesSameSKU(sumAttributedSalesSameSKU);
            childBodyInventoryReportDO.setEffectiveConversionRate(effectiveConversionRate);
            childBodyInventoryReportDO.setCTR(CTR);
            childBodyInventoryReportDO.setCR(CR);
            childBodyInventoryReportDO.setACoS(ACoS);
            childBodyInventoryReportDO.setCPC(CPC);
            childBodyInventoryReportDO.setSumBuyerVisits(sumBuyerVisits);
            if (sumBuyerVisits.compareTo(BigDecimal.valueOf(0)) != 0){
                childBodyInventoryReportDO.setSalesViewsConversionRate(BigDecimal.valueOf(sumQuantity).multiply(BigDecimal.valueOf(100)).divide(sumBuyerVisits,2, RoundingMode.HALF_UP) + "%");
                childBodyInventoryReportDO.setClicksVisitsRate(sumClicks.multiply(BigDecimal.valueOf(100)).divide(sumBuyerVisits, 2, RoundingMode.HALF_UP) + "%");
            }else {
                childBodyInventoryReportDO.setSalesViewsConversionRate(String.valueOf(0));
                childBodyInventoryReportDO.setClicksVisitsRate("0.00%");
            }
            childBodyInventoryReportDO.setExpectedDailySales(expectedDailySales);
            childBodyInventoryReportDO.setExpected60DaysSales(expected60DaysSales);
            if (expected60DaysSales != 0){
                childBodyInventoryReportDO.setExpectedTotalInventoryAfter60Days(BigDecimal.valueOf(totalInventory).divide(BigDecimal.valueOf(expected60DaysSales),0,RoundingMode.HALF_UP).intValue());
            }else {
                childBodyInventoryReportDO.setExpectedTotalInventoryAfter60Days(0);
            }
            if (expectedDailySales != 0){
                childBodyInventoryReportDO.setExpectedSaleDate(LocalDate.now().minusDays(-BigDecimal.valueOf(totalInventory).divide(BigDecimal.valueOf(expectedDailySales),0,RoundingMode.HALF_UP).intValue()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                childBodyInventoryReportDO.setExpectedAvailability(BigDecimal.valueOf(totalInventory).divide(BigDecimal.valueOf(expectedDailySales),0,RoundingMode.HALF_UP).intValue());
            }else {
                childBodyInventoryReportDO.setExpectedSaleDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                childBodyInventoryReportDO.setExpectedAvailability(0);
            }
            childBodyInventoryReportDO.setShortageInventoryEstimates(totalInventory - expectedDailySales * 20);
            if (sumSalesAmount.compareTo(BigDecimal.valueOf(0)) != 0){
                childBodyInventoryReportDO.setAdsRevRate(sumAttributedSalesSameSKU.multiply(BigDecimal.valueOf(100)).divide(sumSalesAmount, 2, RoundingMode.HALF_UP) + "%");
                childBodyInventoryReportDO.setAdsCostRate(sumCost.multiply(BigDecimal.valueOf(100)).divide(sumSalesAmount, 2, RoundingMode.HALF_UP) + "%");
            }else {
                childBodyInventoryReportDO.setAdsRevRate("0.00%");
                childBodyInventoryReportDO.setAdsCostRate("0.00%");
            }
            childBodyInventoryReportDO.setLightningDealsWeek(lightningDealsWeek);
            childBodyInventoryReportDOList.add(childBodyInventoryReportDO);
        }
        return childBodyInventoryReportDOList;
    }

}
