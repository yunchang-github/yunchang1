package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/11 14:20
 */
@Mapper
public interface ShopAreaMapper {

    /**
     * 根据网店id获取地区列表信息
     *
     * @param shopId
     * @return
     */
    List<Area> getAreaListByShopId(@Param("shopId") Long shopId);

    /**
     * 根据网店id获取所有地区列表信息
     *
     * @param shopId
     * @return
     */
    List<Map<String, Object>> getAllAreaListByShopId(@Param("shopId") Long shopId);

    /**
     * 根据网店id删除区域列表
     *
     * @param shopId
     * @return
     */
    int deleteAreaListByShopId(@Param("shopId") Long shopId);

    /**
     * 根据网店id和区域ids新增网店区域列表
     *
     * @param shopId
     * @param areaIds
     * @return
     */
    int addAreaListByShopIdAndAreaIds(@Param("shopId") Long shopId, @Param("areaIds") Long[] areaIds);
}
