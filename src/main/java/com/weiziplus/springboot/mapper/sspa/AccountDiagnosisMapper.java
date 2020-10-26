package com.weiziplus.springboot.mapper.sspa;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountDiagnosisMapper {

    List<Map> getTopTenAsinThreeMonth(@Param("shop") String shop, @Param("area") String area);
}
