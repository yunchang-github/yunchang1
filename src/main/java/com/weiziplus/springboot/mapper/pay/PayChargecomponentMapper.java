package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayChargecomponent;

public interface PayChargecomponentMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayChargecomponent record);

    int insertSelective(PayChargecomponent record);

    PayChargecomponent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayChargecomponent record);

    int updateByPrimaryKey(PayChargecomponent record);
}