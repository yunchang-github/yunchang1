package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.OriginalDataAdvAdgroupsReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OriginalDataAdvAdgroupsReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OriginalDataAdvAdgroupsReport record);

    int insertSelective(OriginalDataAdvAdgroupsReport record);

    OriginalDataAdvAdgroupsReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OriginalDataAdvAdgroupsReport record);

    int updateByPrimaryKey(OriginalDataAdvAdgroupsReport record);
}