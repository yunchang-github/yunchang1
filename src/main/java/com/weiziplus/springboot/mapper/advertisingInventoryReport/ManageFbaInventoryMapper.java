package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.ManageFbaInventory;

@Mapper
public interface ManageFbaInventoryMapper {
	ManageFbaInventory getStock(@Param("param")Map<String, Object> paraMap);
}
