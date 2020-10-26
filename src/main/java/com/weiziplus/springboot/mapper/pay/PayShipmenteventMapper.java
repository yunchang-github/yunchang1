package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayShipmentevent;

public interface PayShipmenteventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayShipmentevent record);

    int insertSelective(PayShipmentevent record);

    PayShipmentevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayShipmentevent record);

    int updateByPrimaryKey(PayShipmentevent record);
}