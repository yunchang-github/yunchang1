package com.weiziplus.springboot.mapper.scheduled;

import com.weiziplus.springboot.models.AllOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/30 11:31
 */
@Mapper
public interface AllOrderMapper {

    /**
     * 根据亚马逊id、sku、asin获取一条亚马逊记录
     *
     * @param amazonOrderId
     * @param sku
     * @param asin
     * @param area
     * @return
     */
    AllOrder getOneInfoByAmazonOrderIdAndSkuAndAsin(@Param("amazonOrderId") String amazonOrderId, @Param("sku") String sku, @Param("asin") String asin,@Param("area") String area);
    void deleteAllOrder();

}
