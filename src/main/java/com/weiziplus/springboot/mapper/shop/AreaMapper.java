package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/11 11:21
 */
@Mapper
public interface AreaMapper {

    /**
     * 获取区域列表
     *
     * @param name
     * @return
     */
    List<Area> getList(@Param("name") String name);

    /**
     * 根据名称获取一个网店信息
     *
     * @param name
     * @return
     */
    Area getOneInfoByName(@Param("name") String name);

    /**
     * 根据MarketplaceId获取一个网店信息
     *
     * @param marketplaceId
     * @return
     */
    Area getOneInfoByMarketplaceId(@Param("marketplaceId") String marketplaceId);

    /**
     * 根据区域编码country_code确定对应的area
     * @param countryCode
     * @return
     */
    Area getAreaByCountryCode(@Param("countryCode") String countryCode);


    /**
     * 根据区域ID获取对应的区域信息
     * @param areaId
     * @return
     */
    Area getOneInfoByAreaId(@Param("areaId") Long areaId);

    /**
     * 根据各区域不同的站点网址获取指定的区域码country_code
     * @param areaUrl
     * @return
     */
    String getMwsCountryCodeByAreaUrl(@Param("areaUrl") String areaUrl);

    /**
     * 根据mwsCountryCode获取Area
     * @param mwsCountryCode
     * @return
     */
    Area getAreaByMWSCountryCode(@Param("mwsCountryCode")String mwsCountryCode);

    /**
     * 根据advertCountryCode获取Area
     * @param advertCountryCode
     * @return
     */
    Area getAreaByAdvertCountryCode(@Param("advertCountryCode")String advertCountryCode);
}
