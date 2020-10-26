package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.ExpectedAmazonDistributionCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/26 14:36
 */
@Mapper
public interface ExpectedAmazonDistributionCostMapper {

    /**
     * 根据店铺、区域、msku获取一条数据
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    ExpectedAmazonDistributionCost getOneInfoByShopAndAreaAndMsku(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);

    /**
     * 获取列表
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    List<ExpectedAmazonDistributionCost> getList(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);
}
