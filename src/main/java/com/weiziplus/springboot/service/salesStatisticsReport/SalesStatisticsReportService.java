package com.weiziplus.springboot.service.salesStatisticsReport;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.salesStatisticsReport.SalesStatisticsReportMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.DO.*;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.VO.SalesStatisticsReportVO;
import com.weiziplus.springboot.models.VO.ShopSalesStatisticsDataVO;
import com.weiziplus.springboot.utils.AreaCastUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.DocFlavor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class SalesStatisticsReportService extends BaseService {
    @Autowired
    SalesStatisticsReportMapper salesStatisticsReportMapper;
    @Autowired
    ShopMapper shopMapper;
    @Autowired
    ShopAreaMapper shopAreaMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResultUtil salesStatisticsReport() {
        List<SalesStatisticsReportVO> salesStatisticsReportVOList = null;
        LocalDate localDate = LocalDate.now().minusDays(1);
        Integer year = localDate.getYear();
        Integer month = localDate.getMonth().getValue();
        try {
            salesStatisticsReportVOList = (List<SalesStatisticsReportVO>) RedisUtil.get("salesStatisticsReport");
        }catch (Exception e){
            log.error(e.getMessage());
        }
        if (salesStatisticsReportVOList != null){
            return ResultUtil.success(salesStatisticsReportVOList);
        }
        salesStatisticsReportVOList = new ArrayList<>();
        //获得目前的各个国家对于美元的月平均汇率
        List<ExchangeRateDO> exchangeRateDOList = salesStatisticsReportMapper.getExchangeRate();
        //获得所有国家店铺的最近预留、在途等库存和所有店铺最近四周的销量
        List<InventorySumDO> manageFbaInventorySumList = salesStatisticsReportMapper.getManageFbaInventorySum();
        List<InventorySumDO> reservedInventorySumList = salesStatisticsReportMapper.getReservedInventorySum();
        //获得各个大组的名称
        List<SalesIndicatorDO> groupNameList = salesStatisticsReportMapper.getSalesIndicatorName(year);
        if (groupNameList == null) {
            return ResultUtil.error("目前没有销售指标，请先添加！");
        }
        //根据大组名称查询所有绑定的店铺和区域
        for (SalesIndicatorDO salesIndicatorGroupName : groupNameList) {
            String groupName = salesIndicatorGroupName.getGroupName();
            SalesStatisticsReportVO salesStatisticsReportVO = new SalesStatisticsReportVO();
            //定义当月组合计，全年组合计，当月销售完成度，全年销售完成度
            String groupAnnualSalesCompletion = "";
            String groupMonthSalesCompletion = "";
            BigDecimal totalGroupAnnualSales = BigDecimal.valueOf(0);
            BigDecimal totalGroupMonthSales = BigDecimal.valueOf(0);
            List<SalesIndicatorDO> salesIndicatorDOList = salesStatisticsReportMapper.getSalesIndicatorByGroupName(groupName, year);
            if (salesIndicatorDOList.size() == 0 || salesIndicatorDOList == null) {
                return ResultUtil.error("请先添加大组信息");
            }
            //各个店铺的销售数据集合
            List<ShopSalesStatisticsDataVO> shopSalesStatisticsDataVOList = new ArrayList<>();
            //根据店铺和区域查询对应的数据，在该大组中的一个具体区域店铺的数据
            for (SalesIndicatorDO salesIndicatorDO : salesIndicatorDOList) {
                String sellerId = salesIndicatorDO.getSellerId();
                String areaCode = salesIndicatorDO.getArea();
                Shop shop = shopMapper.getOneInfoBySellerId(sellerId);
                String shopName = shop.getShopName();
                String salesChannel = AreaCastUtil.castToSalesChannel(areaCode);
                //计算该店铺该区域的最新的各库存和，并计算（可售+预留）/销量和（可售+预留+在途）/销量
                InventorySumDO manageFbaInventorySum = new InventorySumDO();
                InventorySumDO reservedInventorySum = new InventorySumDO();
                MANAGEINVENTORY:
                for (InventorySumDO inventorySumDO : manageFbaInventorySumList) {
                    if (shopName.equals(inventorySumDO.getShopName()) && areaCode.equals(inventorySumDO.getAreaCode())) {
                        manageFbaInventorySum = inventorySumDO;
                        break MANAGEINVENTORY;
                    }
                }
                RESERVEDINVENTORY:
                for (InventorySumDO inventorySumDO : reservedInventorySumList) {
                    if (shopName.equals(inventorySumDO.getShopName()) && areaCode.equals(inventorySumDO.getAreaCode())) {
                        reservedInventorySum = inventorySumDO;
                        break RESERVEDINVENTORY;
                    }
                }
                //可售+预留
                Integer availableStocks = manageFbaInventorySum.getSumAfnFulfillableQuantity() + manageFbaInventorySum.getSumAfnInboundReceivingQuantity() + reservedInventorySum.getSumReservedFcTransfers();
                //可售+预留+在途
                Integer allStocks = manageFbaInventorySum.getSumAfnFulfillableQuantity() + manageFbaInventorySum.getSumAfnInboundReceivingQuantity() + reservedInventorySum.getSumReservedFcTransfers() + manageFbaInventorySum.getSumAfnInboundShippedQuantity();
                SumQuantityDO SumQuantityInOneWeek = new SumQuantityDO(shopName, areaCode, 1);
                SumQuantityDO SumQuantityInTwoWeek = new SumQuantityDO(shopName, areaCode, 2);
                SumQuantityDO SumQuantityInThreeWeek = new SumQuantityDO(shopName, areaCode, 3);
                SumQuantityDO SumQuantityInFourWeek = new SumQuantityDO(shopName, areaCode, 4);
                Integer dailyAverageQuantityInOneWeek = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shopName, areaCode, 1);
                Integer dailyAverageQuantityInTwoWeek = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shopName, areaCode, 2);
                Integer dailyAverageQuantityInThreeWeek = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shopName, areaCode, 3);
                Integer dailyAverageQuantityInFourWeek = salesStatisticsReportMapper.getDailyAverageQuantityByDate(shopName, areaCode, 4);
                if (dailyAverageQuantityInOneWeek != null) {
                    SumQuantityInOneWeek.setSumQuantity(dailyAverageQuantityInOneWeek);
                }
                if (dailyAverageQuantityInTwoWeek != null) {
                    SumQuantityInTwoWeek.setSumQuantity(dailyAverageQuantityInTwoWeek);
                }
                if (dailyAverageQuantityInThreeWeek != null) {
                    SumQuantityInThreeWeek.setSumQuantity(dailyAverageQuantityInThreeWeek);
                }
                if (dailyAverageQuantityInFourWeek != null) {
                    SumQuantityInFourWeek.setSumQuantity(dailyAverageQuantityInFourWeek);
                }
                dailyAverageQuantityInOneWeek = SumQuantityInOneWeek.getSumQuantity();
                dailyAverageQuantityInTwoWeek = SumQuantityInTwoWeek.getSumQuantity();
                dailyAverageQuantityInThreeWeek = SumQuantityInThreeWeek.getSumQuantity();
                dailyAverageQuantityInFourWeek = SumQuantityInFourWeek.getSumQuantity();
                BigDecimal dailyAverageQuantity = BigDecimal.valueOf(dailyAverageQuantityInOneWeek).divide(BigDecimal.valueOf(7),2,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.6))
                        .add(BigDecimal.valueOf(dailyAverageQuantityInOneWeek).add(BigDecimal.valueOf(dailyAverageQuantityInTwoWeek)).divide(BigDecimal.valueOf(14),2,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.3)))
                        .add((BigDecimal.valueOf(dailyAverageQuantityInOneWeek).add(BigDecimal.valueOf(dailyAverageQuantityInTwoWeek)).add(BigDecimal.valueOf(dailyAverageQuantityInThreeWeek)).add(BigDecimal.valueOf(dailyAverageQuantityInFourWeek))).divide(BigDecimal.valueOf(28),2,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.1)));
                BigDecimal availableStocksSoldOutTime = BigDecimal.valueOf(0);
                BigDecimal allStocksSoldOutTime = BigDecimal.valueOf(0);
                if (dailyAverageQuantity.compareTo(BigDecimal.valueOf(0)) != 0) {
                    availableStocksSoldOutTime = BigDecimal.valueOf(manageFbaInventorySum.getSumAfnFulfillableQuantity() + manageFbaInventorySum.getSumAfnInboundReceivingQuantity() + reservedInventorySum.getSumReservedFcTransfers()).divide(dailyAverageQuantity, 2, RoundingMode.HALF_UP);
                    allStocksSoldOutTime = BigDecimal.valueOf(manageFbaInventorySum.getSumAfnFulfillableQuantity() + manageFbaInventorySum.getSumAfnInboundReceivingQuantity() + reservedInventorySum.getSumReservedFcTransfers() + manageFbaInventorySum.getSumAfnInboundShippedQuantity()).divide(dailyAverageQuantity, 2, RoundingMode.HALF_UP);
                }
                //去数据库查询该店铺该区域在今年的数据
                ShopSalesStatisticsDataVO shopSalesStatisticsDataVO = salesStatisticsReportMapper.getSalesAmountFromExistingData(sellerId, areaCode, year);
                //累加当月组销售额
                switch (month) {
                    case 1:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getJanuarySales());
                        break;
                    case 2:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getFebruarySales());
                        break;
                    case 3:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getMarchSales());
                        break;
                    case 4:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getAprilSales());
                        break;
                    case 5:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getMaySales());
                        break;
                    case 6:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getJuneSales());
                        break;
                    case 7:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getJulySales());
                        break;
                    case 8:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getAugustSales());
                        break;
                    case 9:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getSeptemberSales());
                        break;
                    case 10:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getOctoberSales());
                        break;
                    case 11:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getNovemberSales());
                        break;
                    case 12:
                        totalGroupMonthSales = totalGroupMonthSales.add(shopSalesStatisticsDataVO.getDecemberSales());
                        break;
                }
                shopSalesStatisticsDataVO.setAvailableStocksSoldOutTime(availableStocksSoldOutTime);
                shopSalesStatisticsDataVO.setAllStocksSoldOutTime(allStocksSoldOutTime);
                shopSalesStatisticsDataVO.setAvailableStocks(availableStocks);
                shopSalesStatisticsDataVO.setAllStocks(allStocks);
                shopSalesStatisticsDataVO.setDailyAverageQuantity(dailyAverageQuantity.doubleValue());

                shopSalesStatisticsDataVOList.add(shopSalesStatisticsDataVO);
                //累加组全年总销售额
                totalGroupAnnualSales = totalGroupAnnualSales.add(shopSalesStatisticsDataVO.getTotalAnnualSales());
            }

            salesStatisticsReportVO.setShopSalesStatisticsDataVOList(shopSalesStatisticsDataVOList);
            //还要添加一些完成度数据,groupName,当月组合计，全年组合计，当月销售完成度，全年销售完成度
            salesStatisticsReportVO.setGroupName(groupName);
            groupAnnualSalesCompletion = totalGroupAnnualSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getTotalIndicator()), 2, RoundingMode.HALF_UP) + "%";
            salesStatisticsReportVO.setTotalGroupMonthSales(totalGroupMonthSales);
            salesStatisticsReportVO.setTotalGroupAnnualSales(totalGroupAnnualSales);
            switch (month) {
                case 1:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getJanuaryIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 2:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getFebruaryIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 3:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getMarchIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 4:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getAprilIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 5:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getMayIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 6:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getJuneIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 7:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getJulyIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 8:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getAugustIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 9:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getSeptemberIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 10:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getOctoberIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 11:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getNovemberIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
                case 12:
                    salesStatisticsReportVO.setGroupMonthSalesCompletion(totalGroupMonthSales.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(salesIndicatorGroupName.getDecemberIndicator()), 2, RoundingMode.HALF_UP) + "%");
                    break;
            }

            salesStatisticsReportVO.setGroupAnnualSalesCompletion(groupAnnualSalesCompletion);
            salesStatisticsReportVO.setSalesIndicatorDO(salesIndicatorGroupName);
            //遍历该大组的所有店铺销售数据，将同一个店的区域合并
            List<ShopSalesStatisticsDataVO> shopSalesStatisticsDataVOS = salesStatisticsReportVO.getShopSalesStatisticsDataVOList();
            //System.out.println(shopSalesStatisticsDataVOS.toString());
            Map<String,ShopSalesStatisticsDataVO> map = new HashMap<>();
            //先判断是否是同一个店铺名，如果是就把数据相加存入map中
            for(ShopSalesStatisticsDataVO shopSalesStatisticsDataVO : shopSalesStatisticsDataVOS){
                String shopName = shopSalesStatisticsDataVO.getShopName();
                if (map.get(shopName) == null){
                    map.put(shopName,shopSalesStatisticsDataVO);
                }else {
                    map.get(shopName).setAreaCode(map.get(shopName).getAreaCode() + "&" + shopSalesStatisticsDataVO.getAreaCode());
                    map.get(shopName).setJanuarySales(map.get(shopName).getJanuarySales().add(shopSalesStatisticsDataVO.getJanuarySales()));
                    map.get(shopName).setFebruarySales(map.get(shopName).getFebruarySales().add(shopSalesStatisticsDataVO.getFebruarySales()));
                    map.get(shopName).setMarchSales(map.get(shopName).getMarchSales().add(shopSalesStatisticsDataVO.getMarchSales()));
                    map.get(shopName).setAprilSales(map.get(shopName).getAprilSales().add(shopSalesStatisticsDataVO.getAprilSales()));
                    map.get(shopName).setMaySales(map.get(shopName).getMaySales().add(shopSalesStatisticsDataVO.getMaySales()));
                    map.get(shopName).setJuneSales(map.get(shopName).getJuneSales().add(shopSalesStatisticsDataVO.getJuneSales()));
                    map.get(shopName).setJulySales(map.get(shopName).getJulySales().add(shopSalesStatisticsDataVO.getJulySales()));
                    map.get(shopName).setAugustSales(map.get(shopName).getAugustSales().add(shopSalesStatisticsDataVO.getAugustSales()));
                    map.get(shopName).setSeptemberSales(map.get(shopName).getSeptemberSales().add(shopSalesStatisticsDataVO.getSeptemberSales()));
                    map.get(shopName).setOctoberSales(map.get(shopName).getOctoberSales().add(shopSalesStatisticsDataVO.getOctoberSales()));
                    map.get(shopName).setNovemberSales(map.get(shopName).getNovemberSales().add(shopSalesStatisticsDataVO.getNovemberSales()));
                    map.get(shopName).setDecemberSales(map.get(shopName).getDecemberSales().add(shopSalesStatisticsDataVO.getDecemberSales()));
                    map.get(shopName).setTotalAnnualSales(map.get(shopName).getTotalAnnualSales().add(shopSalesStatisticsDataVO.getTotalAnnualSales()));
                    map.get(shopName).setAvailableStocks(map.get(shopName).getAvailableStocks() + shopSalesStatisticsDataVO.getAvailableStocks());
                    map.get(shopName).setAllStocks(map.get(shopName).getAllStocks() + shopSalesStatisticsDataVO.getAllStocks());
                    map.get(shopName).setDailyAverageQuantity(map.get(shopName).getDailyAverageQuantity() + shopSalesStatisticsDataVO.getDailyAverageQuantity());
                }
            }
            //将map转为对应的店铺销售数据集合shopSalesStatisticsDataVOList
            Set<Map.Entry<String,ShopSalesStatisticsDataVO>> entrySet = map.entrySet();
            List<ShopSalesStatisticsDataVO> allAreaShopSalesStatisticsDataVOList = new ArrayList<>();
            for (Map.Entry<String,ShopSalesStatisticsDataVO> entry: entrySet) {
                ShopSalesStatisticsDataVO shopSalesStatisticsDataVO = entry.getValue();
                if (shopSalesStatisticsDataVO.getDailyAverageQuantity() != 0) {
                    shopSalesStatisticsDataVO.setAvailableStocksSoldOutTime(BigDecimal.valueOf(shopSalesStatisticsDataVO.getAvailableStocks()).divide(BigDecimal.valueOf(shopSalesStatisticsDataVO.getDailyAverageQuantity()), 2, RoundingMode.HALF_UP));
                    shopSalesStatisticsDataVO.setAllStocksSoldOutTime(BigDecimal.valueOf(shopSalesStatisticsDataVO.getAllStocks()).divide(BigDecimal.valueOf(shopSalesStatisticsDataVO.getDailyAverageQuantity()), 2, RoundingMode.HALF_UP));
                }
                allAreaShopSalesStatisticsDataVOList.add(shopSalesStatisticsDataVO);
            }
            salesStatisticsReportVO.setShopSalesStatisticsDataVOList(allAreaShopSalesStatisticsDataVOList);
            salesStatisticsReportVOList.add(salesStatisticsReportVO);

        }
        RedisUtil.set("salesStatisticsReport",salesStatisticsReportVOList,60*60L);
        return ResultUtil.success(salesStatisticsReportVOList);
    }
}
