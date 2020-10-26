package com.weiziplus.springboot.mapper.performanceAccountingReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ChildPerformanceAccountingMapper {


	List<HashMap<String,Object>> getChildPageList(Map map);

	List<HashMap<String,Object>> getParentPageList(Map map);

	HashMap<String,Object> findPurchasePrice(@Param("shop")String shop, @Param("area")String area, @Param("sku")String sku);

	HashMap<String, Object> findHeadFreight(@Param("shop")String shop, @Param("area")String area, @Param("sku")String sku);

	String findShippingFees(@Param("shop")String shop, @Param("area")String area,@Param("asin") String asin);

	String findExpectedAmazonDistributionCost(@Param("shop")String shop, @Param("area")String area, @Param("sku")String sku);

	Double findSponsoredProductsAdvertisedProductReport(@Param("shop")String shop, @Param("area")String area,@Param("sku") String sku, @Param("firstDate")String firstDate,
			@Param("lastDate")String lastDate);

	Double findShopExchangeRate(@Param("shop")String shop, @Param("area")String area, @Param("firstDate")String firstDate, @Param("lastDate")String lastDate);

	Double findShopCommissionRate(@Param("shop")String shop, @Param("area")String area, @Param("firstDate")String firstDate, @Param("lastDate")String lastDate);

	Double findMonthlyStorageFees(@Param("shop")String shop, @Param("area")String area, @Param("asin")String asin,@Param("firstDate") String firstDate, @Param("lastDate")String lastDate);

	Double findLongTermStorageFees(@Param("shop")String shop, @Param("area")String area, @Param("asin")String asin,@Param("firstDate") String firstDate, @Param("lastDate")String lastDate);

	Map<String, Object> findNetSalesFromOneSku(@Param("sku")String sku, @Param("shop")String shop, @Param("area")String area, @Param("firstDate")String firstDate, @Param("lastDate")String lastDate);

	Map<String, Object> findTotalRefundFromOneSku(@Param("sku")String sku, @Param("shop")String shop, @Param("area")String area, @Param("firstDate")String firstDate, @Param("lastDate")String lastDate);

	String findSellableFromOneSku(@Param("sku")String sku, @Param("shop")String shop, @Param("area")String area, @Param("firstDate")String firstDate, @Param("lastDate")String lastDate);

}
