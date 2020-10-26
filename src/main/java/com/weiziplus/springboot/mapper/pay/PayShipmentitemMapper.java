package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayShipmentitem;

public interface PayShipmentitemMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayShipmentitem record);

    int insertSelective(PayShipmentitem record);

    PayShipmentitem selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayShipmentitem record);

    int updateByPrimaryKey(PayShipmentitem record);
}