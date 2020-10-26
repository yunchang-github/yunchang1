package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayRetrochargeevent;

public interface PayRetrochargeeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayRetrochargeevent record);

    int insertSelective(PayRetrochargeevent record);

    PayRetrochargeevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayRetrochargeevent record);

    int updateByPrimaryKey(PayRetrochargeevent record);
}