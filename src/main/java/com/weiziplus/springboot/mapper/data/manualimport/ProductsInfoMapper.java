package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.ProductsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/26 16:24
 */
@Mapper
public interface ProductsInfoMapper {
    /**
     * 根据店铺、区域、msku获取一条数据
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    ProductsInfo getOneInfoByShopAndAreaAndMsku(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);

    List<ProductsInfo> getList(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);
}
