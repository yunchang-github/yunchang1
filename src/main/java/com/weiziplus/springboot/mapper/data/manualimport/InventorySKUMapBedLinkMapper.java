package com.weiziplus.springboot.mapper.data.manualimport;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.InventorySkuMapBedLink;

@Mapper
public interface InventorySKUMapBedLinkMapper {

	List<InventorySkuMapBedLink> getList(@Param("inventorySku")String inventorySku);

	InventorySkuMapBedLink getOneInfoByCondition(@Param("inventorySku")String inventorySku);

    String getLinkBySku(@Param("sku") String sku);
}
