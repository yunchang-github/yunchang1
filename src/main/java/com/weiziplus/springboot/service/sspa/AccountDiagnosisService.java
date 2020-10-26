package com.weiziplus.springboot.service.sspa;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.DetailPageSalesAndTrafficByChildItemsMapper;
import com.weiziplus.springboot.mapper.originalAdvertData.OriginalDataAdvCampaignsReportMapper;
import com.weiziplus.springboot.mapper.originalAdvertData.OriginalDataAdvProductadsReportMapper;
import com.weiziplus.springboot.mapper.sspa.*;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.service.datadictionary.DataDictionaryService;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/7/1 16:09
 */
@Service
public class AccountDiagnosisService extends BaseService {

    @Autowired
    SponsoredProductsPlacementReportMapper sponsoredProductsPlacementReportMapper;

    @Autowired
    SponsoredProductsPerformanceOverTimeReportMapper sponsoredProductsPerformanceOverTimeReportMapper;

    @Autowired
    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;

    @Autowired
    SalesAndTrafficMapper salesAndTrafficMapper;

    @Autowired
    DataDictionaryService dataDictionaryService;

    @Autowired
    OriginalDataAdvCampaignsReportMapper originalDataAdvCampaignsReportMapper;

    @Autowired
    OriginalDataAdvProductadsReportMapper originalDataAdvProductadsReportMapper;

    @Autowired
    DetailPageSalesAndTrafficByChildItemsMapper detailPageSalesAndTrafficByChildItemsMapper;
    @Autowired
    AccountDiagnosisMapper accountDiagnosisMapper;

    /**
     * 账户总体数据
     * 获取广告接口campains表中的clicks（总点击量）、impressions（总展现量）、cost（总花费）、attributedSales1d（总广告销售额）,attributedConversions1d（总销售数量）
     * 获取detail_page_sales_and_traffic_by_child_items表中的（ordered_item_sales+ordered_item_sales_B2B）的和（账户销售额），buyer_visits
     * 目前需求不需要根据具体时间显示对应的数据，直接显示该年的每月数据
     *
     * @return
     */
    public ResultUtil accountOverall(String shop, String area) {
        Map<String, Object> result = new HashMap<>(2);
        int year = DateUtil.getYear();
        //获取广告接口campains表中的clicks、impressions、cost、attributedSales1d、attributedConversions1d在一年的总和，
        // detail_page_sales_and_traffic_by_child_items表中的ordered_item_sales+ordered_item_sales_B2B两个字段在一年的总和，buyer_visits的和
        LocalDate today = LocalDate.now();
        String startDate = today.minusMonths(1).toString();
        String endDate = today.minusMonths(12).toString();
        Integer clicksSum = originalDataAdvCampaignsReportMapper.getClicksSumByDate(startDate,endDate, shop, area);
        Integer impressionsSum = originalDataAdvCampaignsReportMapper.getImpressionsSumByDate(startDate,endDate, shop, area);
        BigDecimal costSum = originalDataAdvCampaignsReportMapper.getCostSumByDate(startDate,endDate, shop, area);
        BigDecimal advertSalesSum = originalDataAdvCampaignsReportMapper.getAttributedSales1dSumByDate(startDate,endDate, shop, area);
        Integer totalSalesSum = originalDataAdvCampaignsReportMapper.getAttributedConversions1dSumByDate(startDate,endDate, shop, area);
        BigDecimal accountSalesSum =  detailPageSalesAndTrafficByChildItemsMapper.getAllOrderedItemSalesByDate(startDate,endDate, shop, area);
        Integer buyerVisitsSum = detailPageSalesAndTrafficByChildItemsMapper.getBuyerVisitsByDate(startDate,endDate, shop, area);
        result.put("all", handleAccountOverallRowMap(impressionsSum, clicksSum, totalSalesSum, costSum, advertSalesSum, accountSalesSum, buyerVisitsSum));
        List<Map<String, Object>> monthsResult = new ArrayList<>(12);
        String monthDate = "";
        for (int i = 1; i <= 12; i++) {
            LocalDate localDate = today.minusMonths(i);
            monthDate = localDate.toString();
            Integer monthClicksSum = originalDataAdvCampaignsReportMapper.getClicksSumByDate(monthDate,monthDate, shop, area);
            Integer monthImpressionsSum = originalDataAdvCampaignsReportMapper.getImpressionsSumByDate(monthDate,monthDate, shop, area);
            BigDecimal monthCostSum = originalDataAdvCampaignsReportMapper.getCostSumByDate(monthDate,monthDate, shop, area);
            BigDecimal monthAdvertSalesSum = originalDataAdvCampaignsReportMapper.getAttributedSales1dSumByDate(monthDate,monthDate, shop, area);
            Integer monthTotalSalesSum = originalDataAdvCampaignsReportMapper.getAttributedConversions1dSumByDate(monthDate,monthDate, shop, area);
            BigDecimal monthAccountSalesSum = detailPageSalesAndTrafficByChildItemsMapper.getAllOrderedItemSalesByDate(monthDate,monthDate, shop, area);
            Integer monthBuyerVisitsSum = detailPageSalesAndTrafficByChildItemsMapper.getBuyerVisitsByDate(monthDate,monthDate, shop, area);
            Map<String, Object> monthMap = handleAccountOverallRowMap(monthImpressionsSum, monthClicksSum, monthTotalSalesSum, monthCostSum, monthAdvertSalesSum, monthAccountSalesSum, monthBuyerVisitsSum);
            monthMap.put("month", monthDate.substring(0, 7));
            monthsResult.add(monthMap);
        }
        result.put("months",monthsResult);
        return ResultUtil.success(result);
    }

    /**
     * 账户总体数据处理每一行显示
     * @param impressionsSum
     * @param clicksSum
     * @param totalSalesSum
     * @param costSum
     * @param advertSalesSum
     * @param accountSalesSum
     * @param buyerVisitsSum
     * @return
     */
    private Map<String, Object> handleAccountOverallRowMap(Integer impressionsSum, Integer clicksSum, Integer totalSalesSum, BigDecimal costSum, BigDecimal advertSalesSum, BigDecimal accountSalesSum, Integer buyerVisitsSum) {
        Map<String, Object> map = new HashMap<>(15);
        DecimalFormat df = new DecimalFormat("0.00%");
        if (null == impressionsSum) {
            map.put("impressions", "-");
        } else {
            map.put("impressions", impressionsSum);
        }
        if (null == clicksSum) {
            map.put("clicks", "-");
        } else {
            map.put("clicks", clicksSum);
        }
        if (null == totalSalesSum) {
            map.put("totalSales", "-");
        } else {
            map.put("totalSales", totalSalesSum);
        }
        if (null == costSum) {
            map.put("cost", "-");
        } else {
            map.put("cost", costSum);
        }
        if (null == advertSalesSum) {
            map.put("advertSales", "-");
        } else {
            map.put("advertSales", advertSalesSum);
        }
        if (null == costSum || (null == clicksSum || 0 == clicksSum)) {
            map.put("CPC", "-");
        } else {
            map.put("CPC", costSum.divide(new BigDecimal(clicksSum), 4));
        }
        if (null == clicksSum || (null == impressionsSum || 0 == impressionsSum)) {
            map.put("CTR", "-");
        } else {
            map.put("CTR", df.format(1.00 * clicksSum / impressionsSum));
        }
        if (null == totalSalesSum || (null == clicksSum || 0 == clicksSum)) {
            map.put("CR", "-");
        } else {
            map.put("CR", df.format(1.00 * totalSalesSum / clicksSum));
        }
        if (null == costSum || (null == advertSalesSum || advertSalesSum.compareTo(new BigDecimal(0)) == 0)) {
            map.put("ACoS", "-");
        } else {
            map.put("ACoS", df.format(costSum.divide(advertSalesSum, 4, BigDecimal.ROUND_HALF_UP)));
        }
        if (null == accountSalesSum) {
            map.put("accountSales", "-");
        } else {
            map.put("accountSales", accountSalesSum);
        }
        if (null == costSum || (null == accountSalesSum || accountSalesSum.compareTo(new BigDecimal(0)) == 0)) {
            map.put("广告花费/账户销售额", "-");
        } else {
            map.put("广告花费/账户销售额", df.format(costSum.divide(accountSalesSum, 4, 4)));
        }
        if (null == advertSalesSum || (null == accountSalesSum || accountSalesSum.compareTo(new BigDecimal(0)) == 0)) {
            map.put("广告销售额/账户销售额", "-");
        } else {
            map.put("广告销售额/账户销售额", df.format(advertSalesSum.divide(accountSalesSum, 4, 4)));
        }
        if (null == buyerVisitsSum) {
            map.put("buyerVisits", "-");
        } else {
            map.put("buyerVisits", buyerVisitsSum);
        }
        if (null == clicksSum || (null == buyerVisitsSum || 0 == buyerVisitsSum)) {
            map.put("Clicks/访问次数", "-");
        } else {
            map.put("Clicks/访问次数", df.format(1.00 * clicksSum / buyerVisitsSum));
        }
        return map;
    }

    /**
     * 过去三个月TOP 10 ASIN
     *
     * @return
     */
    public ResultUtil pastThreeMonths(String shop, String area) {

        return ResultUtil.success(accountDiagnosisMapper.getTopTenAsinThreeMonth(shop,area));
    }

    /**
     * 账户广告活动诊断
     *目前该接口没用---sjd
     *
     * @return
     */
    public ResultUtil accountCampaignDiagnostics(String date, String shop, String area) {
        Map<String, Object> result = new HashMap<>(4);
        String year = String.valueOf(DateUtil.getYear());
        Integer clicksSum = 0;
        Integer impressionsSum = 0;
        BigDecimal costSum = new BigDecimal(0);
        BigDecimal advertSalesSum = new BigDecimal(0);
        BigDecimal accountSalesSum = new BigDecimal(0);
        //获取广告接口campains表中的clicks、impressions、cost、attributedSales1d在指定一个月或最近三个月的总和，
        // detail_page_sales_and_traffic_by_child_items表中的ordered_item_sales+ordered_item_sales_B2B两个字段在指定一个月或最近三个月的总和


        return ResultUtil.success(result);
    }

    /**
     * 账户核心广告数据过去三个月变化趋势
     *这个接口没用---sjd
     * @return
     */
    public ResultUtil accountCoreAdvertisingDataTrendsThreeMonths() {
        Map<String, Object> result = new HashMap<>(3);
        int year = DateUtil.getYear();
        int month = DateUtil.getMonth();
        List<Integer> monthList = new ArrayList<>(month);
        List<Double> spendList = new ArrayList<>(month);
        List<Double> salesList = new ArrayList<>(month);
        for (int i = 1; i <= month; i++) {
            monthList.add(i);
            SponsoredProductsPlacementReport sponsoredProductsPlacementReportData = sponsoredProductsPlacementReportMapper.getListDataByYearAndMonth(String.valueOf(year), i > 9 ? String.valueOf(i) : "0" + i);
            SalesAndTraffic salesAndTrafficData = salesAndTrafficMapper.getListDataByYearAndMonth(String.valueOf(year), i > 9 ? String.valueOf(i) : "0" + i);
            if (null == sponsoredProductsPlacementReportData || null == salesAndTrafficData) {
                spendList.add(0.0);
                salesList.add(0.0);
                continue;
            }
            Double spend = sponsoredProductsPlacementReportData.getSpend();
            Double sales = salesAndTrafficData.getOrderedProductSales();
            Double b2b = salesAndTrafficData.getOrderedProductSalesB2b();
            if (null == spend || null == sales || null == b2b || 0 == sales + b2b) {
                spendList.add(0.0);
                salesList.add(0.0);
                continue;
            }
            spendList.add(ToolUtil.doubleKeepDecimal(spend / (sales + b2b), 4));
            Double sevenDayTotalSales = sponsoredProductsPlacementReportData.getSevenDayTotalSales();
            if (null == sevenDayTotalSales) {
                salesList.add(0.00);
            } else {
                salesList.add(ToolUtil.doubleKeepDecimal(sevenDayTotalSales / (sales + b2b), 4));
            }
        }
        result.put("month", monthList);
        result.put("spend", spendList);
        result.put("sales", salesList);


        return ResultUtil.success(result);
    }

    /**
     * 广告clicks/访问次数
     *这个接口没用---sjd
     * @return
     */
    public ResultUtil adClicksVisits() {
        Map<String, Object> result = new HashMap<>(3);
        int year = DateUtil.getYear();
        int month = DateUtil.getMonth();
        List<Integer> monthList = new ArrayList<>(month);
        List<Integer> sessionsList = new ArrayList<>(month);
        List<Double> clicksSessionsList = new ArrayList<>(month);
        for (int i = 1; i <= month; i++) {
            monthList.add(i);
            SalesAndTraffic salesAndTrafficData = salesAndTrafficMapper.getListDataByYearAndMonth(String.valueOf(year), i > 9 ? String.valueOf(i) : "0" + i);
            SponsoredProductsPerformanceOverTimeReport sponsoredProductsPerformanceOverTimeReportData = sponsoredProductsPerformanceOverTimeReportMapper.getListDataByYearAndMonth(String.valueOf(year), i > 9 ? String.valueOf(i) : "0" + i);
            if (null == salesAndTrafficData || null == sponsoredProductsPerformanceOverTimeReportData) {
                sessionsList.add(0);
                clicksSessionsList.add(0.0);
                continue;
            }
            Integer sessions = salesAndTrafficData.getSessions();
            sessionsList.add(sessions);
            Integer clicks = sponsoredProductsPerformanceOverTimeReportData.getClicks();
            if (null == sessions || null == clicks || 0 == sessions) {
                clicksSessionsList.add(0.0);
            } else {
                clicksSessionsList.add(ToolUtil.doubleKeepDecimal(1.00 * clicks / sessions));
            }
        }
        result.put("month", monthList);
        result.put("sessions", sessionsList);
        result.put("clicksSessions", clicksSessionsList);
        return ResultUtil.success(result);
    }

    /**
     * 展现量
     *这个接口要---sjd
     * @return
     */
    public ResultUtil impressions(String shop,String area) {
        List list = originalDataAdvCampaignsReportMapper.getImpressionsListByLatestMonth(shop,area);
        return ResultUtil.success(list);
    }
}
