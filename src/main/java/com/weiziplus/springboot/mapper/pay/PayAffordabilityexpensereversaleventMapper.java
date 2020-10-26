package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayAffordabilityexpensereversalevent;

public interface PayAffordabilityexpensereversaleventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayAffordabilityexpensereversalevent record);

    int insertSelective(PayAffordabilityexpensereversalevent record);

    PayAffordabilityexpensereversalevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayAffordabilityexpensereversalevent record);

    int updateByPrimaryKey(PayAffordabilityexpensereversalevent record);
}