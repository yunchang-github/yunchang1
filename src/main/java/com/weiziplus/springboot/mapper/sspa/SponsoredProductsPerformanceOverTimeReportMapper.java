package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.SponsoredProductsPerformanceOverTimeReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/1 17:45
 */
@Mapper
public interface SponsoredProductsPerformanceOverTimeReportMapper {
    /**
     * 根据年份获取列表数据
     *
     * @param year
     * @return
     */
    SponsoredProductsPerformanceOverTimeReport getListDataByYear(@Param("year") String year);

    /**
     * 根据月份获取列表数据
     *
     * @param year
     * @param month
     * @return
     */
    SponsoredProductsPerformanceOverTimeReport getListDataByYearAndMonth(@Param("year") String year, @Param("month") String month);
}
