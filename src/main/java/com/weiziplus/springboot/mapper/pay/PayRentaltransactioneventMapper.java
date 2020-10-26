package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayRentaltransactionevent;

public interface PayRentaltransactioneventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayRentaltransactionevent record);

    int insertSelective(PayRentaltransactionevent record);

    PayRentaltransactionevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayRentaltransactionevent record);

    int updateByPrimaryKey(PayRentaltransactionevent record);
}