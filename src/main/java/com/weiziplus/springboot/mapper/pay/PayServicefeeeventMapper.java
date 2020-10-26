package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayServicefeeevent;

public interface PayServicefeeeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayServicefeeevent record);

    int insertSelective(PayServicefeeevent record);

    PayServicefeeevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayServicefeeevent record);

    int updateByPrimaryKey(PayServicefeeevent record);
}