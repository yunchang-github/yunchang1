package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayLoanservicingevent;

public interface PayLoanservicingeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayLoanservicingevent record);

    int insertSelective(PayLoanservicingevent record);

    PayLoanservicingevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayLoanservicingevent record);

    int updateByPrimaryKey(PayLoanservicingevent record);
}