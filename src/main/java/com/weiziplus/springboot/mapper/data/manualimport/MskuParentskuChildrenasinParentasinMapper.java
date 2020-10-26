package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.MskuParentskuChildrenasinParentasin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/26 9:36
 */
@Mapper
public interface MskuParentskuChildrenasinParentasinMapper {
    /**
     * 根据店铺、区域、msku获取一条数据
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    MskuParentskuChildrenasinParentasin getOneInfoByShopAndAreaAndMsku(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);

    List<MskuParentskuChildrenasinParentasin> getList(Map map);
}
