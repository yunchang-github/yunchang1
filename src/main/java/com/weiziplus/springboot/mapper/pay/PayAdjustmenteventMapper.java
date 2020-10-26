package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayAdjustmentevent;

public interface PayAdjustmenteventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayAdjustmentevent record);

    int insertSelective(PayAdjustmentevent record);

    PayAdjustmentevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayAdjustmentevent record);

    int updateByPrimaryKey(PayAdjustmentevent record);
}