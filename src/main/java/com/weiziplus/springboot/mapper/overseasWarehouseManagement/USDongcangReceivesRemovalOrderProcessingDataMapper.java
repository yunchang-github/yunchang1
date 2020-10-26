package com.weiziplus.springboot.mapper.overseasWarehouseManagement;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.Msku;
import com.weiziplus.springboot.models.OverseasWarehousePlanInformationRegistration;
import com.weiziplus.springboot.models.RemoveOrderActionRecord;
import com.weiziplus.springboot.models.UsDongcangReceivesRemovalOrderProcessingData;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:51
 */
@Mapper
public interface USDongcangReceivesRemovalOrderProcessingDataMapper {

	List<Map<String,Object>> getList(@Param("shop")String shop, @Param("area")String area, @Param("date")String date);
	
	
	List<RemoveOrderActionRecord> findDataByTrackingNumber(@Param("shop")String shop, @Param("area")String area, @Param("trackingNumber")String trackingNumber);


	RemoveOrderActionRecord findDataByFnsku(@Param("shop")String shop, @Param("area")String area, @Param("fnsku")String fnsku);


	int updateByTableContent(UsDongcangReceivesRemovalOrderProcessingData usDongcangReceivesRemovalOrderProcessingData);


	OverseasWarehousePlanInformationRegistration findDataByPlaceBoxNumber(@Param("placeBoxNumber")String placeBoxNumber);


	Msku findFnsku(@Param("shop")String shop, @Param("area")String area, @Param("msku")String msku);
	
}
