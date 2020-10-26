package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayFeecomponent;

public interface PayFeecomponentMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayFeecomponent record);

    int insertSelective(PayFeecomponent record);

    PayFeecomponent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayFeecomponent record);

    int updateByPrimaryKey(PayFeecomponent record);
}