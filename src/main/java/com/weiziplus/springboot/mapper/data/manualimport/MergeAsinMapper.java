package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.MergeAsin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/26 17:34
 */
@Mapper
public interface MergeAsinMapper {

    /**
     * 根据店铺和区域获取一条数据
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    MergeAsin getOneInfoByShopAndAreaAndAsin(@Param("shop") String shop, @Param("area") String area, @Param("asin") String asin);

    List<MergeAsin> getList(@Param("shop") String shop, @Param("area") String area, @Param("asin") String asin);
}
