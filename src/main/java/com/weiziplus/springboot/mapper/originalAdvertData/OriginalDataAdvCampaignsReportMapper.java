package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.OriginalDataAdvCampaignsReport;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OriginalDataAdvCampaignsReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OriginalDataAdvCampaignsReport record);

    int insertSelective(OriginalDataAdvCampaignsReport record);

    OriginalDataAdvCampaignsReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OriginalDataAdvCampaignsReport record);

    int updateByPrimaryKey(OriginalDataAdvCampaignsReport record);

    /**
     * 获取最近三个月的表中的clicks的和
     * */
    Integer getLatestClicksSum(String shop,String area);

    /**
     * 根据日期月份获取表中的clicks的和
     * */
    Integer getClicksSumByDate(String startDate,String endDate,String shop,String area);

    /**
     * 获取最近三个月的表中的impressions的和
     * */
    Integer getLatestImpressionsSum(String shop,String area);

    /**
     * 根据日期月份获取表中的impressions的和
     * */
    Integer getImpressionsSumByDate(String startDate,String endDate,String shop,String area);

    /**
     * 获取最近三个月的表中的cost的和
     * */
    BigDecimal getLatestCostSum(String shop,String area);

    /**
     * 根据日期月份获取表中的cost的和
     * */
    BigDecimal getCostSumByDate(String startDate,String endDate,String shop,String area);

    /**
     * 获取最近三个月的表中的attributedSales1d的和
     * */
    BigDecimal getLatestAttributedSales1dSum(String shop,String area);

    /**
     * 根据日期月份获取表中的attributedSales1d的和
     * */
    BigDecimal getAttributedSales1dSumByDate(String startDate,String endDate,String shop,String area);

    /**
     * 获取最近三个月的表中的attributedConversions1d的和
     * */
    Integer getLatestAttributedConversions1dSum(String shop,String area);

    /**
     * 根据日期月份获取表中的attributedConversions1d的和
     * */
    Integer getAttributedConversions1dSumByDate(String startDate,String endDate,String shop,String area);

    /**
     * 获得最近28天的展现量列表
     * */
    List<Map> getImpressionsListByLatestMonth(String shop,String area);
}