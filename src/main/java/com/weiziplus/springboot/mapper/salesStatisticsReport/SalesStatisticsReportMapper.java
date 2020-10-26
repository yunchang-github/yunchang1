package com.weiziplus.springboot.mapper.salesStatisticsReport;

import com.weiziplus.springboot.models.DO.*;
import com.weiziplus.springboot.models.Inventory;
import com.weiziplus.springboot.models.VO.ShopSalesStatisticsDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalesStatisticsReportMapper {
    /**
     * 根据年份获得店铺一年的销售额
     */
    List<SalesAmountDO> getSalesAmountByYear(@Param("sellerId") String sellerId, @Param("areaCode") String areaCode, @Param("salesChannel") String salesChannel, @Param("year") int year);

    /**
     * 根据月份获得店铺指定月的销售额
     */
    SalesAmountDO getSalesAmountByMonth(@Param("sellerId") String sellerId, @Param("areaCode") String areaCode, @Param("salesChannel") String salesChannel, @Param("date") String date);

    /**
     * 根据月份获得该月的销售额
     */
    ShopSalesStatisticsDataVO getSalesAmountFromExistingData(@Param("sellerId") String sellerId, @Param("areaCode") String areaCode, @Param("year") int year);

    /**
     * 存入该店铺关于销售额的基本数据
     */
    int insertBaseData(@Param("shopSalesStatisticsDataVO") ShopSalesStatisticsDataVO shopSalesStatisticsDataVO);

    /**
     * 修改该店铺关于销售额的基本数据
     */
    int updateBaseData(@Param("shopSalesStatisticsDataVO") ShopSalesStatisticsDataVO shopSalesStatisticsDataVO);

    /**
     * 获得目前的管理库存
     */
    List<InventorySumDO> getManageFbaInventorySum();

    /**
     * 获得目前的预留库存
     */
    List<InventorySumDO> getReservedInventorySum();

    /**
     * 根据日期间隔获得销量值
     */
    Integer getDailyAverageQuantityByDate(@Param("shopName") String shopName, @Param("areaCode") String areaCode, @Param("latestWeek") int latestWeek);

    /**
     * 根据大组名获得指定的店铺区域的指标
     */
    List<SalesIndicatorDO> getSalesIndicatorByGroupName(@Param("groupName") String groupName,@Param("year") int year);

    /**
     * 获取今年的所有大组的名称集合
     */
    List<SalesIndicatorDO> getSalesIndicatorName(@Param("year") int year);

    List<ExchangeRateDO> getExchangeRate();

}
