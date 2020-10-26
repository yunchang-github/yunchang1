package com.weiziplus.springboot.mapper.review;

import com.weiziplus.springboot.models.CrawlShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/8/27 17:02
 */
@Mapper
public interface CrawlShopMapper {

    /**
     * 根据网店、国家代码、asin获取一条数据
     *
     * @param shop
     * @param area
     * @param asin
     * @return
     */
    CrawlShop getOneInfoByShopAndAreaAndAsin(@Param("shop") String shop, @Param("area") String area, @Param("asin") String asin);
}
