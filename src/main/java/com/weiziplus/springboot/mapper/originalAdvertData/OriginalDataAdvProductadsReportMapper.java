package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.OriginalDataAdvProductadsReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OriginalDataAdvProductadsReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OriginalDataAdvProductadsReport record);

    int insertSelective(OriginalDataAdvProductadsReport record);

    OriginalDataAdvProductadsReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OriginalDataAdvProductadsReport record);

    int updateByPrimaryKey(OriginalDataAdvProductadsReport record);
}