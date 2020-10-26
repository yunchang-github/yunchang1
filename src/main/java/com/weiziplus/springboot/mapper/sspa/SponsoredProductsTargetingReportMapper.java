package com.weiziplus.springboot.mapper.sspa;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/5 9:46
 */
@Mapper
public interface SponsoredProductsTargetingReportMapper {

    /**
     * 获取关键字列表
     *
     * @return
     */
    List<Map<String, Object>> getKeyWordPerspectivePageList(Map map);

    /**
     * 获取关键字列表
     *
     * @return
     */
    List<Map<String, Object>> getKeyWordPerspectiveList(Map map);

    /**
     * 获取关键字总数据
     *
     * @return
     */
    Map<String, Object> getKeyWordPerspectiveSumData();

    /**
     * 获取campaign_name
     * @param shopArea
     * @param sellerId
     * @param adGroupNameList
     * @return
     */
    List<String> getCampaignName(String sellerId,String shopArea,List<String> adGroupNameList);

    /**
     * 获取ad_group_name
     * @param shopArea
     * @param sellerId
     * @return
     */
    List<String> getAdGroupName(String sellerId,String shopArea,List<String> campaignList);
}
