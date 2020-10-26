package com.weiziplus.springboot.mapper.sqlTable;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.PrimitiveIterator;

@Mapper
public interface SqlTableMapper {
    /**
     * 删除sku_asin_psku表里的数据
     * */
    int delSkuAsinPsku();

    /**
     * 往sku_asin_psku表中更新数据
     * */
    int insertSkuAsinPsku();

    /**
     * 删除latest_manage_fba_inventory表里的数据
     * */
    int delLatestManageFbaInventory();

    /**
     * 往latest_manage_fba_inventory表中更新数据
     * */
    int insertLatestManageFbaInventory();

    /**
     * 删除latest_reserved_inventory表里的数据
     * */
    int delLatestReservedInventory();

    /**
     * 往latest_reserved_inventory表中更新数据
     * */
    int insertLatestReservedInventory();

    /**
     * 删除latest_inventory_age表里的数据
     * */
    int delLatestInventoryAge();

    /**
     * 往latest_inventory_age表中更新数据
     * */
    int insertLatestInventoryAge();

    /**
     * 删除detail_page_sales_and_traffic_error_date表里的数据
     * */
    int delDetailPageSalesAndTrafficErrorDate();

    /**
     * 往detail_page_sales_and_traffic_error_date表中更新数据
     * */
    int insertDetailPageSalesAndTrafficErrorDate(@Param("shop") String shop,@Param("sellerId") String sellerId,@Param("area") String area,@Param("tableName") String tableName);

    /**
     * 删除latest_sum_quantity表里的数据
     * */
    int delLatestSumQuantity();

    /**
     * 往latest_sum_quantity表中更新数据
     * */
    int insertLatestSumQuantity(@Param("latestWeek") int latestWeek,@Param("createTime") String createTime);
}
