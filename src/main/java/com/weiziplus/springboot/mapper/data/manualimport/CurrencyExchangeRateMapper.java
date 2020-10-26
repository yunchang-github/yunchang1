package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.CurrencyExchangeRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/26 15:31
 */
@Mapper
public interface CurrencyExchangeRateMapper {

    /**
     * 根据币种获取一个数据
     *
     * @param currency
     * @return
     */
    CurrencyExchangeRate getOneInfoByCurrency(@Param("currency") String currency);
}
