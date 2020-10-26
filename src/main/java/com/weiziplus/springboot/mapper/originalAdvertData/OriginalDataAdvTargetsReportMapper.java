package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.OriginalDataAdvTargetsReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OriginalDataAdvTargetsReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OriginalDataAdvTargetsReport record);

    int insertSelective(OriginalDataAdvTargetsReport record);

    OriginalDataAdvTargetsReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OriginalDataAdvTargetsReport record);

    int updateByPrimaryKey(OriginalDataAdvTargetsReport record);
}