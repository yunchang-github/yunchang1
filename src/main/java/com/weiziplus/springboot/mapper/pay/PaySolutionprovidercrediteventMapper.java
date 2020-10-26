package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PaySolutionprovidercreditevent;

public interface PaySolutionprovidercrediteventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PaySolutionprovidercreditevent record);

    int insertSelective(PaySolutionprovidercreditevent record);

    PaySolutionprovidercreditevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PaySolutionprovidercreditevent record);

    int updateByPrimaryKey(PaySolutionprovidercreditevent record);
}