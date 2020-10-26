package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayChargeinstrument;

public interface PayChargeinstrumentMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayChargeinstrument record);

    int insertSelective(PayChargeinstrument record);

    PayChargeinstrument selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayChargeinstrument record);

    int updateByPrimaryKey(PayChargeinstrument record);
}