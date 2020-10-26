package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.VO.searchWord.SearchWordDateVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/5 11:17
 */
@Mapper
public interface SponsoredProductsSearchTermReportMapper {
    /**
     * 获取搜索词列表
     *
     * @return
     */
    List<Map<String, Object>> getSearchPerspectivePageList(@Param("date") String date, @Param("campaignName") String campaignName, @Param("adGroupName") String adGroupName);

    /**
     * 获取搜索词列表
     *
     * @return
     */
    List<Map<String, Object>> getSearchPerspectiveList(Map map);

    /**
     * 获取全部的搜索词列表根据店铺及区域
     * @param map
     * @return
     */
    List<Map<String,Object>> getAllSearchPerspectiveList(Map map);

    /**
     * 获取搜索词排序列表，根据SortName
     * @param map
     * @return
     */
    List<Map<String,Object>> getSearchPerspectiveListBySortName(Map map);

    /**
     * 获取搜索词表中的最大日期，精确到周
     * @return
     */
    String getMaxTimeByKeywordsReport();

    /**
     * 获取搜索词表中的最小日期，精确到周
     * @return
     */
    String getMinTimeByKeywordsReport();


    /**
     * 获取搜索词合计
     *
     * @return
     */
    Map<String, Object> getSearchPerspectiveSumData();

    /**
     * 获取campaign_name
     * @param sellerId
     * @param shopArea
     * @param adGroupNameList
     * @return
     */
    List<String> getCampaignName(String sellerId,String shopArea,List<String> adGroupNameList);

    /**
     * 获取ad_group_name
     * @param sellerId
     * @param shopArea
     * @param campaignNameList
     * @return
     */
    List<String> getAdGroupName(String sellerId,String shopArea,List<String> campaignNameList);

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop,@Param("area") String area);
}
