package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.SponsoredProductsAdvertisedProductReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/2 8:35
 */
@Mapper
public interface SponsoredProductsAdvertisedProductReportMapper {
    /**
     * 根据年份获取列表数据
     *
     * @param year
     * @return
     */
    SponsoredProductsAdvertisedProductReport getListDataByYear(@Param("year") String year);

    /**
     * 根据月份获取列表数据
     *
     * @param year
     * @param month
     * @return
     */
    SponsoredProductsAdvertisedProductReport getListDataByYearAndMonth(@Param("year") String year, @Param("month") String month);

    /**
     * 获取Campaign表现透视列表数据
     * @return
     */
    List<Map<String, Object>> getCampaignPerspectivePageList(Map map);

    /**
     * 获取Campaign表现透视列表数据加第一周展现量排序
     * @param map 条件筛选参数与其他参数
     * @return
     */
    List<Map<String, Object>> getCampaignPerspectivePageSortList(Map map);

    /**
     * 获取Campaign表现透视列表数据---echarts不分页
     *
     * @return
     */
    List<Map<String, Object>> getCampaignPerspectiveList(Map map);

    /**
     * 获取Advertised ASIN
     *
     * @return
     */
    List<String> getAdvertisedAsin(String sellerId,String shopArea);

    /**
     * 获取campaign_name
     *
     * @return
     */
    List<String> getCampaignName(Map map);

    /**
     * 获取ad_group_name
     *
     * @return
     */
    List<String> getAdGroupName();

    /**
     * 获取ASIN表现透视列表数据
     *
     * @param month
     * @param date
     * @param campaignName
     * @param adGroupName
     * @param asin
     * @return
     */
    List<Map<String, Object>> getAsinPerspectivePageList(@Param("month") String month, @Param("date") String date, @Param("campaignName") String campaignName, @Param("adGroupName") String adGroupName, @Param("asin") String asin);


    /**
     * 获取ASIN表现透视列表数据
     *
     * @param month
     * @param date
     * @param campaignName
     * @param adGroupName
     * @param asin
     * @return
     */
    List<Map<String, Object>> getAsinPerspectiveList(Map map);

    /**
     * 获取日期列表
     *
     * @return
     */
    List<String> getYearMonthList();

    /**
     * 过去四周列表数据
     *
     * @return
     */
    Map<String, Object> getPastFourWeekData();

    /**
     * 获取Campaign表现透视对比列表数据
     *
     * @return
     */
    List<Map<String, Object>> getCampaignPerspectiveContrastList(@Param("monday") String monday);

    /**
     * 获取周一列表
     *
     * @return
     */
    List<Map<String, Object>> getMondayList();

    /**
     * 按时间获取周曝光量
     * @param paraMap
     * @return
     */
	int getAdvertisedProductReportExposureByDate(@Param("param") Map<String, Object> paraMap);


	/**
     * 按时间获取周点击量
     * @param paraMap
     * @return
     */
	int getAdvertisedProductReportClickByDate(@Param("param") Map<String, Object> paraMap);

	/**
	 * 按时间获取周广告费
	 * @param paraMap
	 * @return
	 */
	Double getAdvertisedProductReportSpendByDate(@Param("param") Map<String, Object> paraMap);

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop,@Param("area") String area);

    /**
     * 原始数据中获取时间productAdv
     * @param shop
     * @param area
     * @return
     */

    String getLatestDayFromOriginalProductAds(@Param("shop")String shop, @Param("area")String area);

    String getLatestDayFromOriginalCampaigns(@Param("shop") String shop, @Param("area") String area);

    String getLatestDayFromOriginalAdGroups(@Param("shop") String shop, @Param("area") String area);

    String getLatestDayFromOriginalTargets(@Param("shop") String shop, @Param("area") String area);

    String getLatestDayFromOriginalKeywords(@Param("shop") String shop, @Param("area") String area);

    String getLatestDayFromOriginalAsins(@Param("shop") String shop, @Param("area") String area);
}
