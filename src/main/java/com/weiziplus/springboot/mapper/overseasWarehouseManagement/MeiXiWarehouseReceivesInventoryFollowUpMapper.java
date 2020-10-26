package com.weiziplus.springboot.mapper.overseasWarehouseManagement;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface MeiXiWarehouseReceivesInventoryFollowUpMapper {
	List<HashMap<String,Object>> getList(@Param("shop")String shop, @Param("area")String area);
}
