package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PaySellerreviewenrollmentpaymentevent;

public interface PaySellerreviewenrollmentpaymenteventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PaySellerreviewenrollmentpaymentevent record);

    int insertSelective(PaySellerreviewenrollmentpaymentevent record);

    PaySellerreviewenrollmentpaymentevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PaySellerreviewenrollmentpaymentevent record);

    int updateByPrimaryKey(PaySellerreviewenrollmentpaymentevent record);
}