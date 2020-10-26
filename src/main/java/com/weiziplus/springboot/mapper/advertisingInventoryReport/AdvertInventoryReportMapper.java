package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import com.weiziplus.springboot.models.DO.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdvertInventoryReportMapper {

    List<ChildBodyInventoryReportDO> getChildInventoryAdvertisingReport(Map map);

    List<ParentBodyInventoryReportDO> getParentInventoryAdvertisingReport(Map map);

    /**
     * 获取子体报表数据条数
     * */
    Integer getCount(Map maps);

    /**
     * 获得目前所有的sku，asin，psku的对应关系
     * */
    List<SkuAsinPskuDO> getAllSkuAsinPsku();


    /**
     * 获得店铺，区域，周维度来获得所有的sku对应的销量和销售单价
     * */
    List<QuantityAndUnitPriceDO> getQuantityAndUnitPrice(@Param("shop") String shop, @Param("area") String area,@Param("salesChannel") String salesChannel, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("flag") int flag);

    /**
     * 获得店铺，区域，周维度来获得所有的sku对应的各种库存
     * */
    List<InventoryDO> getInventory(@Param("shop") String shop, @Param("area") String area, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("flag") int flag);

    /**
     * 获得店铺，区域，周维度来获得所有的sku对应的广告数据部分
     * */
    List<AdvProductDataDO> getAdvProductData(@Param("shop") String shop, @Param("area") String area, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("flag") int flag);

    /**
     * 获得店铺，区域，周维度来获得所有的sku对应的用户浏览次数
     * */
    List<BuyerVisitsDO> getBuyerVisits(@Param("shop") String shop, @Param("area") String area, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("flag") int flag);

    /**
     * 获得店铺，区域，周维度来获得所有的asin对应的秒杀数据
     * */
    List<LightningDealsDataDO> getLightningDeals(@Param("shop") String shop, @Param("area") String area, @Param("domainId") int domainId, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("flag") int flag);

}
