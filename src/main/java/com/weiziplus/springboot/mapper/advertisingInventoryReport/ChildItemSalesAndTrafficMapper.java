package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ChildItemSalesAndTrafficMapper {


	List<String> getChildItemSalesAndTrafficAsin(@Param("startFourWeekStr")String startFourWeekStr,@Param("date")String date,@Param("shop")String shop,@Param("area")String area);

//	String getMskuParentskuChildrenasinParentasinByMsku(@Param("shop")String shop,@Param("area")String area,@Param("msku")String msku);

	int getChildItemSalesVolume(@Param("param") Map<String, Object> paraMap);

	Double getChildItemSalesVolumeUnitPrice(@Param("param") Map<String, Object> paraMap);

	int getChildItemPageViews(@Param("param") Map<String, Object> paraMap);

	List<String> getChildItemSalesAndTrafficFromFcTransfersAsin(@Param("shop")String shop,@Param("area")String area);

	List<String> getChildItemSalesAndTrafficFromManageFbaInventory(@Param("shop")String shop,@Param("area")String area);

	List<HashMap<String,Object>> getAsinFromThreeRecource(Map map);

	List<HashMap<String,Object>> getAsinFromThreeRecourceFromParentSku(Map map);





}
