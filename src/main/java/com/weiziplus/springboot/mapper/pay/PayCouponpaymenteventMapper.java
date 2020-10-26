package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayCouponpaymentevent;

public interface PayCouponpaymenteventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayCouponpaymentevent record);

    int insertSelective(PayCouponpaymentevent record);

    PayCouponpaymentevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayCouponpaymentevent record);

    int updateByPrimaryKey(PayCouponpaymentevent record);
}