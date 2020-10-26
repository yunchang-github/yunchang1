package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayAffordabilityexpenseevent;

public interface PayAffordabilityexpenseeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayAffordabilityexpenseevent record);

    int insertSelective(PayAffordabilityexpenseevent record);

    PayAffordabilityexpenseevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayAffordabilityexpenseevent record);

    int updateByPrimaryKey(PayAffordabilityexpenseevent record);
}