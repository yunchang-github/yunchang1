package com.weiziplus.springboot.mapper.caravan;

import com.weiziplus.springboot.models.StockWarehouseData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/9/3 17:11
 */
@Mapper
public interface StockWarehouseDataMapper {

    /**
     * 根据日期删除数据
     *
     * @param date
     * @return
     */
    int deleteByDate(@Param("date") String date);

    /**
     * 根据参数获取一条数据
     *
     * @param stockId
     * @param wearhouseId
     * @return
     */
    StockWarehouseData getOneInfoByStockIdAndWearhouseId(@Param("stockId") String stockId, @Param("wearhouseId") String wearhouseId);
}
