package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayFbaliquidationevent;

public interface PayFbaliquidationeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayFbaliquidationevent record);

    int insertSelective(PayFbaliquidationevent record);

    PayFbaliquidationevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayFbaliquidationevent record);

    int updateByPrimaryKey(PayFbaliquidationevent record);
}