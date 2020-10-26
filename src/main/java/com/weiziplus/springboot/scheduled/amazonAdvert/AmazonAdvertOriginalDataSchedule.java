package com.weiziplus.springboot.scheduled.amazonAdvert;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.originalAdvertData.ErrorDateMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ProfileMapper;
import com.weiziplus.springboot.mapper.shop.RefreshTokenMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.sspa.AdvertOriginalDataMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsPlacementReportMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsSearchTermReportMapper;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.models.DO.ErrorDateDO;
import com.weiziplus.springboot.utils.CryptoUtil;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.amazon.AmazonAdvertApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
@Slf4j
public class AmazonAdvertOriginalDataSchedule extends BaseService {

    @Autowired
    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;

    @Autowired
    SponsoredProductsPlacementReportMapper sponsoredProductsPlacementReportMapper;

    @Autowired
    SponsoredProductsSearchTermReportMapper sponsoredProductsSearchTermReportMapper;

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    AreaMapper areaMapper;

    @Autowired
    ProfileMapper profileMapper;

    @Autowired
    AdvertOriginalDataMapper advertOriginalDataMapper;

    @Autowired
    RefreshTokenMapper refreshTokenMapper;

    @Autowired
    ErrorDateMapper errorDateMapper;

    /**
     * 错误重试次数
     */
    private final Integer MAX_ERROR_NUM = 5;


    /**
     * 定时获取recordType：productAds；的广告api报告数据
     */
    //@Scheduled(cron = "0 0 20 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvProductadsReport() {
        String date = DateUtil.getFetureDate(-1);
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--productAds定时任务开始*************");
        //查询授权过的店铺对应区域的配置文件ID
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            //根据基本的配置信息去获取所有的原始数据
            StringBuffer errorMsg = new StringBuffer();
            Map<String, Object> params = new HashMap<String, Object>(2) {{
                put("reportDate", reportDate);//时间格式YYYYMMDD
                put("metrics", "campaignName,campaignId,adGroupName,adGroupId,impressions,clicks,cost,currency,sku,asin,attributedConversions1d,attributedConversions7d" +
                        ",attributedConversions14d,attributedConversions30d,attributedConversions1dSameSKU,attributedConversions7dSameSKU,attributedConversions14dSameSKU" +
                        ",attributedConversions30dSameSKU,attributedUnitsOrdered1d,attributedUnitsOrdered7d,attributedUnitsOrdered14d,attributedUnitsOrdered30d,attributedSales1d" +
                        ",attributedSales7d,attributedSales14d,attributedSales30d,attributedSales1dSameSKU,attributedSales7dSameSKU,attributedSales14dSameSKU,attributedSales30dSameSKU,attributedUnitsOrdered1dSameSKU,attributedUnitsOrdered7dSameSKU,attributedUnitsOrdered14dSameSKU,attributedUnitsOrdered30dSameSKU");
            }};
            Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
            Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
            String sellerId = shop.getSellerId();
            String regionCode = area.getRegionCode();
            RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的productAds定时任务开始*************");
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
                try {
                    //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                    List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "productAds", params);
                    if (null == lists) {
                        errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                        log.warn(errorMsg.toString());
                        Thread.sleep(1000L);
                        //若失败五次，则记录这个错误日期
                        if (errorNum == 4) {
                            ErrorDateDO errorDateDO = new ErrorDateDO();
                            errorDateDO.setSellerId(shop.getSellerId());
                            errorDateDO.setArea(area.getAdvertCountryCode());
                            errorDateDO.setErrorDate(reportDate);
                            errorDateDO.setType(1);
                            errorDateDO.setStatus(1);
                            errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            errorDateMapper.addErrorDate(errorDateDO);
                        }
                        continue;
                    }
                    //处理数据
                    Integer dataCountFromOriginalProductAds = advertOriginalDataMapper.getDataCountFromOriginalProductAds(shop.getShopName(), area.getAdvertCountryCode(), date);
                    if (lists.size() <= dataCountFromOriginalProductAds) {
                        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的productAds在" + date + "的数据已存在**********");
                        break;
                    } else {
                        advertOriginalDataMapper.deleteDataFromProductAdsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                    }
                    handleOriginalDataAdvProductadsReport(lists, shop, area.getAdvertCountryCode(), date);
                    break;
                } catch (Exception e) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    e.printStackTrace();
                    errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                    try {
                        Thread.sleep((long) Math.pow(2, errorNum + 1));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        log.info("***********亚马逊广告api--productAds定时任务结束**********");
    }

    /**
     * 获取指定时间的ProductadsReport数据--苏建东
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvProductadsReport(String date, ShopAreaProfile shopAreaProfile) {
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--productAds定时任务开始*************");
        //根据基本的配置信息去获取所有的原始数据
        StringBuffer errorMsg = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>(2) {{
            put("reportDate", reportDate);//时间格式YYYYMMDD
            put("metrics", "campaignName,campaignId,adGroupName,adGroupId,impressions,clicks,cost,currency,sku,asin,attributedConversions1d,attributedConversions7d" +
                    ",attributedConversions14d,attributedConversions30d,attributedConversions1dSameSKU,attributedConversions7dSameSKU,attributedConversions14dSameSKU" +
                    ",attributedConversions30dSameSKU,attributedUnitsOrdered1d,attributedUnitsOrdered7d,attributedUnitsOrdered14d,attributedUnitsOrdered30d,attributedSales1d" +
                    ",attributedSales7d,attributedSales14d,attributedSales30d,attributedSales1dSameSKU,attributedSales7dSameSKU,attributedSales14dSameSKU,attributedSales30dSameSKU,attributedUnitsOrdered1dSameSKU,attributedUnitsOrdered7dSameSKU,attributedUnitsOrdered14dSameSKU,attributedUnitsOrdered30dSameSKU");
        }};
        Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
        Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
        String sellerId = shop.getSellerId();
        String regionCode = area.getRegionCode();
        RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
        AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的productAds定时任务开始*************");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
            try {
                //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "productAds", params);
                if (null == lists) {
                    errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                    log.warn("profileid:" + shopAreaProfile.getProfileId() + "店铺：" + shop.getShopName() + errorMsg.toString());
                    Thread.sleep(1000L);
                    //若失败五次，则记录这个错误日期
                    if (errorNum == 4) {
                        ErrorDateDO errorDateDO = new ErrorDateDO();
                        errorDateDO.setSellerId(shop.getSellerId());
                        errorDateDO.setArea(area.getAdvertCountryCode());
                        errorDateDO.setErrorDate(reportDate);
                        errorDateDO.setType(1);
                        errorDateDO.setStatus(1);
                        errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        errorDateMapper.addErrorDate(errorDateDO);
                    }
                    continue;
                }
                //处理数据
                Integer dataCountFromOriginalProductAds = advertOriginalDataMapper.getDataCountFromOriginalProductAds(shop.getShopName(), area.getAdvertCountryCode(), date);
                if (lists.size() <= dataCountFromOriginalProductAds) {
                    log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的productAds在" + date + "的数据已存在**********");
                    break;
                } else {
                    advertOriginalDataMapper.deleteDataFromProductAdsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                }
                handleOriginalDataAdvProductadsReport(lists, shop, area.getAdvertCountryCode(), date);
                break;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                e.printStackTrace();
                errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                try {
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        log.info("***********亚马逊广告api--productAds定时任务结束**********");
    }

    public void handleOriginalDataAdvProductadsReport(List<Map<String, Object>> lists, Shop shop, String countryCode, String date)
            throws Exception {
        String nowDate = DateUtil.getDate();
        List<Object> list = new ArrayList<>(lists.size());
        OriginalDataAdvProductadsReport report = null;
        for (Map<String, Object> map : lists) {
            report = new OriginalDataAdvProductadsReport();
            report.setAdId(MapUtils.getLong(map, "adId"));
            report.setAdGroupId(MapUtils.getLong(map, "adGroupId"));
            report.setAdGroupName(MapUtils.getString(map, "adGroupName"));
            report.setShop(shop.getShopName());
            report.setArea(countryCode);
            report.setSellerId(shop.getSellerId());
            report.setAsin(MapUtils.getString(map, "asin"));
            report.setAttributedConversions1d(MapUtils.getInteger(map, "attributedConversions1d"));
            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map, "attributedConversions1dSameSKU"));
            report.setAttributedConversions7d(MapUtils.getInteger(map, "attributedConversions7d"));
            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map, "attributedConversions7dSameSKU"));
            report.setAttributedConversions14d(MapUtils.getInteger(map, "attributedConversions14d"));
            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map, "attributedConversions14dSameSKU"));
            report.setAttributedConversions30d(MapUtils.getInteger(map, "attributedConversions30d"));
            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map, "attributedConversions30dSameSKU"));
            report.setAttributedSales1d(MapUtils.getDouble(map, "attributedSales1d"));
            report.setAttributedSales1dSameSku(MapUtils.getDouble(map, "attributedSales1dSameSKU"));
            report.setAttributedSales7d(MapUtils.getDouble(map, "attributedSales7d"));
            report.setAttributedSales7dSameSku(MapUtils.getDouble(map, "attributedSales7dSameSKU"));
            report.setAttributedSales14d(MapUtils.getDouble(map, "attributedSales14d"));
            report.setAttributedSales14dSameSku(MapUtils.getDouble(map, "attributedSales14dSameSKU"));
            report.setAttributedSales30d(MapUtils.getDouble(map, "attributedSales30d"));
            report.setAttributedSales30dSameSku(MapUtils.getDouble(map, "attributedSales30dSameSKU"));
            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map, "attributedUnitsOrdered1d"));
            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map, "attributedUnitsOrdered7d"));
            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map, "attributedUnitsOrdered14d"));
            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map, "attributedUnitsOrdered30d"));
            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered1dSameSKU"));
            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered7dSameSKU"));
            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered14dSameSKU"));
            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered30dSameSKU"));
            report.setCampaignId(MapUtils.getLong(map, "campaignId"));
            report.setCampaignName(MapUtils.getString(map, "campaignName"));
            report.setClicks(MapUtils.getInteger(map, "clicks"));
            report.setCost(MapUtils.getDouble(map, "cost"));
            report.setCurrency(MapUtils.getString(map, "currency"));
            report.setDate(date);
            report.setImpressions(MapUtils.getInteger(map, "impressions"));
            report.setSku(MapUtils.getString(map, "sku"));
            report.setCreateTime(nowDate);
            list.add(report);
        }
        baseInsertList(list);
    }

    /**
     * 获取recordType：campaigns；segment赋值：placement；的广告api报告数据
     */
    //@Scheduled(cron = "0 0 20 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvCampaignsReport() {
        String date = DateUtil.getFetureDate(-1);
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--campaigns定时任务开始*************");
        //查询授权过的店铺对应区域的配置文件ID
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            //根据基本的配置信息去获取所有的原始数据
            StringBuffer errorMsg = new StringBuffer();
            Map<String, Object> params = new HashMap<String, Object>(2) {{
                put("segment", "placement");
                put("reportDate", reportDate);//时间格式YYYYMMDD
                put("metrics", "bidPlus," +
                        "campaignName," +
                        "campaignId," +
                        "campaignStatus," +
                        "campaignBudget," +
                        "impressions," +
                        "clicks," +
                        "cost," +
                        "attributedConversions1d," +
                        "attributedConversions7d," +
                        "attributedConversions14d," +
                        "attributedConversions30d," +
                        "attributedConversions1dSameSKU," +
                        "attributedConversions7dSameSKU," +
                        "attributedConversions14dSameSKU," +
                        "attributedConversions30dSameSKU," +
                        "attributedUnitsOrdered1d," +
                        "attributedUnitsOrdered7d," +
                        "attributedUnitsOrdered14d," +
                        "attributedUnitsOrdered30d," +
                        "attributedSales1d," +
                        "attributedSales7d," +
                        "attributedSales14d," +
                        "attributedSales30d," +
                        "attributedSales1dSameSKU," +
                        "attributedSales7dSameSKU," +
                        "attributedSales14dSameSKU," +
                        "attributedSales30dSameSKU," +
                        "attributedUnitsOrdered1dSameSKU," +
                        "attributedUnitsOrdered7dSameSKU," +
                        "attributedUnitsOrdered14dSameSKU," +
                        "attributedUnitsOrdered30dSameSKU");
            }};
            Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
            Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
            String sellerId = shop.getSellerId();
            String regionCode = area.getRegionCode();
            RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的campaigns定时任务开始*************");
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
                try {
                    //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                    List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "campaigns", params);
                    if (null == lists) {
                        errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                        log.warn(errorMsg.toString());
                        Thread.sleep(1000L);
                        //若失败五次，则记录这个错误日期
                        if (errorNum == 4) {
                            ErrorDateDO errorDateDO = new ErrorDateDO();
                            errorDateDO.setSellerId(shop.getSellerId());
                            errorDateDO.setArea(area.getAdvertCountryCode());
                            errorDateDO.setErrorDate(reportDate);
                            errorDateDO.setType(2);
                            errorDateDO.setStatus(1);
                            errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            errorDateMapper.addErrorDate(errorDateDO);
                        }
                        continue;
                    }
                    //处理
                    Integer dataCountFromOriginalCampaigns = advertOriginalDataMapper.getDataCountFromOriginalCampaigns(shop.getShopName(), area.getAdvertCountryCode(), date);
                    if (lists.size() <= dataCountFromOriginalCampaigns) {
                        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的campaigns在" + date + "的数据已存在**********");
                        break;
                    } else {
                        advertOriginalDataMapper.deleteDataFromCampaignsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                    }
                    handleOriginalDataAdvCampaignsReport(lists, shop, area.getAdvertCountryCode(), date);
                    break;
                } catch (Exception e) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    e.printStackTrace();
                    errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                    try {
                        Thread.sleep((long) Math.pow(2, errorNum + 1));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        log.info("***********亚马逊广告api--campaigns定时任务结束**********");
    }

    /**
     * 获取指定时间的CampaignsReport数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvCampaignsReport(String date, ShopAreaProfile shopAreaProfile) {
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--campaigns定时任务开始*************");
        //根据基本的配置信息去获取所有的原始数据
        StringBuffer errorMsg = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>(3) {{
            put("segment", "placement");
            put("reportDate", reportDate);//时间格式YYYYMMDD
            put("metrics", "bidPlus," +
                    "campaignName," +
                    "campaignId," +
                    "campaignStatus," +
                    "campaignBudget," +
                    "impressions," +
                    "clicks," +
                    "cost," +
                    "attributedConversions1d," +
                    "attributedConversions7d," +
                    "attributedConversions14d," +
                    "attributedConversions30d," +
                    "attributedConversions1dSameSKU," +
                    "attributedConversions7dSameSKU," +
                    "attributedConversions14dSameSKU," +
                    "attributedConversions30dSameSKU," +
                    "attributedUnitsOrdered1d," +
                    "attributedUnitsOrdered7d," +
                    "attributedUnitsOrdered14d," +
                    "attributedUnitsOrdered30d," +
                    "attributedSales1d," +
                    "attributedSales7d," +
                    "attributedSales14d," +
                    "attributedSales30d," +
                    "attributedSales1dSameSKU," +
                    "attributedSales7dSameSKU," +
                    "attributedSales14dSameSKU," +
                    "attributedSales30dSameSKU," +
                    "attributedUnitsOrdered1dSameSKU," +
                    "attributedUnitsOrdered7dSameSKU," +
                    "attributedUnitsOrdered14dSameSKU," +
                    "attributedUnitsOrdered30dSameSKU");
        }};
        Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
        Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
        String sellerId = shop.getSellerId();
        String regionCode = area.getRegionCode();
        RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
        AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的campaigns定时任务开始*************");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
            try {
                //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "campaigns", params);
                if (null == lists) {
                    errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                    log.warn("profileid:" + shopAreaProfile.getProfileId() + "店铺：" + shop.getShopName() + errorMsg.toString());
                    Thread.sleep(1000L);
                    //若失败五次，则记录这个错误日期
                    if (errorNum == 4) {
                        ErrorDateDO errorDateDO = new ErrorDateDO();
                        errorDateDO.setSellerId(shop.getSellerId());
                        errorDateDO.setArea(area.getAdvertCountryCode());
                        errorDateDO.setErrorDate(reportDate);
                        errorDateDO.setType(2);
                        errorDateDO.setStatus(1);
                        errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        errorDateMapper.addErrorDate(errorDateDO);
                    }
                    continue;
                }
                //处理
                Integer dataCountFromOriginalCampaigns = advertOriginalDataMapper.getDataCountFromOriginalCampaigns(shop.getShopName(), area.getAdvertCountryCode(), date);
                if (lists.size() <= dataCountFromOriginalCampaigns) {
                    log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的campaigns在" + date + "的数据已存在**********");
                    break;
                } else {
                    advertOriginalDataMapper.deleteDataFromCampaignsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                }
                handleOriginalDataAdvCampaignsReport(lists, shop, area.getAdvertCountryCode(), date);
                break;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                e.printStackTrace();
                errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                try {
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        log.info("***********亚马逊广告api--campaigns定时任务结束**********");
    }

    public void handleOriginalDataAdvCampaignsReport(List<Map<String, Object>> lists, Shop shop, String countryCode, String date)
            throws Exception {
        String nowDate = DateUtil.getDate();
        List<Object> list = new ArrayList<>(lists.size());
        OriginalDataAdvCampaignsReport report = null;
        for (Map<String, Object> map : lists) {
            report = new OriginalDataAdvCampaignsReport();
            report.setBidPlus(MapUtils.getBoolean(map, "bidPlus").equals(true) ? (byte) 1 : (byte) 0);
            report.setShop(shop.getShopName());
            report.setArea(countryCode);
            report.setSellerId(shop.getSellerId());
            report.setPlacement(MapUtils.getString(map, "placement"));
            report.setCampaignStatus(MapUtils.getString(map, "campaignStatus"));
            report.setCampaignBudget(MapUtils.getInteger(map, "campaignBudget"));
            report.setAttributedConversions1d(MapUtils.getInteger(map, "attributedConversions1d"));
            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map, "attributedConversions1dSameSKU"));
            report.setAttributedConversions7d(MapUtils.getInteger(map, "attributedConversions7d"));
            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map, "attributedConversions7dSameSKU"));
            report.setAttributedConversions14d(MapUtils.getInteger(map, "attributedConversions14d"));
            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map, "attributedConversions14dSameSKU"));
            report.setAttributedConversions30d(MapUtils.getInteger(map, "attributedConversions30d"));
            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map, "attributedConversions30dSameSKU"));
            report.setAttributedSales1d(MapUtils.getDouble(map, "attributedSales1d"));
            report.setAttributedSales1dSameSku(MapUtils.getDouble(map, "attributedSales1dSameSKU"));
            report.setAttributedSales7d(MapUtils.getDouble(map, "attributedSales7d"));
            report.setAttributedSales7dSameSku(MapUtils.getDouble(map, "attributedSales7dSameSKU"));
            report.setAttributedSales14d(MapUtils.getDouble(map, "attributedSales14d"));
            report.setAttributedSales14dSameSku(MapUtils.getDouble(map, "attributedSales14dSameSKU"));
            report.setAttributedSales30d(MapUtils.getDouble(map, "attributedSales30d"));
            report.setAttributedSales30dSameSku(MapUtils.getDouble(map, "attributedSales30dSameSKU"));
            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map, "attributedUnitsOrdered1d"));
            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map, "attributedUnitsOrdered7d"));
            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map, "attributedUnitsOrdered14d"));
            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map, "attributedUnitsOrdered30d"));
            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered1dSameSKU"));
            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered7dSameSKU"));
            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered14dSameSKU"));
            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered30dSameSKU"));
            report.setCampaignId(MapUtils.getLong(map, "campaignId"));
            report.setCampaignName(MapUtils.getString(map, "campaignName"));
            report.setClicks(MapUtils.getInteger(map, "clicks"));
            report.setCost(MapUtils.getDouble(map, "cost"));

            report.setDate(date);
            report.setImpressions(MapUtils.getInteger(map, "impressions"));

            report.setCreateTime(nowDate);
            list.add(report);
        }
        baseInsertList(list);

    }


    /**
     * 获取recordType：adGroups；的广告api报告数据
     */
    //@Scheduled(cron = "0 0 20 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvAdGroupsReport() {
        String date = DateUtil.getFetureDate(-1);
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--AdGroups定时任务开始*************");
        //查询授权过的店铺对应区域的配置文件ID
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            //根据基本的配置信息去获取所有的原始数据
            StringBuffer errorMsg = new StringBuffer();
            Map<String, Object> params = new HashMap<String, Object>(2) {{
                put("reportDate", reportDate);//时间格式YYYYMMDD
                put("metrics", "campaignName," +
                        "campaignId," +
                        "adGroupName," +
                        "adGroupId," +
                        "impressions," +
                        "clicks," +
                        "cost," +
                        "attributedConversions1d," +
                        "attributedConversions7d," +
                        "attributedConversions14d," +
                        "attributedConversions30d," +
                        "attributedConversions1dSameSKU," +
                        "attributedConversions7dSameSKU," +
                        "attributedConversions14dSameSKU," +
                        "attributedConversions30dSameSKU," +
                        "attributedUnitsOrdered1d," +
                        "attributedUnitsOrdered7d," +
                        "attributedUnitsOrdered14d," +
                        "attributedUnitsOrdered30d," +
                        "attributedSales1d," +
                        "attributedSales7d," +
                        "attributedSales14d," +
                        "attributedSales30d," +
                        "attributedSales1dSameSKU," +
                        "attributedSales7dSameSKU," +
                        "attributedSales14dSameSKU," +
                        "attributedSales30dSameSKU," +
                        "attributedUnitsOrdered1dSameSKU," +
                        "attributedUnitsOrdered7dSameSKU," +
                        "attributedUnitsOrdered14dSameSKU," +
                        "attributedUnitsOrdered30dSameSKU");
            }};
            Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
            Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
            String sellerId = shop.getSellerId();
            String regionCode = area.getRegionCode();
            RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的AdGroups定时任务开始*************");
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
                try {
                    //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                    List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "adGroups", params);
                    if (null == lists) {
                        errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                        log.warn(errorMsg.toString());
                        Thread.sleep(1000L);
                        //若失败五次，则记录这个错误日期
                        if (errorNum == 4) {
                            ErrorDateDO errorDateDO = new ErrorDateDO();
                            errorDateDO.setSellerId(shop.getSellerId());
                            errorDateDO.setArea(area.getAdvertCountryCode());
                            errorDateDO.setErrorDate(reportDate);
                            errorDateDO.setType(3);
                            errorDateDO.setStatus(1);
                            errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            errorDateMapper.addErrorDate(errorDateDO);
                        }
                        continue;
                    }
                    //处理
                    Integer dataCountFromOriginalAdGroups = advertOriginalDataMapper.getDataCountFromOriginalAdGroups(shop.getShopName(), area.getAdvertCountryCode(), date);
                    if (lists.size() <= dataCountFromOriginalAdGroups) {
                        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的AdGroups在" + date + "的数据已存在**********");
                        break;
                    } else {
                        advertOriginalDataMapper.deleteDataFromAdGroupsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                    }
                    handleOriginalDataAdvAdGroupsReport(lists, shop, area.getAdvertCountryCode(), date);
                    break;

                } catch (Exception e) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    e.printStackTrace();
                    errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                    try {
                        Thread.sleep((long) Math.pow(2, errorNum + 1));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        log.info("***********亚马逊广告api--adGroups定时任务结束**********");
    }

    /**
     * 获取指定时间的AdGroupsReport数据--苏建东
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvAdGroupsReport(String date, ShopAreaProfile shopAreaProfile) {
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--AdGroups定时任务开始*************");
        //根据基本的配置信息去获取所有的原始数据
        StringBuffer errorMsg = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>(2) {{
            put("reportDate", reportDate);//时间格式YYYYMMDD
            put("metrics", "campaignName," +
                    "campaignId," +
                    "adGroupName," +
                    "adGroupId," +
                    "impressions," +
                    "clicks," +
                    "cost," +
                    "attributedConversions1d," +
                    "attributedConversions7d," +
                    "attributedConversions14d," +
                    "attributedConversions30d," +
                    "attributedConversions1dSameSKU," +
                    "attributedConversions7dSameSKU," +
                    "attributedConversions14dSameSKU," +
                    "attributedConversions30dSameSKU," +
                    "attributedUnitsOrdered1d," +
                    "attributedUnitsOrdered7d," +
                    "attributedUnitsOrdered14d," +
                    "attributedUnitsOrdered30d," +
                    "attributedSales1d," +
                    "attributedSales7d," +
                    "attributedSales14d," +
                    "attributedSales30d," +
                    "attributedSales1dSameSKU," +
                    "attributedSales7dSameSKU," +
                    "attributedSales14dSameSKU," +
                    "attributedSales30dSameSKU," +
                    "attributedUnitsOrdered1dSameSKU," +
                    "attributedUnitsOrdered7dSameSKU," +
                    "attributedUnitsOrdered14dSameSKU," +
                    "attributedUnitsOrdered30dSameSKU");
        }};
        Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
        Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
        String sellerId = shop.getSellerId();
        String regionCode = area.getRegionCode();
        RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
        AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的AdGroups定时任务开始*************");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
            try {
                //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "adGroups", params);
                if (null == lists) {
                    errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                    log.warn("profileid:" + shopAreaProfile.getProfileId() + "店铺：" + shop.getShopName() + errorMsg.toString());
                    Thread.sleep(1000L);
                    //若失败五次，则记录这个错误日期
                    if (errorNum == 4) {
                        ErrorDateDO errorDateDO = new ErrorDateDO();
                        errorDateDO.setSellerId(shop.getSellerId());
                        errorDateDO.setArea(area.getAdvertCountryCode());
                        errorDateDO.setErrorDate(reportDate);
                        errorDateDO.setType(3);
                        errorDateDO.setStatus(1);
                        errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        errorDateMapper.addErrorDate(errorDateDO);
                    }
                    continue;
                }
                //处理
                Integer dataCountFromOriginalAdGroups = advertOriginalDataMapper.getDataCountFromOriginalAdGroups(shop.getShopName(), area.getAdvertCountryCode(), date);
                if (lists.size() <= dataCountFromOriginalAdGroups) {
                    log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的AdGroups在" + date + "的数据已存在**********");
                    break;
                } else {
                    advertOriginalDataMapper.deleteDataFromAdGroupsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                }
                handleOriginalDataAdvAdGroupsReport(lists, shop, area.getAdvertCountryCode(), date);
                break;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                e.printStackTrace();
                errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                try {
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        log.info("***********亚马逊广告api--adGroups定时任务结束**********");
    }

    public void handleOriginalDataAdvAdGroupsReport(List<Map<String, Object>> lists, Shop shop, String countryCode, String date)
            throws Exception {
        String nowDate = DateUtil.getDate();
        List<Object> list = new ArrayList<>(lists.size());
        OriginalDataAdvAdgroupsReport report = null;
        for (Map<String, Object> map : lists) {
            report = new OriginalDataAdvAdgroupsReport();
            report.setShop(shop.getShopName());
            report.setArea(countryCode);
            report.setSellerId(shop.getSellerId());
            report.setAttributedConversions1d(MapUtils.getInteger(map, "attributedConversions1d"));
            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map, "attributedConversions1dSameSKU"));
            report.setAttributedConversions7d(MapUtils.getInteger(map, "attributedConversions7d"));
            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map, "attributedConversions7dSameSKU"));
            report.setAttributedConversions14d(MapUtils.getInteger(map, "attributedConversions14d"));
            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map, "attributedConversions14dSameSKU"));
            report.setAttributedConversions30d(MapUtils.getInteger(map, "attributedConversions30d"));
            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map, "attributedConversions30dSameSKU"));
            report.setAttributedSales1d(MapUtils.getDouble(map, "attributedSales1d"));
            report.setAttributedSales1dSameSku(MapUtils.getDouble(map, "attributedSales1dSameSKU"));
            report.setAttributedSales7d(MapUtils.getDouble(map, "attributedSales7d"));
            report.setAttributedSales7dSameSku(MapUtils.getDouble(map, "attributedSales7dSameSKU"));
            report.setAttributedSales14d(MapUtils.getDouble(map, "attributedSales14d"));
            report.setAttributedSales14dSameSku(MapUtils.getDouble(map, "attributedSales14dSameSKU"));
            report.setAttributedSales30d(MapUtils.getDouble(map, "attributedSales30d"));
            report.setAttributedSales30dSameSku(MapUtils.getDouble(map, "attributedSales30dSameSKU"));
            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map, "attributedUnitsOrdered1d"));
            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map, "attributedUnitsOrdered7d"));
            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map, "attributedUnitsOrdered14d"));
            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map, "attributedUnitsOrdered30d"));
            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered1dSameSKU"));
            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered7dSameSKU"));
            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered14dSameSKU"));
            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered30dSameSKU"));
            report.setCampaignId(MapUtils.getLong(map, "campaignId"));
            report.setCampaignName(MapUtils.getString(map, "campaignName"));
            report.setClicks(MapUtils.getInteger(map, "clicks"));
            report.setCost(MapUtils.getDouble(map, "cost"));
            report.setAdGroupId(MapUtils.getLong(map, "adGroupId"));
            report.setAdGroupName(MapUtils.getString(map, "adGroupName"));
            report.setDate(date);
            report.setImpressions(MapUtils.getInteger(map, "impressions"));

            report.setCreateTime(nowDate);
            list.add(report);
        }
        baseInsertList(list);

    }

    /**
     * 获取recordType：targets；segment:query 的广告api报告数据
     */
    //@Scheduled(cron = "0 0 20 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvTargetsReport() {
        String date = DateUtil.getFetureDate(-1);
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--Targets定时任务开始*************");
        //查询授权过的店铺对应区域的配置文件ID
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            //根据基本的配置信息去获取所有的原始数据
            StringBuffer errorMsg = new StringBuffer();
            Map<String, Object> params = new HashMap<String, Object>(2) {{
                put("segment", "query");
                put("reportDate", reportDate);//时间格式YYYYMMDD
                put("metrics", "campaignName," +
                        "campaignId," +
                        "adGroupName," +
                        "adGroupId," +
                        "targetId," +
                        "targetingExpression," +
                        "targetingText," +
                        "targetingType," +
                        "impressions," +
                        "clicks," +
                        "cost," +
                        "attributedConversions1d," +
                        "attributedConversions7d," +
                        "attributedConversions14d," +
                        "attributedConversions30d," +
                        "attributedConversions1dSameSKU," +
                        "attributedConversions7dSameSKU," +
                        "attributedConversions14dSameSKU," +
                        "attributedConversions30dSameSKU," +
                        "attributedUnitsOrdered1d," +
                        "attributedUnitsOrdered7d," +
                        "attributedUnitsOrdered14d," +
                        "attributedUnitsOrdered30d," +
                        "attributedSales1d," +
                        "attributedSales7d," +
                        "attributedSales14d," +
                        "attributedSales30d," +
                        "attributedSales1dSameSKU," +
                        "attributedSales7dSameSKU," +
                        "attributedSales14dSameSKU," +
                        "attributedSales30dSameSKU," +
                        "attributedUnitsOrdered1dSameSKU," +
                        "attributedUnitsOrdered7dSameSKU," +
                        "attributedUnitsOrdered14dSameSKU," +
                        "attributedUnitsOrdered30dSameSKU");
            }};
            Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
            Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
            String sellerId = shop.getSellerId();
            String regionCode = area.getRegionCode();
            RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的targets定时任务开始*************");
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
                try {
                    //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                    List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "targets", params);
                    if (null == lists) {
                        errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                        log.warn(errorMsg.toString());
                        Thread.sleep(1000L);
                        //若失败五次，则记录这个错误日期
                        if (errorNum == 4) {
                            ErrorDateDO errorDateDO = new ErrorDateDO();
                            errorDateDO.setSellerId(shop.getSellerId());
                            errorDateDO.setArea(area.getAdvertCountryCode());
                            errorDateDO.setErrorDate(reportDate);
                            errorDateDO.setType(4);
                            errorDateDO.setStatus(1);
                            errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            errorDateMapper.addErrorDate(errorDateDO);
                        }
                        continue;
                    }
                    //处理
                    Integer dataCountFromOriginalTargets = advertOriginalDataMapper.getDataCountFromOriginalTargets(shop.getShopName(), area.getAdvertCountryCode(), date);
                    if (lists.size() == dataCountFromOriginalTargets) {
                        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的targets在" + date + "的数据已存在**********");
                        break;
                    } else {
                        advertOriginalDataMapper.deleteDataFromTargetsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                    }
                    handleOriginalDataAdvTargetsReport(lists, shop, area.getAdvertCountryCode(), date);
                    break;
                } catch (Exception e) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    e.printStackTrace();
                    errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                    try {
                        Thread.sleep((long) Math.pow(2, errorNum + 1));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        log.info("***********亚马逊广告api--targets定时任务结束**********");
    }

    /**
     * 获取指定时间的TargetsReport数据--苏建东
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvTargetsReport(String date, ShopAreaProfile shopAreaProfile) {
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--Targets定时任务开始*************");
        //根据基本的配置信息去获取所有的原始数据
        StringBuffer errorMsg = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>(3) {{
            put("segment", "query");
            put("reportDate", reportDate);//时间格式YYYYMMDD
            put("metrics", "campaignName," +
                    "campaignId," +
                    "adGroupName," +
                    "adGroupId," +
                    "targetId," +
                    "targetingExpression," +
                    "targetingText," +
                    "targetingType," +
                    "impressions," +
                    "clicks," +
                    "cost," +
                    "attributedConversions1d," +
                    "attributedConversions7d," +
                    "attributedConversions14d," +
                    "attributedConversions30d," +
                    "attributedConversions1dSameSKU," +
                    "attributedConversions7dSameSKU," +
                    "attributedConversions14dSameSKU," +
                    "attributedConversions30dSameSKU," +
                    "attributedUnitsOrdered1d," +
                    "attributedUnitsOrdered7d," +
                    "attributedUnitsOrdered14d," +
                    "attributedUnitsOrdered30d," +
                    "attributedSales1d," +
                    "attributedSales7d," +
                    "attributedSales14d," +
                    "attributedSales30d," +
                    "attributedSales1dSameSKU," +
                    "attributedSales7dSameSKU," +
                    "attributedSales14dSameSKU," +
                    "attributedSales30dSameSKU," +
                    "attributedUnitsOrdered1dSameSKU," +
                    "attributedUnitsOrdered7dSameSKU," +
                    "attributedUnitsOrdered14dSameSKU," +
                    "attributedUnitsOrdered30dSameSKU");
        }};
        Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
        Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
        String sellerId = shop.getSellerId();
        String regionCode = area.getRegionCode();
        RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
        AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的targets定时任务开始*************");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
            try {
                //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "targets", params);
                if (null == lists) {
                    errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                    log.warn("profileid:" + shopAreaProfile.getProfileId() + "店铺：" + shop.getShopName() + errorMsg.toString());
                    Thread.sleep(1000L);
                    //若失败五次，则记录这个错误日期
                    if (errorNum == 4) {
                        ErrorDateDO errorDateDO = new ErrorDateDO();
                        errorDateDO.setSellerId(shop.getSellerId());
                        errorDateDO.setArea(area.getAdvertCountryCode());
                        errorDateDO.setErrorDate(reportDate);
                        errorDateDO.setType(4);
                        errorDateDO.setStatus(1);
                        errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        errorDateMapper.addErrorDate(errorDateDO);
                    }
                    continue;
                }
                //处理
                Integer dataCountFromOriginalTargets = advertOriginalDataMapper.getDataCountFromOriginalTargets(shop.getShopName(), area.getAdvertCountryCode(), date);
                if (lists.size() == dataCountFromOriginalTargets) {
                    log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的targets在" + date + "的数据已存在**********");
                    break;
                } else {
                    advertOriginalDataMapper.deleteDataFromTargetsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                }
                handleOriginalDataAdvTargetsReport(lists, shop, area.getAdvertCountryCode(), date);
                break;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                e.printStackTrace();
                errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                try {
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        log.info("***********亚马逊广告api--targets定时任务结束**********");
    }

    public void handleOriginalDataAdvTargetsReport(List<Map<String, Object>> lists, Shop shop, String countryCode, String date)
            throws Exception {
        String nowDate = DateUtil.getDate();
        List<Object> list = new ArrayList<>(lists.size());
        OriginalDataAdvTargetsReport report = null;
        for (Map<String, Object> map : lists) {
            report = new OriginalDataAdvTargetsReport();
            report.setShop(shop.getShopName());
            report.setArea(countryCode);
            report.setSellerId(shop.getSellerId());
            report.setAttributedConversions1d(MapUtils.getInteger(map, "attributedConversions1d"));
            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map, "attributedConversions1dSameSKU"));
            report.setAttributedConversions7d(MapUtils.getInteger(map, "attributedConversions7d"));
            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map, "attributedConversions7dSameSKU"));
            report.setAttributedConversions14d(MapUtils.getInteger(map, "attributedConversions14d"));
            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map, "attributedConversions14dSameSKU"));
            report.setAttributedConversions30d(MapUtils.getInteger(map, "attributedConversions30d"));
            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map, "attributedConversions30dSameSKU"));
            report.setAttributedSales1d(MapUtils.getDouble(map, "attributedSales1d"));
            report.setAttributedSales1dSameSku(MapUtils.getDouble(map, "attributedSales1dSameSKU"));
            report.setAttributedSales7d(MapUtils.getDouble(map, "attributedSales7d"));
            report.setAttributedSales7dSameSku(MapUtils.getDouble(map, "attributedSales7dSameSKU"));
            report.setAttributedSales14d(MapUtils.getDouble(map, "attributedSales14d"));
            report.setAttributedSales14dSameSku(MapUtils.getDouble(map, "attributedSales14dSameSKU"));
            report.setAttributedSales30d(MapUtils.getDouble(map, "attributedSales30d"));
            report.setAttributedSales30dSameSku(MapUtils.getDouble(map, "attributedSales30dSameSKU"));
            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map, "attributedUnitsOrdered1d"));
            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map, "attributedUnitsOrdered7d"));
            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map, "attributedUnitsOrdered14d"));
            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map, "attributedUnitsOrdered30d"));
            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered1dSameSKU"));
            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered7dSameSKU"));
            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered14dSameSKU"));
            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered30dSameSKU"));
            report.setCampaignId(MapUtils.getLong(map, "campaignId"));
            report.setCampaignName(MapUtils.getString(map, "campaignName"));
            report.setClicks(MapUtils.getInteger(map, "clicks"));
            report.setCost(MapUtils.getDouble(map, "cost"));
            report.setAdGroupId(MapUtils.getLong(map, "adGroupId"));
            report.setAdGroupName(MapUtils.getString(map, "adGroupName"));
            report.setDate(date);
            report.setImpressions(MapUtils.getInteger(map, "impressions"));
            report.setQuery(MapUtils.getString(map, "query"));
            report.setTargetId(MapUtils.getLong(map, "targetId"));
            report.setTargetingExpression(MapUtils.getString(map, "targetingExpression"));
            report.setTargetingText(MapUtils.getString(map, "targetingText"));
            report.setTargetingType(MapUtils.getString(map, "targetingType"));
            report.setCreateTime(nowDate);
            list.add(report);
        }
        baseInsertList(list);

    }

    /**
     * 获取recordType：keywords；segment:query 的广告api报告数据
     */
    //@Scheduled(cron = "0 0 20 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvKeywordsReport() {
        String date = DateUtil.getFetureDate(-1);
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--Keywords定时任务开始*************");
        //查询授权过的店铺对应区域的配置文件ID
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            //根据基本的配置信息去获取所有的原始数据
            StringBuffer errorMsg = new StringBuffer();
            Map<String, Object> params = new HashMap<String, Object>(3) {{
                put("segment", "query");
                put("reportDate", reportDate);//时间格式YYYYMMDD
                put("metrics", "campaignName," +
                        "campaignId," +
                        "adGroupName," +
                        "adGroupId," +
                        "keywordId," +
                        "keywordText," +
                        "matchType," +
                        "impressions," +
                        "clicks," +
                        "cost," +
                        "attributedConversions1d," +
                        "attributedConversions7d," +
                        "attributedConversions14d," +
                        "attributedConversions30d," +
                        "attributedConversions1dSameSKU," +
                        "attributedConversions7dSameSKU," +
                        "attributedConversions14dSameSKU," +
                        "attributedConversions30dSameSKU," +
                        "attributedUnitsOrdered1d," +
                        "attributedUnitsOrdered7d," +
                        "attributedUnitsOrdered14d," +
                        "attributedUnitsOrdered30d," +
                        "attributedSales1d," +
                        "attributedSales7d," +
                        "attributedSales14d," +
                        "attributedSales30d," +
                        "attributedSales1dSameSKU," +
                        "attributedSales7dSameSKU," +
                        "attributedSales14dSameSKU," +
                        "attributedSales30dSameSKU," +
                        "attributedUnitsOrdered1dSameSKU," +
                        "attributedUnitsOrdered7dSameSKU," +
                        "attributedUnitsOrdered14dSameSKU," +
                        "attributedUnitsOrdered30dSameSKU");
            }};
            Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
            Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
            String sellerId = shop.getSellerId();
            String regionCode = area.getRegionCode();
            RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的keywords定时任务开始*************");
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
                try {
                    //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                    List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "keywords", params);
                    if (null == lists) {
                        errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                        log.warn(errorMsg.toString());
                        Thread.sleep(1000L);
                        //若失败五次，则记录这个错误日期
                        if (errorNum == 4) {
                            ErrorDateDO errorDateDO = new ErrorDateDO();
                            errorDateDO.setSellerId(shop.getSellerId());
                            errorDateDO.setArea(area.getAdvertCountryCode());
                            errorDateDO.setErrorDate(reportDate);
                            errorDateDO.setType(5);
                            errorDateDO.setStatus(1);
                            errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            errorDateMapper.addErrorDate(errorDateDO);
                        }
                        continue;
                    }
                    //处理
                    Integer dataCountFromOriginalKeywords = advertOriginalDataMapper.getDataCountFromOriginalKeywords(shop.getShopName(), area.getAdvertCountryCode(), date);
                    if (lists.size() == dataCountFromOriginalKeywords) {
                        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的keywords在" + date + "的数据已存在**********");
                        break;
                    } else {
                        advertOriginalDataMapper.deleteDataFromKeywordsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                    }
                    handleOriginalDataAdvKeywordsReport(lists, shop, area.getAdvertCountryCode(), date);
                    break;
                } catch (Exception e) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    e.printStackTrace();
                    errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                    try {
                        Thread.sleep((long) Math.pow(2, errorNum + 1));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        log.info("***********亚马逊广告api--keywords定时任务结束**********");
    }

    /**
     * 获取指定时间的KeywordsReport数据--苏建东
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvKeywordsReport(String date, ShopAreaProfile shopAreaProfile) {
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--Keywords定时任务开始*************");
        //根据基本的配置信息去获取所有的原始数据
        StringBuffer errorMsg = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>(3) {{
            put("segment", "query");
            put("reportDate", reportDate);//时间格式YYYYMMDD
            put("metrics", "campaignName," +
                    "campaignId," +
                    "adGroupName," +
                    "adGroupId," +
                    "keywordId," +
                    "keywordText," +
                    "matchType," +
                    "impressions," +
                    "clicks," +
                    "cost," +
                    "attributedConversions1d," +
                    "attributedConversions7d," +
                    "attributedConversions14d," +
                    "attributedConversions30d," +
                    "attributedConversions1dSameSKU," +
                    "attributedConversions7dSameSKU," +
                    "attributedConversions14dSameSKU," +
                    "attributedConversions30dSameSKU," +
                    "attributedUnitsOrdered1d," +
                    "attributedUnitsOrdered7d," +
                    "attributedUnitsOrdered14d," +
                    "attributedUnitsOrdered30d," +
                    "attributedSales1d," +
                    "attributedSales7d," +
                    "attributedSales14d," +
                    "attributedSales30d," +
                    "attributedSales1dSameSKU," +
                    "attributedSales7dSameSKU," +
                    "attributedSales14dSameSKU," +
                    "attributedSales30dSameSKU," +
                    "attributedUnitsOrdered1dSameSKU," +
                    "attributedUnitsOrdered7dSameSKU," +
                    "attributedUnitsOrdered14dSameSKU," +
                    "attributedUnitsOrdered30dSameSKU");
        }};
        Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
        Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
        String sellerId = shop.getSellerId();
        String regionCode = area.getRegionCode();
        RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
        AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的keywords定时任务开始*************");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
            try {
                //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "keywords", params);
                if (null == lists) {
                    errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                    log.warn("profileid:" + shopAreaProfile.getProfileId() + "店铺：" + shop.getShopName() + errorMsg.toString());
                    Thread.sleep(1000L);
                    //若失败五次，则记录这个错误日期
                    if (errorNum == 4) {
                        ErrorDateDO errorDateDO = new ErrorDateDO();
                        errorDateDO.setSellerId(shop.getSellerId());
                        errorDateDO.setArea(area.getAdvertCountryCode());
                        errorDateDO.setErrorDate(reportDate);
                        errorDateDO.setType(5);
                        errorDateDO.setStatus(1);
                        errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        errorDateMapper.addErrorDate(errorDateDO);
                    }
                    continue;
                }
                //处理
                Integer dataCountFromOriginalKeywords = advertOriginalDataMapper.getDataCountFromOriginalKeywords(shop.getShopName(), area.getAdvertCountryCode(), date);
                if (lists.size() == dataCountFromOriginalKeywords) {
                    log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的keywords在" + date + "的数据已存在**********");
                    break;
                } else {
                    advertOriginalDataMapper.deleteDataFromKeywordsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                }
                handleOriginalDataAdvKeywordsReport(lists, shop, area.getAdvertCountryCode(), date);
                break;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                e.printStackTrace();
                errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                try {
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        log.info("***********亚马逊广告api--keywords定时任务结束**********");
    }

    public void handleOriginalDataAdvKeywordsReport(List<Map<String, Object>> lists, Shop shop, String countryCode, String date)
            throws Exception {
        String nowDate = DateUtil.getDate();
        List<Object> list = new ArrayList<>(lists.size());
        OriginalDataAdvKeywordsReport report = null;
        for (Map<String, Object> map : lists) {
            report = new OriginalDataAdvKeywordsReport();
            report.setShop(shop.getShopName());
            report.setArea(countryCode);
            report.setSellerId(shop.getSellerId());
            report.setAttributedConversions1d(MapUtils.getInteger(map, "attributedConversions1d"));
            report.setAttributedConversions1dSameSku(MapUtils.getInteger(map, "attributedConversions1dSameSKU"));
            report.setAttributedConversions7d(MapUtils.getInteger(map, "attributedConversions7d"));
            report.setAttributedConversions7dSameSku(MapUtils.getInteger(map, "attributedConversions7dSameSKU"));
            report.setAttributedConversions14d(MapUtils.getInteger(map, "attributedConversions14d"));
            report.setAttributedConversions14dSameSku(MapUtils.getInteger(map, "attributedConversions14dSameSKU"));
            report.setAttributedConversions30d(MapUtils.getInteger(map, "attributedConversions30d"));
            report.setAttributedConversions30dSameSku(MapUtils.getInteger(map, "attributedConversions30dSameSKU"));
            report.setAttributedSales1d(MapUtils.getDouble(map, "attributedSales1d"));
            report.setAttributedSales1dSameSku(MapUtils.getDouble(map, "attributedSales1dSameSKU"));
            report.setAttributedSales7d(MapUtils.getDouble(map, "attributedSales7d"));
            report.setAttributedSales7dSameSku(MapUtils.getDouble(map, "attributedSales7dSameSKU"));
            report.setAttributedSales14d(MapUtils.getDouble(map, "attributedSales14d"));
            report.setAttributedSales14dSameSku(MapUtils.getDouble(map, "attributedSales14dSameSKU"));
            report.setAttributedSales30d(MapUtils.getDouble(map, "attributedSales30d"));
            report.setAttributedSales30dSameSku(MapUtils.getDouble(map, "attributedSales30dSameSKU"));
            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map, "attributedUnitsOrdered1d"));
            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map, "attributedUnitsOrdered7d"));
            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map, "attributedUnitsOrdered14d"));
            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map, "attributedUnitsOrdered30d"));
            report.setAttributedUnitsOrdered1dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered1dSameSKU"));
            report.setAttributedUnitsOrdered7dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered7dSameSKU"));
            report.setAttributedUnitsOrdered14dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered14dSameSKU"));
            report.setAttributedUnitsOrdered30dSameSku(MapUtils.getInteger(map, "attributedUnitsOrdered30dSameSKU"));
            report.setCampaignId(MapUtils.getLong(map, "campaignId"));
            report.setCampaignName(MapUtils.getString(map, "campaignName"));
            report.setClicks(MapUtils.getInteger(map, "clicks"));
            report.setCost(MapUtils.getDouble(map, "cost"));
            report.setAdGroupId(MapUtils.getLong(map, "adGroupId"));
            report.setAdGroupName(MapUtils.getString(map, "adGroupName"));
            report.setDate(date);
            report.setImpressions(MapUtils.getInteger(map, "impressions"));
            report.setQuery(MapUtils.getString(map, "query"));
            report.setKeywordId(MapUtils.getLong(map, "keywordId"));
            report.setKeywordText(MapUtils.getString(map, "keywordText"));
            report.setMatchType(MapUtils.getString(map, "matchType"));
            report.setCreateTime(nowDate);
            list.add(report);
        }
        baseInsertList(list);

    }


    /**
     * 获取recordType：keywords；segment:query 的广告api报告数据
     */
    //@Scheduled(cron = "0 0 20 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvAsinsReport() {
        String date = DateUtil.getFetureDate(-1);
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--asins定时任务开始*************");
        //查询授权过的店铺对应区域的配置文件ID
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            //根据基本的配置信息去获取所有的原始数据
            StringBuffer errorMsg = new StringBuffer();
            Map<String, Object> params = new HashMap<String, Object>(3) {{
                put("reportDate", reportDate);//时间格式YYYYMMDD
                put("campaignType", "sponsoredProducts");
                put("metrics", "campaignName," +
                        "campaignId," +
                        "adGroupName," +
                        "adGroupId," +
                        "keywordId," +
                        "keywordText," +
                        "asin," +
                        "otherAsin," +
                        "sku," +
                        "currency," +
                        "matchType," +
                        "attributedUnitsOrdered1d," +
                        "attributedUnitsOrdered7d," +
                        "attributedUnitsOrdered14d," +
                        "attributedUnitsOrdered30d," +
                        "attributedUnitsOrdered1dOtherSKU," +
                        "attributedUnitsOrdered7dOtherSKU," +
                        "attributedUnitsOrdered14dOtherSKU," +
                        "attributedUnitsOrdered30dOtherSKU," +
                        "attributedSales1dOtherSKU," +
                        "attributedSales7dOtherSKU," +
                        "attributedSales14dOtherSKU," +
                        "attributedSales30dOtherSKU");
            }};
            Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
            Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
            String sellerId = shop.getSellerId();
            String regionCode = area.getRegionCode();
            RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
            AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
            log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的asins定时任务开始*************");
            //创建事务还原点
            Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
                try {
                    //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                    List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "asins", params);
                    if (null == lists) {
                        errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                        log.warn(errorMsg.toString());
                        Thread.sleep(1000L);
                        //若失败五次，则记录这个错误日期
                        if (errorNum == 4) {
                            ErrorDateDO errorDateDO = new ErrorDateDO();
                            errorDateDO.setSellerId(shop.getSellerId());
                            errorDateDO.setArea(area.getAdvertCountryCode());
                            errorDateDO.setErrorDate(reportDate);
                            errorDateDO.setType(6);
                            errorDateDO.setStatus(1);
                            errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            errorDateMapper.addErrorDate(errorDateDO);
                        }
                        continue;
                    }
                    //处理
                    Integer dataCountFromOriginalAsins = advertOriginalDataMapper.getDataCountFromOriginalAsins(shop.getShopName(), area.getAdvertCountryCode(), date);
                    if (lists.size() == dataCountFromOriginalAsins) {
                        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的asins在" + date + "的数据已存在**********");
                        break;
                    } else {
                        advertOriginalDataMapper.deleteDataFromAsinsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                    }
                    handleOriginalDataAdvAsinsReport(lists, shop, area.getAdvertCountryCode(), date);
                    break;
                } catch (Exception e) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    e.printStackTrace();
                    errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                    try {
                        Thread.sleep((long) Math.pow(2, errorNum + 1));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        log.info("***********亚马逊广告api--asins定时任务结束**********");
    }

    /**
     * 获取指定时间的AdvAsinsReport数据--苏建东
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOriginalDataAdvAsinsReport(String date, ShopAreaProfile shopAreaProfile) {
        String reportDate = date.replace("-", "");
        log.info("***********亚马逊广告api--productAds定时任务开始*************");
        //根据基本的配置信息去获取所有的原始数据
        StringBuffer errorMsg = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>(3) {{
            put("reportDate", reportDate);//时间格式YYYYMMDD
            put("campaignType", "sponsoredProducts");
            put("metrics", "campaignName," +
                    "campaignId," +
                    "adGroupName," +
                    "adGroupId," +
                    "keywordId," +
                    "keywordText," +
                    "asin," +
                    "otherAsin," +
                    "sku," +
                    "currency," +
                    "matchType," +
                    "attributedUnitsOrdered1d," +
                    "attributedUnitsOrdered7d," +
                    "attributedUnitsOrdered14d," +
                    "attributedUnitsOrdered30d," +
                    "attributedUnitsOrdered1dOtherSKU," +
                    "attributedUnitsOrdered7dOtherSKU," +
                    "attributedUnitsOrdered14dOtherSKU," +
                    "attributedUnitsOrdered30dOtherSKU," +
                    "attributedSales1dOtherSKU," +
                    "attributedSales7dOtherSKU," +
                    "attributedSales14dOtherSKU," +
                    "attributedSales30dOtherSKU");
        }};
        Area area = areaMapper.getOneInfoByAreaId(shopAreaProfile.getAreaId());
        Shop shop = shopMapper.getOneInfoByShopId(shopAreaProfile.getShopId());
        String sellerId = shop.getSellerId();
        String regionCode = area.getRegionCode();
        RefreshTokenDO refreshTokenDO = refreshTokenMapper.selectRefreshTokenBySellerIdAndRegionCode(sellerId, regionCode);
        AmazonAdvertApiUtil.setAccessTokenToRedis(refreshTokenDO);
        log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的asins定时任务开始*************");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (int errorNum = 0; errorNum < MAX_ERROR_NUM; errorNum++) {
            try {
                //将配置文件ID，商店的sellerID和需要拼接的URL字段和参数传入获取报表数据接口
                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(shopAreaProfile, CryptoUtil.decode(shop.getSellerId()), "sp", "asins", params);
                if (null == lists) {
                    errorMsg.append("第").append(errorNum + 1).append("次，获取数据失败");
                    log.warn("profileid:" + shopAreaProfile.getProfileId() + "店铺：" + shop.getShopName() + errorMsg.toString());
                    Thread.sleep(1000L);
                    //若失败五次，则记录这个错误日期
                    if (errorNum == 4) {
                        ErrorDateDO errorDateDO = new ErrorDateDO();
                        errorDateDO.setSellerId(shop.getSellerId());
                        errorDateDO.setArea(area.getAdvertCountryCode());
                        errorDateDO.setErrorDate(reportDate);
                        errorDateDO.setType(6);
                        errorDateDO.setStatus(1);
                        errorDateDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        errorDateMapper.addErrorDate(errorDateDO);
                    }
                    continue;
                }
                //处理
                Integer dataCountFromOriginalAsins = advertOriginalDataMapper.getDataCountFromOriginalAsins(shop.getShopName(), area.getAdvertCountryCode(), date);
                if (lists.size() == dataCountFromOriginalAsins) {
                    log.info("***********" + shop.getShopName() + "的" + area.getAreaName() + "的asins在" + date + "的数据已存在**********");
                    break;
                } else {
                    advertOriginalDataMapper.deleteDataFromAsinsByShopAreaDate(shop.getShopName(), area.getAdvertCountryCode(), date);
                }
                handleOriginalDataAdvAsinsReport(lists, shop, area.getAdvertCountryCode(), date);
                break;
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                e.printStackTrace();
                errorMsg.append("第").append(errorNum + 1).append("次出错,错误详情:").append(e);
                try {
                    Thread.sleep((long) Math.pow(2, errorNum + 1));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        log.info("***********亚马逊广告api--asins定时任务结束**********");
    }

    public void handleOriginalDataAdvAsinsReport(List<Map<String, Object>> lists, Shop shop, String countryCode, String date)
            throws Exception {
        String nowDate = DateUtil.getDate();
        List<Object> list = new ArrayList<>(lists.size());
        OriginalDataAdvAsinsReport report = null;
        for (Map<String, Object> map : lists) {
            report = new OriginalDataAdvAsinsReport();
            report.setShop(shop.getShopName());
            report.setArea(countryCode);
            report.setSellerId(shop.getSellerId());
            report.setAttributedSales1dOtherSku(MapUtils.getDouble(map, "attributedSales1dOtherSKU"));
            report.setAttributedSales7dOtherSku(MapUtils.getDouble(map, "attributedSales7dOtherSKU"));
            report.setAttributedSales14dOtherSku(MapUtils.getDouble(map, "attributedSales14dOtherSKU"));
            report.setAttributedSales30dOtherSku(MapUtils.getDouble(map, "attributedSales30dOtherSKU"));
            report.setAttributedUnitsOrdered1d(MapUtils.getInteger(map, "attributedUnitsOrdered1d"));
            report.setAttributedUnitsOrdered7d(MapUtils.getInteger(map, "attributedUnitsOrdered7d"));
            report.setAttributedUnitsOrdered14d(MapUtils.getInteger(map, "attributedUnitsOrdered14d"));
            report.setAttributedUnitsOrdered30d(MapUtils.getInteger(map, "attributedUnitsOrdered30d"));
            report.setAttributedUnitsOrdered1dOtherSku(MapUtils.getInteger(map, "attributedUnitsOrdered1dOtherSKU"));
            report.setAttributedUnitsOrdered7dOtherSku(MapUtils.getInteger(map, "attributedUnitsOrdered7dOtherSKU"));
            report.setAttributedUnitsOrdered14dOtherSku(MapUtils.getInteger(map, "attributedUnitsOrdered14dOtherSKU"));
            report.setAttributedUnitsOrdered30dOtherSku(MapUtils.getInteger(map, "attributedUnitsOrdered30dOtherSKU"));
            report.setCampaignId(MapUtils.getLong(map, "campaignId"));
            report.setCampaignName(MapUtils.getString(map, "campaignName"));

            report.setAdGroupId(MapUtils.getLong(map, "adGroupId"));
            report.setAdGroupName(MapUtils.getString(map, "adGroupName"));
            report.setDate(date);

            report.setKeywordId(MapUtils.getLong(map, "keywordId"));
            report.setKeywordText(MapUtils.getString(map, "keywordText"));
            report.setMatchType(MapUtils.getString(map, "matchType"));
            report.setCreateTime(nowDate);
            report.setAsin(MapUtils.getString(map, "asin"));
            report.setOtherAsin(MapUtils.getString(map, "otherAsin"));
            report.setSku(MapUtils.getString(map, "sku"));
            report.setCurrency(MapUtils.getString(map, "currency"));
            list.add(report);
        }
        baseInsertList(list);

    }

    /**
     * 打印出错信息
     *
     * @param errorMsg
     */
    private void logWarnErrorMsg(String errorMsg, String shopName) {
        log.warn("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓定时任务出错↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        log.warn("×××××亚马逊广告api×××定时任务出错×××" +
                "×××××错误详情:" + errorMsg);
        log.warn("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑定时任务出错↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setShop(shopName);
        record.setArea("所有旗下国家代码");
        String date = DateUtil.getFetureDate(0) + " 08:00:00";
        record.setDate(date);
        record.setName("亚马逊广告api定时任务");
        //是否处理，0:未处理,1:已处理',
        record.setIsHandle(0);
        //类型:亚马逊广告api定时任务
        record.setType(2);
        record.setRemark("亚马逊广告api定时任务出错,任务名称:"
                + "出错的时间:" + date
                + "错误详情:" + errorMsg);
        record.setCreateTime(DateUtil.getFutureDateTime(0));
        baseInsert(record);
    }
}
