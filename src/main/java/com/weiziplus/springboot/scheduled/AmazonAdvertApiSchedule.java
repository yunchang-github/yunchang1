package com.weiziplus.springboot.scheduled;

import com.alibaba.druid.util.StringUtils;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsPlacementReportMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsSearchTermReportMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.utils.CryptoUtil;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import com.weiziplus.springboot.utils.amazon.AmazonAdvertApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/8/19 16:26
 */
@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class AmazonAdvertApiSchedule extends BaseService {
//
//    @Autowired
//    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;
//
//    @Autowired
//    SponsoredProductsPlacementReportMapper sponsoredProductsPlacementReportMapper;
//
//    @Autowired
//    SponsoredProductsSearchTermReportMapper sponsoredProductsSearchTermReportMapper;
//
//    @Autowired
//    ShopMapper shopMapper;
//
//    /**
//     * 错误重试次数
//     */
//    private final Integer MAX_ERROR_NUM = 5;
//
//    /**
//     * 当前网店名字
//     */
//    // private final String SHOP_NAME = GlobalConfig.SHOP_NAME;
//
//    /**
//     * 亚马逊广告api定时任务
//     * 每周二上午8:00获取上上周日至上周六的报告（Report period选择Last week 默认时间范围为上上周六至上周日）
//     */
////    @Scheduled(cron = "0 0 8 ? * 2")
//    @Transactional(rollbackFor = Exception.class)
//    public void sponsoredProductsAdvertisedProductReport() {
//        log.info("***********亚马逊广告api定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                int maxErrorNum = 5;
//                boolean tokenFlag = false;
//                for (int i = 0; i < maxErrorNum; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < maxErrorNum; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api定时任务结束**********");
//                return;
//            }
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("reportDate", DateUtil.getFetureDate(-3).replace("-", ""));
//                put("metrics", "currency,campaignName,adGroupName,sku,asin,impressions,clicks,cost,attributedSales7d,attributedUnitsOrdered7d" +
//                        ",attributedUnitsOrdered7dSameSKU,attributedSales7dSameSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);
//                String profileId = String.valueOf(profilesMap.get("profileId"));
//                String countryCode = String.valueOf(profilesMap.get("countryCode"));
//                ShopAreaProfile shopAreaProfile =
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "productAds", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理Sponsored Products Advertised product report和Sponsored Products Performance Over Time report
//                        String date = DateUtil.getFutureDateTime(-2);
//                        String sponsoredProductsAdvertisedProductReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDay(shop.getShopName(), countryCode);
//                        if (null == sponsoredProductsAdvertisedProductReportLatestDay
//                                || DateUtil.compateTime(sponsoredProductsAdvertisedProductReportLatestDay, date) < 0) {
//                            handleSponsoredProductsAdvertisedProductReport(lists, shop.getShopName(), countryCode, date);
//                        }
//
//                        String currency = String.valueOf(lists.get(0).get("currency"));
//                        String sponsoredProductsPlacementReportLatestDay = sponsoredProductsPlacementReportMapper.getLatestDay(shop.getShopName(), countryCode);
//                        if (null == sponsoredProductsPlacementReportLatestDay
//                                || DateUtil.compateTime(sponsoredProductsPlacementReportLatestDay, date) < 0) {
//                            boolean flag = handleSponsoredProductsPlacementReport(profileId, shop, currency, countryCode);
//
//                            if (!flag) {
//                                continue;
//                            }
//                        }
//                        String sponsoredProductsSearchTermReportLatestDay = sponsoredProductsSearchTermReportMapper.getLatestDay(shop.getShopName(), countryCode);
//                        if (null == sponsoredProductsSearchTermReportLatestDay
//                                || DateUtil.compateTime(sponsoredProductsSearchTermReportLatestDay, date) < 0) {
//                            boolean flag = handleSponsoredProductsSearchTermReport(profileId, shop, currency, countryCode);
//                    /*    int ernum3 = 0;
//                        while(!flag){
//                            if(ernum3>=6){
//                                break;
//                            }
//                            flag = handleSponsoredProductsSearchTermReport(profileId,shop, currency, countryCode);
//                            ernum3++;
//                        }*/
//                            if (!flag) {
//                                continue;
//                            }
//                        }
//                        break;
//                    /*//如果当前是最后一个
//                    if (index == profiles.size() - 1) {
//                        break;
//                    }
//                    log.info("***********亚马逊广告api定时任务结束**********");
//                    return;*/
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//            logWarnErrorMsg(String.valueOf(errorMsg), shop.getShopName());
//        }
//        log.info("***********亚马逊广告api定时任务结束**********");
//    }
//
//    /**
//     * 处理Sponsored Products Advertised product report和Sponsored Products Performance Over Time report
//     *
//     * @param lists
//     * @param countryCode
//     * @param date
//     * @throws Exception
//     */
//    public void handleSponsoredProductsAdvertisedProductReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        int clicksSum = 0;
//        double spendSum = 0.00;
//        List<Object> list = new ArrayList<>(lists.size());
//        SponsoredProductsAdvertisedProductReport report = null;
//        for (Map<String, Object> map : lists) {
//            Integer clicks = Integer.valueOf(String.valueOf(map.get("clicks")));
//            clicksSum += clicks;
//            Double spend = ToolUtil.valueOfDouble(String.valueOf(map.get("cost")));
//            spendSum += spend;
//            report = new SponsoredProductsAdvertisedProductReport();
//            report.setShop(shopName);
//            report.setArea(countryCode);
//            report.setDate(date);
//            report.setCurrency(String.valueOf(map.get("currency")));
//            report.setCampaignName(String.valueOf(map.get("campaignName")));
//            report.setAdGroupName(String.valueOf(map.get("adGroupName")));
//            report.setAdvertisedSku(String.valueOf(map.get("sku")));
//            report.setAdvertisedAsin(String.valueOf(map.get("asin")));
//            report.setImpressions(ToolUtil.valueOfInteger(String.valueOf(map.get("impressions"))));
//            report.setClicks(clicks);
//            report.setSpend(spend);
//            Double sevenDayTotalSalesDouble = ToolUtil.valueOfDouble(String.valueOf(map.get("attributedSales7d")));
//            report.setSevenDayTotalSales(sevenDayTotalSalesDouble);
//            Integer sevenDayTotalUnitsInteger = ToolUtil.valueOfInteger(String.valueOf(map.get("attributedUnitsOrdered7d")));
//            report.setSevenDayTotalUnits(sevenDayTotalUnitsInteger);
//            if (0 < clicks) {
//                report.setSevenDayConversionRate(String.valueOf(sevenDayTotalUnitsInteger / clicks));
//            }
//            Integer sevenDayAdvertisedSkuUnitsInteger = ToolUtil.valueOfInteger(String.valueOf(map.get("attributedUnitsOrdered7dSameSKU")));
//            report.setSevenDayAdvertisedSkuUnits(sevenDayAdvertisedSkuUnitsInteger);
//            report.setSevenDayOtherSkuUnits(sevenDayTotalUnitsInteger - sevenDayAdvertisedSkuUnitsInteger);
//            Double sevenDayAdvertisedSkuSalesDouble = ToolUtil.valueOfDouble(String.valueOf(map.get("attributedSales7dSameSKU")));
//            report.setSevenDayAdvertisedSkuSales(sevenDayAdvertisedSkuSalesDouble);
//            report.setSevenDayOtherSkuSales(sevenDayTotalSalesDouble - sevenDayAdvertisedSkuSalesDouble);
//            list.add(report);
//        }
//        baseInsertList(list);
//        SponsoredProductsPerformanceOverTimeReport report1 = new SponsoredProductsPerformanceOverTimeReport();
//        report1.setShop(shopName);
//        report1.setArea(countryCode);
//        report1.setDate(date);
//        report1.setClicks(clicksSum);
//        report1.setSpend(spendSum);
//        if (0 < clicksSum) {
//            report1.setCpc(spendSum / clicksSum);
//        }
//        baseInsert(report1);
//    }
//
//    /**
//     * 处理Sponsored Products Placement report
//     *
//     * @param profileId
//     * @param currency
//     * @param countryCode
//     * @return
//     * @throws Exception
//     */
//    public boolean handleSponsoredProductsPlacementReport(String profileId, Shop shop, String currency, String countryCode) throws Exception {
//        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "campaigns", new HashMap<String, Object>(2) {{
//            put("segment", "placement");
//            put("reportDate", DateUtil.getFetureDate(-3).replace("-", ""));
//            put("metrics", "campaignName,campaignId,impressions,clicks,cost,attributedSales7d,attributedUnitsOrdered7d");
//        }});
//        if (null == lists || 0 >= lists.size()) {
//            return false;
//        }
//        String date = DateUtil.getFutureDateTime(-3);
//        SponsoredProductsPlacementReport report = null;
//        List<Object> list = new ArrayList<>(lists.size());
//        for (Map<String, Object> map : lists) {
//            report = new SponsoredProductsPlacementReport();
//            report.setShop(shop.getShopName());
//            report.setArea(countryCode);
//            report.setDate(date);
//            report.setCurrency(currency);
//            report.setPlacement(String.valueOf(map.get("placement")));
//            report.setCampaignName(String.valueOf(map.get("campaignName")));
//            Long campaignId = ToolUtil.valueOfLong(String.valueOf(map.get("campaignId")));
//            Object biddingStrategyByCampaignId = AmazonAdvertApiUtil.getBiddingStrategyByCampaignId(profileId, CryptoUtil.decode(shop.getSellerId()), campaignId);
//            if (null != biddingStrategyByCampaignId) {
//                report.setBiddingStrategy(String.valueOf(biddingStrategyByCampaignId));
//            }
//            report.setCampaignId(campaignId);
//            report.setImpressions(ToolUtil.valueOfInteger(String.valueOf(map.get("impressions"))));
//            report.setClicks(ToolUtil.valueOfInteger(String.valueOf(map.get("clicks"))));
//            report.setSpend(ToolUtil.valueOfDouble(String.valueOf(map.get("cost"))));
//            report.setSevenDayTotalSales(ToolUtil.valueOfDouble(String.valueOf(map.get("attributedSales7d"))));
//            report.setSevenDayTotalUnits(ToolUtil.valueOfInteger(String.valueOf(map.get("attributedUnitsOrdered7d"))));
//            list.add(report);
//        }
//        baseInsertList(list);
//        return true;
//    }
//
//    /**
//     * 处理Sponsored Products Search term report
//     *
//     * @param profileId
//     * @param currency
//     * @param countryCode
//     * @return
//     * @throws Exception
//     */
//    public boolean handleSponsoredProductsSearchTermReport(String profileId, Shop shop, String currency, String countryCode) throws Exception {
//        //获取keywords---根据Date，Campaign Name，Ad Group Name，Targeting（在keywords（赞助商品）中对应的为keywordText）查找keywords（赞助商品）中的matchType
//        List<Map<String, Object>> keywordsList = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "keywords", new HashMap<String, Object>(2) {{
//            put("reportDate", DateUtil.getFetureDate(-3).replace("-", ""));
//            put("metrics", "campaignName,adGroupName,keywordText,matchType");
//        }});
//        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "targets", new HashMap<String, Object>(2) {{
//            put("segment", "query");
//            put("reportDate", DateUtil.getFetureDate(-3).replace("-", ""));
//            put("metrics", "campaignName,adGroupName,targetingText,impressions,clicks,cost,attributedSales7d,attributedUnitsOrdered7d,attributedUnitsOrdered7dSameSKU,attributedSales7dSameSKU");
//        }});
//        if (null == lists || 0 >= lists.size()) {
//            return false;
//        }
//        List<Object> list = new ArrayList<>(lists.size());
//        String date = DateUtil.getFutureDateTime(-3);
//        SponsoredProductsSearchTermReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new SponsoredProductsSearchTermReport();
//            report.setShop(shop.getShopName());
//            report.setArea(countryCode);
//            report.setDate(date);
//            report.setCurrency(currency);
//            report.setCampaignName(String.valueOf(map.get("campaignName")));
//            report.setAdGroupName(String.valueOf(map.get("adGroupName")));
//            report.setTargeting(String.valueOf(map.get("targetingText")));
//            if (null != keywordsList) {
//                for (Map<String, Object> map1 : keywordsList) {
//                    if (!String.valueOf(map.get("campaignName")).equals(String.valueOf(map1.get("campaignName")))) {
//                        continue;
//                    }
//                    if (!String.valueOf(map.get("adGroupName")).equals(String.valueOf(map1.get("adGroupName")))) {
//                        continue;
//                    }
//                    if (!String.valueOf(map.get("targetingText")).equals(String.valueOf(map1.get("keywordText")))) {
//                        continue;
//                    }
//                    report.setMatchType(String.valueOf(map1.get("matchType")));
//                    break;
//                }
//            }
//            report.setCustomerSearchTerm(String.valueOf(map.get("query")));
//            report.setImpressions(ToolUtil.valueOfInteger(String.valueOf(map.get("impressions"))));
//            Integer clicksInteger = ToolUtil.valueOfInteger(String.valueOf(map.get("clicks")));
//            report.setClicks(clicksInteger);
//            report.setSpend(ToolUtil.valueOfDouble(String.valueOf(map.get("cost"))));
//            Double sevenDayTotalSalesDouble = ToolUtil.valueOfDouble(String.valueOf(map.get("attributedSales7d")));
//            report.setSevenDayTotalSales(sevenDayTotalSalesDouble);
//            Integer sevenDayTotalUnitsInteger = ToolUtil.valueOfInteger(String.valueOf(map.get("attributedUnitsOrdered7d")));
//            report.setSevenDayTotalUnits(sevenDayTotalUnitsInteger);
//            if (0 < clicksInteger) {
//                report.setSevenDayConversionRate(String.valueOf(sevenDayTotalUnitsInteger / clicksInteger));
//            }
//            Integer sevenDayAdvertisedSkuUnitsInteger = ToolUtil.valueOfInteger(String.valueOf(map.get("attributedUnitsOrdered7dSameSKU")));
//            report.setSevenDayAdvertisedSkuUnits(sevenDayAdvertisedSkuUnitsInteger);
//            report.setSevenDayOtherSkuUnits(sevenDayTotalUnitsInteger - sevenDayAdvertisedSkuUnitsInteger);
//            Double sevenDayAdvertisedSkuSalesDouble = ToolUtil.valueOfDouble(String.valueOf(map.get("attributedSales7dSameSKU")));
//            report.setSevenDayAdvertisedSkuSales(sevenDayAdvertisedSkuSalesDouble);
//            report.setSevenDayOtherSkuSales(sevenDayTotalSalesDouble - sevenDayAdvertisedSkuSalesDouble);
//            list.add(report);
//        }
//        baseInsertList(list);
//        return true;
//    }
//
//    /**
//     * 打印出错信息
//     *
//     * @param errorMsg
//     */
//    private void logWarnErrorMsg(String errorMsg, String shopName) {
//        log.warn("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓定时任务出错↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
//        log.warn("×××××亚马逊广告api×××定时任务出错×××" +
//                "×××××错误详情:" + errorMsg);
//        log.warn("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑定时任务出错↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
//        DataGetErrorRecord record = new DataGetErrorRecord();
//        record.setShop(shopName);
//        record.setArea("所有旗下国家代码");
//        String date = DateUtil.getFetureDate(0) + " 08:00:00";
//        record.setDate(date);
//        record.setName("亚马逊广告api定时任务");
//        //是否处理，0:未处理,1:已处理',
//        record.setIsHandle(0);
//        //类型:亚马逊广告api定时任务
//        record.setType(2);
//        record.setRemark("亚马逊广告api定时任务出错,任务名称:"
//                + "出错的时间:" + date
//                + "错误详情:" + errorMsg);
//        record.setCreateTime(DateUtil.getFutureDateTime(0));
//        baseInsert(record);
//    }
//
//
//    /**
//     * 获取recordType：productAds；的广告api报告数据
//     */
////    @Scheduled(cron = "0 10 3 ? * *")
////    @Scheduled(cron = "0 0 13 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void addOriginalDataAdvProductadsReport() {
//        String USDate = DateUtil.getAdvReportTime();
//        String reportDate =  USDate.replace("-", "");
//        log.info("***********亚马逊广告api--productAds定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                boolean tokenFlag = false;
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api--productAds定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--productAds定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api--productAds获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--productAds定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api--productAds定时任务结束**********");
//                return;
//            }
//
//            //获取原始数据
//
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("reportDate",reportDate);//时间格式YYYYMMDD
//                put("metrics", "campaignName,campaignId,adGroupName,adGroupId,impressions,clicks,cost,currency,sku,asin,attributedConversions1d,attributedConversions7d" +
//                        ",attributedConversions14d,attributedConversions30d,attributedConversions1dSameSKU,attributedConversions7dSameSKU,attributedConversions14dSameSKU" +
//                        ",attributedConversions30dSameSKU,attributedUnitsOrdered1d,attributedUnitsOrdered7d,attributedUnitsOrdered14d,attributedUnitsOrdered30d,attributedSales1d" +
//                        ",attributedSales7d,attributedSales14d,attributedSales30d,attributedSales1dSameSKU,attributedSales7dSameSKU,attributedSales14dSameSKU,attributedSales30dSameSKU,attributedUnitsOrdered1dSameSKU,attributedUnitsOrdered7dSameSKU,attributedUnitsOrdered14dSameSKU,attributedUnitsOrdered30dSameSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);   //一个店铺下每个区域
//                String profileId = MapUtils.getString(profilesMap, "profileId");
//                String countryCode = MapUtils.getString(profilesMap, "countryCode");
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "productAds", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum+1).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理
//                        String originalDataAdvProductadsReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDayFromOriginalProductAds(shop.getShopName(), countryCode);
//                        if (null == originalDataAdvProductadsReportLatestDay
//                                || DateUtil.compateTime(originalDataAdvProductadsReportLatestDay, USDate) < 0) {
//                            handleOriginalDataAdvProductadsReport(lists, shop.getShopName(), countryCode, USDate);
//                        }
//                        break;
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum+1).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        }
//        log.info("***********亚马逊广告api--productAds定时任务结束**********");
//
//    }
//
//    public void handleOriginalDataAdvProductadsReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        String nowDate = DateUtil.getDate();
//        List<Object> list = new ArrayList<>(lists.size());
//        OriginalDataAdvProductadsReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new OriginalDataAdvProductadsReport();
//            report.setAdId(MapUtils.getLong(map,"adId" ));
//            report.setAdGroupId(MapUtils.getLong(map,"adGroupId" ));
//            report.setAdGroupName(MapUtils.getString(map,"adGroupName" ));
//            report.setShop(shopName);
//            report.setArea(countryCode);
//            report.setAsin(MapUtils.getString(map,"asin" ));
//            report.setAttributedConversions1d(MapUtils.getInteger(map,"attributedConversions1d"));
//            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map,"attributedConversions1dSameSKU"));
//            report.setAttributedConversions7d(MapUtils.getInteger(map,"attributedConversions7d"));
//            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map,"attributedConversions7dSameSKU"));
//            report.setAttributedConversions14d(MapUtils.getInteger(map,"attributedConversions14d"));
//            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map,"attributedConversions14dSameSKU"));
//            report.setAttributedConversions30d(MapUtils.getInteger(map,"attributedConversions30d"));
//            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map,"attributedConversions30dSameSKU"));
//            report.setAttributedSales1d(MapUtils.getDouble(map,"attributedSales1d"));
//            report.setAttributedSales1dSameSku(MapUtils.getDouble(map,"attributedSales1dSameSKU"));
//            report.setAttributedSales7d(MapUtils.getDouble(map,"attributedSales7d"));
//            report.setAttributedSales7dSameSku(MapUtils.getDouble(map,"attributedSales7dSameSKU"));
//            report.setAttributedSales14d(MapUtils.getDouble(map,"attributedSales14d"));
//            report.setAttributedSales14dSameSku(MapUtils.getDouble(map,"attributedSales14dSameSKU"));
//            report.setAttributedSales30d(MapUtils.getDouble(map,"attributedSales30d"));
//            report.setAttributedSales30dSameSku(MapUtils.getDouble(map,"attributedSales30dSameSKU"));
//            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map,"attributedUnitsOrdered1d"));
//            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map,"attributedUnitsOrdered7d"));
//            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map,"attributedUnitsOrdered14d"));
//            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map,"attributedUnitsOrdered30d"));
//            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered1dSameSKU"));
//            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered7dSameSKU"));
//            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered14dSameSKU"));
//            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered30dSameSKU"));
//            report.setCampaignId(MapUtils.getLong(map,"campaignId"));
//            report.setCampaignName(MapUtils.getString(map,"campaignName"));
//            report.setClicks(MapUtils.getInteger(map,"clicks"));
//            report.setCost(MapUtils.getDouble(map,"cost"));
//            report.setCurrency(MapUtils.getString(map,"currency"));
//            report.setDate(date);
//            report.setImpressions(MapUtils.getInteger(map,"impressions"));
//            report.setSku(MapUtils.getString(map,"sku"));
//            report.setCreateTime(nowDate);
//            list.add(report);
//        }
//        baseInsertList(list);
//
//    }
//
//
//    /**
//     * 获取recordType：campaigns；segment赋值：placement；的广告api报告数据
//     */
////    @Scheduled(cron = "0 50 2 ? * *")
////    @Scheduled(cron = "0 0 13 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void addOriginalDataAdvCampaignsReport() {
//
//        String USDate = DateUtil.getAdvReportTime();
//        String reportDate =  USDate.replace("-", "");
//
//        log.info("***********亚马逊广告api--campaigns定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                boolean tokenFlag = false;
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api--campaigns定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--campaigns定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api--campaigns获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--campaigns定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api--campaigns定时任务结束**********");
//                return;
//            }
//
//            //获取原始数据
//
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("segment","placement");
//                put("reportDate",reportDate);//时间格式YYYYMMDD
//                put("metrics","bidPlus," +
//                        "campaignName," +
//                        "campaignId," +
//                        "campaignStatus," +
//                        "campaignBudget," +
//                        "impressions," +
//                        "clicks," +
//                        "cost," +
//                        "attributedConversions1d," +
//                        "attributedConversions7d," +
//                        "attributedConversions14d," +
//                        "attributedConversions30d," +
//                        "attributedConversions1dSameSKU," +
//                        "attributedConversions7dSameSKU," +
//                        "attributedConversions14dSameSKU," +
//                        "attributedConversions30dSameSKU," +
//                        "attributedUnitsOrdered1d," +
//                        "attributedUnitsOrdered7d," +
//                        "attributedUnitsOrdered14d," +
//                        "attributedUnitsOrdered30d," +
//                        "attributedSales1d," +
//                        "attributedSales7d," +
//                        "attributedSales14d," +
//                        "attributedSales30d," +
//                        "attributedSales1dSameSKU," +
//                        "attributedSales7dSameSKU," +
//                        "attributedSales14dSameSKU," +
//                        "attributedSales30dSameSKU," +
//                        "attributedUnitsOrdered1dSameSKU," +
//                        "attributedUnitsOrdered7dSameSKU," +
//                        "attributedUnitsOrdered14dSameSKU," +
//                        "attributedUnitsOrdered30dSameSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);   //一个店铺下每个区域
//                String profileId = MapUtils.getString(profilesMap, "profileId");
//                String countryCode = MapUtils.getString(profilesMap, "countryCode");
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "campaigns", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum+1).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理
//
//                        String originalDataAdvCampaignsReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDayFromOriginalCampaigns(shop.getShopName(), countryCode);
//                        if (null == originalDataAdvCampaignsReportLatestDay
//                                || DateUtil.compateTime(originalDataAdvCampaignsReportLatestDay, USDate) < 0) {
//                            handleOriginalDataAdvCampaignsReport(lists, shop.getShopName(), countryCode, USDate);
//                        }
//                        break;
//
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum+1).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        }
//        log.info("***********亚马逊广告api--campaigns定时任务结束**********");
//
//    }
//
//
//
//    public void handleOriginalDataAdvCampaignsReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        String nowDate = DateUtil.getDate();
//        List<Object> list = new ArrayList<>(lists.size());
//        OriginalDataAdvCampaignsReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new OriginalDataAdvCampaignsReport();
//            report.setBidPlus(MapUtils.getBoolean(map, "bidPlus").equals(true)?(byte)1:(byte)0);
//            report.setShop(shopName);
//            report.setArea(countryCode);
//            report.setPlacement(MapUtils.getString(map,"placement" ));
//            report.setCampaignStatus(MapUtils.getString(map,"campaignStatus" ));
//            report.setCampaignBudget(MapUtils.getInteger(map,"campaignBudget" ));
//            report.setAttributedConversions1d(MapUtils.getInteger(map,"attributedConversions1d"));
//            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map,"attributedConversions1dSameSKU"));
//            report.setAttributedConversions7d(MapUtils.getInteger(map,"attributedConversions7d"));
//            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map,"attributedConversions7dSameSKU"));
//            report.setAttributedConversions14d(MapUtils.getInteger(map,"attributedConversions14d"));
//            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map,"attributedConversions14dSameSKU"));
//            report.setAttributedConversions30d(MapUtils.getInteger(map,"attributedConversions30d"));
//            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map,"attributedConversions30dSameSKU"));
//            report.setAttributedSales1d(MapUtils.getDouble(map,"attributedSales1d"));
//            report.setAttributedSales1dSameSku(MapUtils.getDouble(map,"attributedSales1dSameSKU"));
//            report.setAttributedSales7d(MapUtils.getDouble(map,"attributedSales7d"));
//            report.setAttributedSales7dSameSku(MapUtils.getDouble(map,"attributedSales7dSameSKU"));
//            report.setAttributedSales14d(MapUtils.getDouble(map,"attributedSales14d"));
//            report.setAttributedSales14dSameSku(MapUtils.getDouble(map,"attributedSales14dSameSKU"));
//            report.setAttributedSales30d(MapUtils.getDouble(map,"attributedSales30d"));
//            report.setAttributedSales30dSameSku(MapUtils.getDouble(map,"attributedSales30dSameSKU"));
//            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map,"attributedUnitsOrdered1d"));
//            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map,"attributedUnitsOrdered7d"));
//            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map,"attributedUnitsOrdered14d"));
//            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map,"attributedUnitsOrdered30d"));
//            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered1dSameSKU"));
//            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered7dSameSKU"));
//            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered14dSameSKU"));
//            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered30dSameSKU"));
//            report.setCampaignId(MapUtils.getLong(map,"campaignId"));
//            report.setCampaignName(MapUtils.getString(map,"campaignName"));
//            report.setClicks(MapUtils.getInteger(map,"clicks"));
//            report.setCost(MapUtils.getDouble(map,"cost"));
//
//            report.setDate(date);
//            report.setImpressions(MapUtils.getInteger(map,"impressions"));
//
//            report.setCreateTime(nowDate);
//            list.add(report);
//        }
//        baseInsertList(list);
//
//    }
//
//
//    /**
//     * 获取recordType：adGroups；的广告api报告数据
//     */
////    @Scheduled(cron = "0 0 13 ? * *")
////    @Scheduled(cron = "0 40 2 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void addOriginalDataAdvAdGroupsReport() {
//
//        String USDate = DateUtil.getAdvReportTime();
//        String reportDate =  USDate.replace("-", "");
//
//        log.info("***********亚马逊广告api--adGroups定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                boolean tokenFlag = false;
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api--adGroups定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--adGroups定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api--adGroups获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--adGroups定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api--adGroups定时任务结束**********");
//                return;
//            }
//
//            //获取原始数据
//
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("reportDate",reportDate);//时间格式YYYYMMDD
//                put("metrics","campaignName," +
//                        "campaignId," +
//                        "adGroupName," +
//                        "adGroupId," +
//                        "impressions," +
//                        "clicks," +
//                        "cost," +
//                        "attributedConversions1d," +
//                        "attributedConversions7d," +
//                        "attributedConversions14d," +
//                        "attributedConversions30d," +
//                        "attributedConversions1dSameSKU," +
//                        "attributedConversions7dSameSKU," +
//                        "attributedConversions14dSameSKU," +
//                        "attributedConversions30dSameSKU," +
//                        "attributedUnitsOrdered1d," +
//                        "attributedUnitsOrdered7d," +
//                        "attributedUnitsOrdered14d," +
//                        "attributedUnitsOrdered30d," +
//                        "attributedSales1d," +
//                        "attributedSales7d," +
//                        "attributedSales14d," +
//                        "attributedSales30d," +
//                        "attributedSales1dSameSKU," +
//                        "attributedSales7dSameSKU," +
//                        "attributedSales14dSameSKU," +
//                        "attributedSales30dSameSKU," +
//                        "attributedUnitsOrdered1dSameSKU," +
//                        "attributedUnitsOrdered7dSameSKU," +
//                        "attributedUnitsOrdered14dSameSKU," +
//                        "attributedUnitsOrdered30dSameSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);   //一个店铺下每个区域
//                String profileId = MapUtils.getString(profilesMap, "profileId");
//                String countryCode = MapUtils.getString(profilesMap, "countryCode");
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "adGroups", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum+1).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理
//
//                        String originalDataAdvAdGroupsReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDayFromOriginalAdGroups(shop.getShopName(), countryCode);
//                        if (null == originalDataAdvAdGroupsReportLatestDay
//                                || DateUtil.compateTime(originalDataAdvAdGroupsReportLatestDay, USDate) < 0) {
//                            handleOriginalDataAdvAdGroupsReport(lists, shop.getShopName(), countryCode, USDate);
//                        }
//                        break;
//
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum+1).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        }
//        log.info("***********亚马逊广告api--adGroups定时任务结束**********");
//
//    }
//
//
//
//
//    public void handleOriginalDataAdvAdGroupsReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        String nowDate = DateUtil.getDate();
//        List<Object> list = new ArrayList<>(lists.size());
//        OriginalDataAdvAdgroupsReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new OriginalDataAdvAdgroupsReport();
//            report.setShop(shopName);
//            report.setArea(countryCode);
//            report.setAttributedConversions1d(MapUtils.getInteger(map,"attributedConversions1d"));
//            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map,"attributedConversions1dSameSKU"));
//            report.setAttributedConversions7d(MapUtils.getInteger(map,"attributedConversions7d"));
//            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map,"attributedConversions7dSameSKU"));
//            report.setAttributedConversions14d(MapUtils.getInteger(map,"attributedConversions14d"));
//            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map,"attributedConversions14dSameSKU"));
//            report.setAttributedConversions30d(MapUtils.getInteger(map,"attributedConversions30d"));
//            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map,"attributedConversions30dSameSKU"));
//            report.setAttributedSales1d(MapUtils.getDouble(map,"attributedSales1d"));
//            report.setAttributedSales1dSameSku(MapUtils.getDouble(map,"attributedSales1dSameSKU"));
//            report.setAttributedSales7d(MapUtils.getDouble(map,"attributedSales7d"));
//            report.setAttributedSales7dSameSku(MapUtils.getDouble(map,"attributedSales7dSameSKU"));
//            report.setAttributedSales14d(MapUtils.getDouble(map,"attributedSales14d"));
//            report.setAttributedSales14dSameSku(MapUtils.getDouble(map,"attributedSales14dSameSKU"));
//            report.setAttributedSales30d(MapUtils.getDouble(map,"attributedSales30d"));
//            report.setAttributedSales30dSameSku(MapUtils.getDouble(map,"attributedSales30dSameSKU"));
//            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map,"attributedUnitsOrdered1d"));
//            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map,"attributedUnitsOrdered7d"));
//            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map,"attributedUnitsOrdered14d"));
//            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map,"attributedUnitsOrdered30d"));
//            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered1dSameSKU"));
//            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered7dSameSKU"));
//            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered14dSameSKU"));
//            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered30dSameSKU"));
//            report.setCampaignId(MapUtils.getLong(map,"campaignId"));
//            report.setCampaignName(MapUtils.getString(map,"campaignName"));
//            report.setClicks(MapUtils.getInteger(map,"clicks"));
//            report.setCost(MapUtils.getDouble(map,"cost"));
//             report.setAdGroupId(MapUtils.getLong(map,"adGroupId" ));
//             report.setAdGroupName(MapUtils.getString(map,"adGroupName" ));
//            report.setDate(date);
//            report.setImpressions(MapUtils.getInteger(map,"impressions"));
//
//            report.setCreateTime(nowDate);
//            list.add(report);
//        }
//        baseInsertList(list);
//
//    }
//
//
//    /**
//     * 获取recordType：targets；segment:query 的广告api报告数据
//     */
////    @Scheduled(cron = "0 0 13 ? * *")
////    @Scheduled(cron = "0 30 2 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void addOriginalDataAdvTargetsReport() {
//
//        String USDate = DateUtil.getAdvReportTime();
//        String reportDate =  USDate.replace("-", "");
//
//        log.info("***********亚马逊广告api--targets定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                boolean tokenFlag = false;
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api--targets定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--targets定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api--targets获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--targets定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api--targets定时任务结束**********");
//                return;
//            }
//
//            //获取原始数据
//
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("segment","query");
//                put("reportDate",reportDate);//时间格式YYYYMMDD
//                put("metrics","campaignName," +
//                        "campaignId," +
//                        "adGroupName," +
//                        "adGroupId," +
//                        "targetId," +
//                        "targetingExpression," +
//                        "targetingText," +
//                        "targetingType," +
//                        "impressions," +
//                        "clicks," +
//                        "cost," +
//                        "attributedConversions1d," +
//                        "attributedConversions7d," +
//                        "attributedConversions14d," +
//                        "attributedConversions30d," +
//                        "attributedConversions1dSameSKU," +
//                        "attributedConversions7dSameSKU," +
//                        "attributedConversions14dSameSKU," +
//                        "attributedConversions30dSameSKU," +
//                        "attributedUnitsOrdered1d," +
//                        "attributedUnitsOrdered7d," +
//                        "attributedUnitsOrdered14d," +
//                        "attributedUnitsOrdered30d," +
//                        "attributedSales1d," +
//                        "attributedSales7d," +
//                        "attributedSales14d," +
//                        "attributedSales30d," +
//                        "attributedSales1dSameSKU," +
//                        "attributedSales7dSameSKU," +
//                        "attributedSales14dSameSKU," +
//                        "attributedSales30dSameSKU," +
//                        "attributedUnitsOrdered1dSameSKU," +
//                        "attributedUnitsOrdered7dSameSKU," +
//                        "attributedUnitsOrdered14dSameSKU," +
//                        "attributedUnitsOrdered30dSameSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);   //一个店铺下每个区域
//                String profileId = MapUtils.getString(profilesMap, "profileId");
//                String countryCode = MapUtils.getString(profilesMap, "countryCode");
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "targets", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum+1).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理
//
//                        String originalDataAdvTargetsReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDayFromOriginalTargets(shop.getShopName(), countryCode);
//                        if (null == originalDataAdvTargetsReportLatestDay
//                                || DateUtil.compateTime(originalDataAdvTargetsReportLatestDay, USDate) < 0) {
//                            handleOriginalDataAdvTargetsReport(lists, shop.getShopName(), countryCode, USDate);
//                        }
//                        break;
//
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum+1).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        }
//        log.info("***********亚马逊广告api--targets定时任务结束**********");
//
//    }
//
//
//    public void handleOriginalDataAdvTargetsReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        String nowDate = DateUtil.getDate();
//        List<Object> list = new ArrayList<>(lists.size());
//        OriginalDataAdvTargetsReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new OriginalDataAdvTargetsReport();
//            report.setShop(shopName);
//            report.setArea(countryCode);
//            report.setAttributedConversions1d(MapUtils.getInteger(map,"attributedConversions1d"));
//            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map,"attributedConversions1dSameSKU"));
//            report.setAttributedConversions7d(MapUtils.getInteger(map,"attributedConversions7d"));
//            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map,"attributedConversions7dSameSKU"));
//            report.setAttributedConversions14d(MapUtils.getInteger(map,"attributedConversions14d"));
//            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map,"attributedConversions14dSameSKU"));
//            report.setAttributedConversions30d(MapUtils.getInteger(map,"attributedConversions30d"));
//            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map,"attributedConversions30dSameSKU"));
//            report.setAttributedSales1d(MapUtils.getDouble(map,"attributedSales1d"));
//            report.setAttributedSales1dSameSku(MapUtils.getDouble(map,"attributedSales1dSameSKU"));
//            report.setAttributedSales7d(MapUtils.getDouble(map,"attributedSales7d"));
//            report.setAttributedSales7dSameSku(MapUtils.getDouble(map,"attributedSales7dSameSKU"));
//            report.setAttributedSales14d(MapUtils.getDouble(map,"attributedSales14d"));
//            report.setAttributedSales14dSameSku(MapUtils.getDouble(map,"attributedSales14dSameSKU"));
//            report.setAttributedSales30d(MapUtils.getDouble(map,"attributedSales30d"));
//            report.setAttributedSales30dSameSku(MapUtils.getDouble(map,"attributedSales30dSameSKU"));
//            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map,"attributedUnitsOrdered1d"));
//            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map,"attributedUnitsOrdered7d"));
//            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map,"attributedUnitsOrdered14d"));
//            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map,"attributedUnitsOrdered30d"));
//            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered1dSameSKU"));
//            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered7dSameSKU"));
//            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered14dSameSKU"));
//            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered30dSameSKU"));
//            report.setCampaignId(MapUtils.getLong(map,"campaignId"));
//            report.setCampaignName(MapUtils.getString(map,"campaignName"));
//            report.setClicks(MapUtils.getInteger(map,"clicks"));
//            report.setCost(MapUtils.getDouble(map,"cost"));
//            report.setAdGroupId(MapUtils.getLong(map,"adGroupId" ));
//            report.setAdGroupName(MapUtils.getString(map,"adGroupName" ));
//            report.setDate(date);
//            report.setImpressions(MapUtils.getInteger(map,"impressions"));
//            report.setQuery(MapUtils.getString(map,"query" ));
//            report.setTargetId(MapUtils.getLong(map, "targetId"));
//            report.setTargetingExpression(MapUtils.getString(map, "targetingExpression"));
//            report.setTargetingText(MapUtils.getString(map, "targetingText"));
//            report.setTargetingType(MapUtils.getString(map, "targetingType"));
//            report.setCreateTime(nowDate);
//            list.add(report);
//        }
//        baseInsertList(list);
//
//    }
//
//    /**
//     * 获取recordType：keywords；segment:query 的广告api报告数据
//     */
////    @Scheduled(cron = "0 20 2 ? * *")
////    @Scheduled(cron = "0 0 13 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void addOriginalDataAdvKeywordsReport() {
//
//        String USDate = DateUtil.getAdvReportTime();
//        String reportDate =  USDate.replace("-", "");
//
//        log.info("***********亚马逊广告api--keywords定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                boolean tokenFlag = false;
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api--keywords定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--keywords定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api--keywords获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--keywords定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api--keywords定时任务结束**********");
//                return;
//            }
//
//            //获取原始数据
//
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("segment","query");
//                put("reportDate",reportDate);//时间格式YYYYMMDD
//                put("metrics","campaignName," +
//                        "campaignId," +
//                        "adGroupName," +
//                        "adGroupId," +
//                        "keywordId," +
//                        "keywordText," +
//                        "matchType," +
//                        "impressions," +
//                        "clicks," +
//                        "cost," +
//                        "attributedConversions1d," +
//                        "attributedConversions7d," +
//                        "attributedConversions14d," +
//                        "attributedConversions30d," +
//                        "attributedConversions1dSameSKU," +
//                        "attributedConversions7dSameSKU," +
//                        "attributedConversions14dSameSKU," +
//                        "attributedConversions30dSameSKU," +
//                        "attributedUnitsOrdered1d," +
//                        "attributedUnitsOrdered7d," +
//                        "attributedUnitsOrdered14d," +
//                        "attributedUnitsOrdered30d," +
//                        "attributedSales1d," +
//                        "attributedSales7d," +
//                        "attributedSales14d," +
//                        "attributedSales30d," +
//                        "attributedSales1dSameSKU," +
//                        "attributedSales7dSameSKU," +
//                        "attributedSales14dSameSKU," +
//                        "attributedSales30dSameSKU," +
//                        "attributedUnitsOrdered1dSameSKU," +
//                        "attributedUnitsOrdered7dSameSKU," +
//                        "attributedUnitsOrdered14dSameSKU," +
//                        "attributedUnitsOrdered30dSameSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);   //一个店铺下每个区域
//                String profileId = MapUtils.getString(profilesMap, "profileId");
//                String countryCode = MapUtils.getString(profilesMap, "countryCode");
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "sp", "keywords", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum+1).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理
//
//                        String originalDataAdvKeywordsReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDayFromOriginalKeywords(shop.getShopName(), countryCode);
//                        if (null == originalDataAdvKeywordsReportLatestDay
//                                || DateUtil.compateTime(originalDataAdvKeywordsReportLatestDay, USDate) < 0) {
//                            handleOriginalDataAdvKeywordsReport(lists, shop.getShopName(), countryCode, USDate);
//                        }
//                        break;
//
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum+1).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        }
//        log.info("***********亚马逊广告api--keywords定时任务结束**********");
//
//    }
//
//
//    public void handleOriginalDataAdvKeywordsReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        String nowDate = DateUtil.getDate();
//        List<Object> list = new ArrayList<>(lists.size());
//        OriginalDataAdvKeywordsReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new OriginalDataAdvKeywordsReport();
//            report.setShop(shopName);
//            report.setArea(countryCode);
//            report.setAttributedConversions1d(MapUtils.getInteger(map,"attributedConversions1d"));
//            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map,"attributedConversions1dSameSKU"));
//            report.setAttributedConversions7d(MapUtils.getInteger(map,"attributedConversions7d"));
//            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map,"attributedConversions7dSameSKU"));
//            report.setAttributedConversions14d(MapUtils.getInteger(map,"attributedConversions14d"));
//            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map,"attributedConversions14dSameSKU"));
//            report.setAttributedConversions30d(MapUtils.getInteger(map,"attributedConversions30d"));
//            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map,"attributedConversions30dSameSKU"));
//            report.setAttributedSales1d(MapUtils.getDouble(map,"attributedSales1d"));
//            report.setAttributedSales1dSameSku(MapUtils.getDouble(map,"attributedSales1dSameSKU"));
//            report.setAttributedSales7d(MapUtils.getDouble(map,"attributedSales7d"));
//            report.setAttributedSales7dSameSku(MapUtils.getDouble(map,"attributedSales7dSameSKU"));
//            report.setAttributedSales14d(MapUtils.getDouble(map,"attributedSales14d"));
//            report.setAttributedSales14dSameSku(MapUtils.getDouble(map,"attributedSales14dSameSKU"));
//            report.setAttributedSales30d(MapUtils.getDouble(map,"attributedSales30d"));
//            report.setAttributedSales30dSameSku(MapUtils.getDouble(map,"attributedSales30dSameSKU"));
//            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map,"attributedUnitsOrdered1d"));
//            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map,"attributedUnitsOrdered7d"));
//            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map,"attributedUnitsOrdered14d"));
//            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map,"attributedUnitsOrdered30d"));
//            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered1dSameSKU"));
//            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered7dSameSKU"));
//            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered14dSameSKU"));
//            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map,"attributedUnitsOrdered30dSameSKU"));
//            report.setCampaignId(MapUtils.getLong(map,"campaignId"));
//            report.setCampaignName(MapUtils.getString(map,"campaignName"));
//            report.setClicks(MapUtils.getInteger(map,"clicks"));
//            report.setCost(MapUtils.getDouble(map,"cost"));
//            report.setAdGroupId(MapUtils.getLong(map,"adGroupId" ));
//            report.setAdGroupName(MapUtils.getString(map,"adGroupName" ));
//            report.setDate(date);
//            report.setImpressions(MapUtils.getInteger(map,"impressions"));
//            report.setQuery(MapUtils.getString(map,"query" ));
//            report.setKeywordId(MapUtils.getLong(map, "keywordId"));
//            report.setKeywordText(MapUtils.getString(map, "keywordText"));
//            report.setMatchType(MapUtils.getString(map, "matchType"));
//            report.setCreateTime(nowDate);
//            list.add(report);
//        }
//        baseInsertList(list);
//
//    }
//
//
//    /**
//     * 获取recordType：keywords；segment:query 的广告api报告数据
//     */
//    //@Scheduled(cron = "0 0 13 ? * *")
////    @Scheduled(cron = "0 10 2 ? * *")
//    @Transactional(rollbackFor = Exception.class)
//    public void addOriginalDataAdvAsinsReport() {
//
//        String USDate = DateUtil.getAdvReportTime();
//        String reportDate =  USDate.replace("-", "");
//
//        log.info("***********亚马逊广告api--keywords定时任务开始*************");
//        //查询授权过的店铺
//        List<Shop> shopList = shopMapper.getAuthedShopList();
//        for (Shop shop : shopList) {
//            List<Map<String, Object>> profiles = null;
//            try {
//                boolean tokenFlag = false;
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    //获取token
//                    tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
//                    if (tokenFlag) {
//                        break;
//                    }
//                }
//                if (!tokenFlag) {
//                    logWarnErrorMsg("亚马逊广告api--keywords定时任务开始token获取失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--keywords定时任务结束**********");
//                    return;
//                }
//                for (int i = 0; i < MAX_ERROR_NUM; i++) {
//                    profiles = AmazonAdvertApiUtil.getProfiles(shop);
//                    if (null != profiles) {
//                        break;
//                    }
//                }
//                if (null == profiles) {
//                    logWarnErrorMsg("亚马逊广告api--keywords获取配置文件失败", shop.getShopName());
//                    log.info("***********亚马逊广告api--keywords定时任务结束**********");
//                    return;
//                }
//            } catch (Exception e) {
//                log.warn("亚马逊广告api定时任务出错,详情:", e);
//                log.info("***********亚马逊广告api--keywords定时任务结束**********");
//                return;
//            }
//
//            //获取原始数据
//
//            StringBuffer errorMsg = new StringBuffer();
//            Map<String, Object> params = new HashMap<String, Object>(2) {{
//                put("reportDate",reportDate);//时间格式YYYYMMDD
//                put("campaignType","sponsoredProducts");
//                put("metrics","campaignName," +
//                        "campaignId," +
//                        "adGroupName," +
//                        "adGroupId," +
//                        "keywordId," +
//                        "keywordText," +
//                        "asin," +
//                        "otherAsin," +
//                        "sku," +
//                        "currency," +
//                        "matchType," +
//                        "attributedUnitsOrdered1d," +
//                        "attributedUnitsOrdered7d," +
//                        "attributedUnitsOrdered14d," +
//                        "attributedUnitsOrdered30d," +
//                        "attributedUnitsOrdered1dOtherSKU," +
//                        "attributedUnitsOrdered7dOtherSKU," +
//                        "attributedUnitsOrdered14dOtherSKU," +
//                        "attributedUnitsOrdered30dOtherSKU," +
//                        "attributedSales1dOtherSKU," +
//                        "attributedSales7dOtherSKU," +
//                        "attributedSales14dOtherSKU," +
//                        "attributedSales30dOtherSKU");
//            }};
//            //创建事务还原点
//            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
//            for (int index = 0; index < profiles.size(); index++) {
//                Map<String, Object> profilesMap = profiles.get(index);   //一个店铺下每个区域
//                String profileId = MapUtils.getString(profilesMap, "profileId");
//                String countryCode = MapUtils.getString(profilesMap, "countryCode");
//                for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
//                    try {
//                        List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()), "", "asins", params);
//                        if (null == lists || 0 >= lists.size()) {
//                            errorMsg.append("第").append(errorNum+1).append("次，获取没数据");
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                            continue;
//                        }
//                        //处理
//
//                        String originalDataAdvAsinsReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDayFromOriginalAsins(shop.getShopName(), countryCode);
//                        if (null == originalDataAdvAsinsReportLatestDay
//                                || DateUtil.compateTime(originalDataAdvAsinsReportLatestDay, USDate) < 0) {
//                            handleOriginalDataAdvAsinsReport(lists, shop.getShopName(), countryCode, USDate);
//                        }
//                        break;
//
//                    } catch (Exception e) {
//                        //回滚事务
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        e.printStackTrace();
//                        errorMsg.append("第").append(errorNum+1).append("次出错,错误详情:").append(e);
//                        try {
//                            Thread.sleep((long) Math.pow(2, errorNum + 1));
//                        } catch (InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        }
//        log.info("***********亚马逊广告api--keywords定时任务结束**********");
//
//    }
//
//
//    public void handleOriginalDataAdvAsinsReport(List<Map<String, Object>> lists, String shopName, String countryCode, String date)
//            throws Exception {
//        String nowDate = DateUtil.getDate();
//        List<Object> list = new ArrayList<>(lists.size());
//        OriginalDataAdvAsinsReport report = null;
//        for (Map<String, Object> map : lists) {
//            report = new OriginalDataAdvAsinsReport();
//            report.setShop(shopName);
//            report.setArea(countryCode);
//
//            report.setAttributedSales1dOtherSku(MapUtils.getDouble(map,"attributedSales1dOtherSKU"));
//            report.setAttributedSales7dOtherSku(MapUtils.getDouble(map,"attributedSales7dOtherSKU"));
//            report.setAttributedSales14dOtherSku(MapUtils.getDouble(map,"attributedSales14dOtherSKU"));
//            report.setAttributedSales30dOtherSku(MapUtils.getDouble(map,"attributedSales30dOtherSKU"));
//            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map,"attributedUnitsOrdered1d"));
//            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map,"attributedUnitsOrdered7d"));
//            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map,"attributedUnitsOrdered14d"));
//            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map,"attributedUnitsOrdered30d"));
//            report.setAttributedUnitsOrdered1dOtherSku(MapUtils.getInteger(map,"attributedUnitsOrdered1dOtherSKU"));
//            report.setAttributedUnitsOrdered7dOtherSku(MapUtils.getInteger(map,"attributedUnitsOrdered7dOtherSKU"));
//            report.setAttributedUnitsOrdered14dOtherSku(MapUtils.getInteger(map,"attributedUnitsOrdered14dOtherSKU"));
//            report.setAttributedUnitsOrdered30dOtherSku(MapUtils.getInteger(map,"attributedUnitsOrdered30dOtherSKU"));
//            report.setCampaignId(MapUtils.getLong(map,"campaignId"));
//            report.setCampaignName(MapUtils.getString(map,"campaignName"));
//
//            report.setAdGroupId(MapUtils.getLong(map,"adGroupId" ));
//            report.setAdGroupName(MapUtils.getString(map,"adGroupName" ));
//            report.setDate(date);
//
//            report.setKeywordId(MapUtils.getLong(map, "keywordId"));
//            report.setKeywordText(MapUtils.getString(map, "keywordText"));
//            report.setMatchType(MapUtils.getString(map, "matchType"));
//            report.setCreateTime(nowDate);
//            report.setAsin(MapUtils.getString(map, "asin"));
//            report.setOtherAsin(MapUtils.getString(map, "otherAsin"));
//            report.setSku(MapUtils.getString(map, "sku"));
//            report.setCurrency(MapUtils.getString(map, "currency"));
//            list.add(report);
//        }
//        baseInsertList(list);
//
//    }
}
