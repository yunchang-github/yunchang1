package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayDirectpayment;

public interface PayDirectpaymentMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayDirectpayment record);

    int insertSelective(PayDirectpayment record);

    PayDirectpayment selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayDirectpayment record);

    int updateByPrimaryKey(PayDirectpayment record);
}