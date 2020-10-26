package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.SalesAndTraffic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/2 10:31
 */
@Mapper
public interface SalesAndTrafficMapper {
    /**
     * 根据年份获取列表数据
     *
     * @param year
     * @return
     */
    SalesAndTraffic getListDataByYear(@Param("year") String year);

    /**
     * 根据月份获取列表数据
     *
     * @param year
     * @param month
     * @return
     */
    SalesAndTraffic getListDataByYearAndMonth(@Param("year") String year, @Param("month") String month);

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop, @Param("area") String area);


    /**
     * 根据网店、国家代码、日期获取一条数据
     *
     * @param shop
     * @param area
     * @param date
     * @return
     */
    SalesAndTraffic getOneInfoByShopAndAreaAndDate(@Param("shop") String shop, @Param("area") String area, @Param("date") String date);
}
