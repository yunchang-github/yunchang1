package com.weiziplus.springboot.mapper.productStockingManage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.Beihuo;

@Mapper
public interface ProductStockingManageMapper {

	List getProductStockingPageList(@Param("shop")String shop, @Param("area")String area);

	Beihuo findRecordByCondition(@Param("cm")Map<String, Object> map);

}
