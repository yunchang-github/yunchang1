package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.ReservedInventory;


@Mapper
public interface ReservedInventoryMapper {
	ReservedInventory getStockFromReservedInventory(@Param("param")Map<String, Object> paraMap);
	void deleteReservedInventory();
}
