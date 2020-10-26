package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayAdjustmentitem;

public interface PayAdjustmentitemMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayAdjustmentitem record);

    int insertSelective(PayAdjustmentitem record);

    PayAdjustmentitem selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayAdjustmentitem record);

    int updateByPrimaryKey(PayAdjustmentitem record);
}