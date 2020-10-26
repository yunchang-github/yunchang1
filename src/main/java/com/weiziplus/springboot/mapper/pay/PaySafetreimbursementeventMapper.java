package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PaySafetreimbursementevent;

public interface PaySafetreimbursementeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PaySafetreimbursementevent record);

    int insertSelective(PaySafetreimbursementevent record);

    PaySafetreimbursementevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PaySafetreimbursementevent record);

    int updateByPrimaryKey(PaySafetreimbursementevent record);
}