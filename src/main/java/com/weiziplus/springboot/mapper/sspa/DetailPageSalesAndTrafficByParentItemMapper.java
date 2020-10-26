package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.DetailPageSalesAndTrafficByParentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/8/16 11:50
 */
@Mapper
public interface DetailPageSalesAndTrafficByParentItemMapper {

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop, @Param("area") String area);

    /**
     * 根据网店、国家代码、日期获取一条数据
     *
     * @param shop
     * @param area
     * @param date
     * @return
     */
    DetailPageSalesAndTrafficByParentItem getOneInfoByShopAndAreaAndDate(@Param("shop") String shop, @Param("area") String area, @Param("date") String date);
}
