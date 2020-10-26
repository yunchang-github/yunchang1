package com.weiziplus.springboot.mapper.sspa;

import com.weiziplus.springboot.models.DetailPageSalesAndTrafficByChildItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/3 14:06
 */
@Mapper
public interface DetailPageSalesAndTrafficByChildItemMapper {

    /**
     * 获取列表数据
     *
     * @return
     */
    List<Map<String, Object>> getList(Map map    );

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
    DetailPageSalesAndTrafficByChildItem getOneInfoByShopAndAreaAndDate(@Param("shop") String shop, @Param("area") String area, @Param("date") String date);
}
