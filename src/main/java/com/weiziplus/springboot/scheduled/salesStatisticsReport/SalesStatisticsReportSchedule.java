package com.weiziplus.springboot.scheduled.salesStatisticsReport;

import com.weiziplus.springboot.mapper.salesStatisticsReport.SalesStatisticsReportMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.DO.*;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.VO.SalesStatisticsReportVO;
import com.weiziplus.springboot.models.VO.ShopSalesStatisticsDataVO;
import com.weiziplus.springboot.utils.AreaCastUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Service
public class SalesStatisticsReportSchedule {
    @Autowired
    SalesStatisticsReportMapper salesStatisticsReportMapper;
    @Autowired
    ShopMapper shopMapper;

   //@Scheduled(cron = "00 20 04 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateSalesStatisticsReport(){
        LocalDate localDate = LocalDate.now();
        Integer year = localDate.getYear();
        Integer month = localDate.getMonth().getValue();
        //获得目前的各个国家对于美元的月平均汇率
        List<ExchangeRateDO> exchangeRateDOList = salesStatisticsReportMapper.getExchangeRate();
        //获得各个大组的名称
        List<SalesIndicatorDO> groupNameList = salesStatisticsReportMapper.getSalesIndicatorName(year);
        if (groupNameList == null) {
            log.warn("目前没有销售指标，请先添加！");
            return ;
        }
        //根据大组名称查询所有绑定的店铺和区域
        for (SalesIndicatorDO salesIndicatorGroupName : groupNameList) {
            String groupName = salesIndicatorGroupName.getGroupName();
            SalesStatisticsReportVO salesStatisticsReportVO = new SalesStatisticsReportVO();
            //获得对应年份和组名的各店铺销售指标
            List<SalesIndicatorDO> salesIndicatorDOList = salesStatisticsReportMapper.getSalesIndicatorByGroupName(groupName, year);
            if (salesIndicatorDOList.size() == 0 || salesIndicatorDOList == null) {
                log.warn("请先添加大组信息");
                return ;
            }
            //根据店铺和区域查询对应的数据
            IN:
            for (SalesIndicatorDO salesIndicatorDO : salesIndicatorDOList) {
                String sellerId = salesIndicatorDO.getSellerId();
                String areaCode = salesIndicatorDO.getArea();
                Shop shop = shopMapper.getOneInfoBySellerId(sellerId);
                String shopName = shop.getShopName();
                String salesChannel = AreaCastUtil.castToSalesChannel(areaCode);
                //去数据库查询该店铺该区域在今年的数据
                ShopSalesStatisticsDataVO shopSalesStatisticsDataVO = salesStatisticsReportMapper.getSalesAmountFromExistingData(sellerId, areaCode, year);
                //如果表中还没有今年的数据，则直接去原始的all_order表查数据
                if (shopSalesStatisticsDataVO == null) {
                    shopSalesStatisticsDataVO = new ShopSalesStatisticsDataVO();
                    List<SalesAmountDO> salesAmountDOList = salesStatisticsReportMapper.getSalesAmountByYear(sellerId, areaCode, salesChannel, year);
                    shopSalesStatisticsDataVO.setShopName(shopName);
                    shopSalesStatisticsDataVO.setSellerId(sellerId);
                    shopSalesStatisticsDataVO.setAreaCode(areaCode);
                    shopSalesStatisticsDataVO.setYear(year);
//                    shopSalesStatisticsDataVO.setAvailableStocksSoldOutTime(availableStocksSoldOutTime);
//                    shopSalesStatisticsDataVO.setAllStocksSoldOutTime(allStocksSoldOutTime);
                    //将汇率计算上
                    BigDecimal exchangeRate = null;
                    RATE:
                    for (ExchangeRateDO exchangeRateDO : exchangeRateDOList) {
                        if (areaCode.equals(exchangeRateDO.getCode())) {
                            exchangeRate = exchangeRateDO.getClosePri();
                            break RATE;
                        }
                    }
                    if (null == exchangeRate) {
                        log.warn("目前没有关于" + areaCode + "的汇率，请先添加对应汇率！");
                        return ;
                    }
                    //把查到的各个月的数据存入shopSalesStatisticsDataVO中
                    for (SalesAmountDO salesAmountDO : salesAmountDOList) {
                        salesAmountDO.setSumSalesAmount(salesAmountDO.getSumSalesAmount().multiply(exchangeRate));
                        switch (salesAmountDO.getMonth()) {
                            case "1":
                                shopSalesStatisticsDataVO.setJanuarySales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "2":
                                shopSalesStatisticsDataVO.setFebruarySales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "3":
                                shopSalesStatisticsDataVO.setMarchSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "4":
                                shopSalesStatisticsDataVO.setAprilSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "5":
                                shopSalesStatisticsDataVO.setMaySales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "6":
                                shopSalesStatisticsDataVO.setJuneSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "7":
                                shopSalesStatisticsDataVO.setJulySales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "8":
                                shopSalesStatisticsDataVO.setAugustSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "9":
                                shopSalesStatisticsDataVO.setSeptemberSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "10":
                                shopSalesStatisticsDataVO.setOctoberSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "11":
                                shopSalesStatisticsDataVO.setNovemberSales(salesAmountDO.getSumSalesAmount());
                                break;
                            case "12":
                                shopSalesStatisticsDataVO.setDecemberSales(salesAmountDO.getSumSalesAmount());
                                break;
                        }
                    }
                    BigDecimal totalAnnualSales = shopSalesStatisticsDataVO.getJanuarySales().add(shopSalesStatisticsDataVO.getFebruarySales()).add(shopSalesStatisticsDataVO.getMarchSales()).add(shopSalesStatisticsDataVO.getAprilSales()).add(shopSalesStatisticsDataVO.getMaySales()).add(shopSalesStatisticsDataVO.getJuneSales()).add(shopSalesStatisticsDataVO.getJulySales()).add(shopSalesStatisticsDataVO.getAugustSales()).add(shopSalesStatisticsDataVO.getSeptemberSales()).add(shopSalesStatisticsDataVO.getOctoberSales()).add(shopSalesStatisticsDataVO.getNovemberSales()).add(shopSalesStatisticsDataVO.getDecemberSales());
                    shopSalesStatisticsDataVO.setTotalAnnualSales(totalAnnualSales);
                    //把目前的数据存入数据库里
                    salesStatisticsReportMapper.insertBaseData(shopSalesStatisticsDataVO);
                    log.info("--------------------" + "店铺：" + shopName + ",区域：" + areaCode + "的" + year + "年的销售额更新完成" + "--------------------");
                    continue IN;
                }
                //如果表中有今年的数据，那么更新最近这个月的数据，封装
                String date = String.valueOf(year) + String.valueOf(month);
                SalesAmountDO salesAmountDO = salesStatisticsReportMapper.getSalesAmountByMonth(sellerId, areaCode, salesChannel, date);
                //将汇率计算上
                BigDecimal exchangeRate = null;
                RATE:
                for (ExchangeRateDO exchangeRateDO : exchangeRateDOList) {
                    if (areaCode.equals(exchangeRateDO.getCode())) {
                        exchangeRate = exchangeRateDO.getClosePri();
                        break RATE;
                    }
                }
                if (null == exchangeRate) {
                    log.warn("目前没有关于" + areaCode + "的汇率，请先添加对应汇率！");
                    return ;
                }
                if (salesAmountDO != null) {
                    salesAmountDO.setSumSalesAmount(salesAmountDO.getSumSalesAmount().multiply(exchangeRate));
                    switch (salesAmountDO.getMonth()) {
                        case "1":
                            shopSalesStatisticsDataVO.setJanuarySales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "2":
                            shopSalesStatisticsDataVO.setFebruarySales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "3":
                            shopSalesStatisticsDataVO.setMarchSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "4":
                            shopSalesStatisticsDataVO.setAprilSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "5":
                            shopSalesStatisticsDataVO.setMaySales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "6":
                            shopSalesStatisticsDataVO.setJuneSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "7":
                            shopSalesStatisticsDataVO.setJulySales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "8":
                            shopSalesStatisticsDataVO.setAugustSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "9":
                            shopSalesStatisticsDataVO.setSeptemberSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "10":
                            shopSalesStatisticsDataVO.setOctoberSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "11":
                            shopSalesStatisticsDataVO.setNovemberSales(salesAmountDO.getSumSalesAmount());
                            break;
                        case "12":
                            shopSalesStatisticsDataVO.setDecemberSales(salesAmountDO.getSumSalesAmount());
                            break;
                    }
                }
                BigDecimal totalAnnualSales = shopSalesStatisticsDataVO.getJanuarySales().add(shopSalesStatisticsDataVO.getFebruarySales()).add(shopSalesStatisticsDataVO.getMarchSales()).add(shopSalesStatisticsDataVO.getAprilSales()).add(shopSalesStatisticsDataVO.getMaySales()).add(shopSalesStatisticsDataVO.getJuneSales()).add(shopSalesStatisticsDataVO.getJulySales()).add(shopSalesStatisticsDataVO.getAugustSales()).add(shopSalesStatisticsDataVO.getSeptemberSales()).add(shopSalesStatisticsDataVO.getOctoberSales()).add(shopSalesStatisticsDataVO.getNovemberSales()).add(shopSalesStatisticsDataVO.getDecemberSales());
                shopSalesStatisticsDataVO.setTotalAnnualSales(totalAnnualSales);
                //把目前的数据存入数据库里
                salesStatisticsReportMapper.updateBaseData(shopSalesStatisticsDataVO);
                log.info("--------------------" + "店铺：" + shopName + ",区域：" + areaCode + "的" + year + "年的销售额更新完成" + "--------------------");
            }
        }
        log.info("--------------------" + year + "年的销售额更新完成" + "--------------------");
    }
}
