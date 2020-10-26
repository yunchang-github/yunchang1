package com.weiziplus.springboot.mapper.advertisingInventoryReport;

import com.weiziplus.springboot.models.DetailPageSalesAndTrafficByChildItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/*
 * 新增的 子商品详情页面上的销售量和访问量 的mapper接口
 * --苏建东
 * */
@Mapper
public interface DetailPageSalesAndTrafficByChildItemsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DetailPageSalesAndTrafficByChildItems record);

    int insertSelective(DetailPageSalesAndTrafficByChildItems record);

    /**
     * 通过list新增多条数据
     */
    int insertListSelective(@Param("detailPageSalesAndTrafficByChildItemsList") List<DetailPageSalesAndTrafficByChildItems> detailPageSalesAndTrafficByChildItemsList);

    DetailPageSalesAndTrafficByChildItems selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DetailPageSalesAndTrafficByChildItems record);

    int updateByPrimaryKey(DetailPageSalesAndTrafficByChildItems record);

    /**
     * 获得数据库中的该日期的数据数量
     */
    Integer getDateSum(@Param("date") String date,@Param("sellerId") String sellerId,@Param("area") String area);

    /**
     * 删除数据库中的该日期的数据
     */
    Integer deleteDateSum(@Param("sellerId")String sellerId, @Param("area")String area, @Param("date") String date);

    /**
     * 获取最近三个月的表中的ordered_item_sales和ordered_item_sales_B2B两个字段的集合
     */
    BigDecimal getLatestAllOrderedItemSales(String shop, String area);

    /**
     * 根据日期月份获取表中的ordered_item_sales和ordered_item_sales_B2B两个字段的集合
     */
    BigDecimal getAllOrderedItemSalesByDate(String startDate,String endDate, String shop, String area);

    /**
     * 获取最近三个月的表中的buyer_visits的和
     */
    Integer getLatestBuyerVisits(String shop, String area);

    /**
     * 根据日期月份获取表中的buyer_visits的和
     */
    Integer getBuyerVisitsByDate(String startDate,String endDate, String shop, String area);
}