package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.SponsoredProductsPlacementReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/1 17:41
 */
@Mapper
public interface SponsoredProductsPlacementReportMapper {
    /**
     * 根据年份获取列表数据
     *
     * @param year
     * @return
     */
    SponsoredProductsPlacementReport getListDataByYear(@Param("year") String year);

    /**
     * 根据月份获取列表数据
     *
     * @param year
     * @param month
     * @return
     */
    SponsoredProductsPlacementReport getListDataByYearAndMonth(@Param("year") String year, @Param("month") String month);

    /**
     * 根据日期分组获取列表
     *
     * @return
     */
    List<SponsoredProductsPlacementReport> getListGroupByDate();

    /**
     * 获取位置透视列表
     *
     * @return
     */
    List<Map<String,Object>> getPlacePerspectivePageList(@Param("date") String date);

    /**
     * 获取位置透视列表
     *
     * @return
     */
    List<Map<String,Object>> getPlacePerspectiveList(Map map);

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop, @Param("area") String area);



    List<String> getPlacement(@Param("shop") String shop, @Param("area") String area);


    /**
     * 获取campaign_name
     * @param shopArea
     * @param sellerId
     * @return
     */
    List<String> getCampaignName(@Param("sellerId") String sellerId,@Param("shopArea") String shopArea);

}
