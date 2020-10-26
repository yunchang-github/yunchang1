package com.weiziplus.springboot.mapper.overseasWarehouseManagement;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.UsEastWarehouseData;

@Mapper
public interface USEastWarehouseDataMapper {
	List<UsEastWarehouseData> getList(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("msku")String msku, @Param("placeBoxNumber")String placeBoxNumber);

	UsEastWarehouseData getOneInfoByUSEastWarehouseDataId(Long id);
}
