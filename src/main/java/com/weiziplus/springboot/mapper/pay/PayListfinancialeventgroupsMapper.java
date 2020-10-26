package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayListfinancialeventgroups;

public interface PayListfinancialeventgroupsMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayListfinancialeventgroups record);

    int insertSelective(PayListfinancialeventgroups record);

    PayListfinancialeventgroups selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayListfinancialeventgroups record);

    int updateByPrimaryKey(PayListfinancialeventgroups record);
}