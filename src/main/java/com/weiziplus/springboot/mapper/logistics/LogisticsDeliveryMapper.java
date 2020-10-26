package com.weiziplus.springboot.mapper.logistics;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.weiziplus.springboot.models.ClaimFollowUp;

@Mapper
public interface LogisticsDeliveryMapper {

	List getShopDeliveryDetailsPageList(@Param("shop")String shop, @Param("area")String area);

	List getTimeLinePageList(@Param("shop")String shop, @Param("area")String area);

	List getShippingLogisticsRecordPageList(@Param("shop")String shop, @Param("area")String area);

	List getClaimFollowUpPageList(@Param("shop")String shop, @Param("area")String area);

	ClaimFollowUp getClaimFollowUpByCondition(@Param("shop")String shop, @Param("area")String area, @Param("shipmentId")String shipmentId, @Param("msku")String msku);
	
}
