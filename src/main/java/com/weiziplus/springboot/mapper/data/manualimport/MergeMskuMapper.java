package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.MergeMsku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/26 17:19
 */
@Mapper
public interface MergeMskuMapper {

    /**
     * 根据店铺和区域获取一条数据
     *
     * @param shop
     * @param area
     * @return
     */
    MergeMsku getOneInfoByShopAndAreaAndMsku(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);

    List<MergeMsku> getList(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);
}
