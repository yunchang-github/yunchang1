package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayImagingservicesfeeevent;

public interface PayImagingservicesfeeeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayImagingservicesfeeevent record);

    int insertSelective(PayImagingservicesfeeevent record);

    PayImagingservicesfeeevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayImagingservicesfeeevent record);

    int updateByPrimaryKey(PayImagingservicesfeeevent record);
}