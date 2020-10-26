package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.OriginalDataAdvKeywordsReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OriginalDataAdvKeywordsReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OriginalDataAdvKeywordsReport record);

    int insertSelective(OriginalDataAdvKeywordsReport record);

    OriginalDataAdvKeywordsReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OriginalDataAdvKeywordsReport record);

    int updateByPrimaryKey(OriginalDataAdvKeywordsReport record);
}