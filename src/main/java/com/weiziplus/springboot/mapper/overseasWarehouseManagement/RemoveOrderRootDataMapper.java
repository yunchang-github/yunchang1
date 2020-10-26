package com.weiziplus.springboot.mapper.overseasWarehouseManagement;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:51
 */
@Mapper
public interface RemoveOrderRootDataMapper {
	List<Map<String,Object>> getList(@Param("shop")String shop, @Param("area")String area);
}
