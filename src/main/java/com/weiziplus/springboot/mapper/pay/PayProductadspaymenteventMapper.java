package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayProductadspaymentevent;

public interface PayProductadspaymenteventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayProductadspaymentevent record);

    int insertSelective(PayProductadspaymentevent record);

    PayProductadspaymentevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayProductadspaymentevent record);

    int updateByPrimaryKey(PayProductadspaymentevent record);
}