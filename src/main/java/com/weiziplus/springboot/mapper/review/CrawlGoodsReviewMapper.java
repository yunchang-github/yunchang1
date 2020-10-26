package com.weiziplus.springboot.mapper.review;

import com.weiziplus.springboot.models.CrawlGoodsReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/8/28 14:43
 */
@Mapper
public interface CrawlGoodsReviewMapper {

    /**
     * 根据店铺、区域、asin获取一条数据
     *
     * @param shop
     * @param area
     * @param asin
     * @return
     */
    CrawlGoodsReview getOneInfoByShopAndAreaAndAsin(@Param("shop") String shop, @Param("area") String area, @Param("asin") String asin);
}
