package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.OriginalDataAdvAsinsReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OriginalDataAdvAsinsReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OriginalDataAdvAsinsReport record);

    int insertSelective(OriginalDataAdvAsinsReport record);

    OriginalDataAdvAsinsReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OriginalDataAdvAsinsReport record);

    int updateByPrimaryKey(OriginalDataAdvAsinsReport record);
}