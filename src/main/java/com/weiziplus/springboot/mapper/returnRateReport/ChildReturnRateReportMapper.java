package com.weiziplus.springboot.mapper.returnRateReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ChildReturnRateReportMapper {


	List<HashMap<String,Object>> getChildPageList(Map map);


	List<HashMap<String,Object>> getParentPageList(Map map);


}
