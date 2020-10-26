package com.weiziplus.springboot.mapper.scheduled;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/8/1 10:44
 */
@Mapper
public interface OriginalDataFinancialCouponPaymentEventMapper {

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop, @Param("area") String area,@Param("couponId") String couponId);
}
