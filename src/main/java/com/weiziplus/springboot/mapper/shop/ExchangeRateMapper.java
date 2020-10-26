package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.DO.ExchangeRateDO;
import com.weiziplus.springboot.models.ExchangeRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/11 15:22
 */
@Mapper
public interface ExchangeRateMapper {

    /**
     * 获取列表数据
     *
     * @param areaId
     * @param currency
     * @param createTime
     * @return
     */
    List<Map<String, Object>> getList(@Param("areaId") Long areaId, @Param("currency") String currency, @Param("createTime") String createTime);

    /**
     * 根据区域id获取一个汇率信息
     *
     * @param areaId
     * @return
     */
    ExchangeRate getOneInfoByAreaId(@Param("areaId") Long areaId);

    /**
     * 获取最新的汇率数据
     * */
    int insertExchangeRate(@Param("exchangeRateDOList") List<ExchangeRateDO> exchangeRateDOList);
}
