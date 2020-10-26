package com.weiziplus.springboot.mapper.originalAdvertData;

import com.weiziplus.springboot.models.DO.ErrorDateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ErrorDateMapper {
    int addErrorDate(ErrorDateDO errorDateDO);

    List<ErrorDateDO> selectAllErrorDateData();

    int delErrorDate(@Param("errorDate") ErrorDateDO errorDate);
}
