package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayPaywithamazonevent;

public interface PayPaywithamazoneventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayPaywithamazonevent record);

    int insertSelective(PayPaywithamazonevent record);

    PayPaywithamazonevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayPaywithamazonevent record);

    int updateByPrimaryKey(PayPaywithamazonevent record);
}