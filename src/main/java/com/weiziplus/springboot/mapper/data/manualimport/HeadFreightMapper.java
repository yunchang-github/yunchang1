package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.HeadFreight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/26 11:45
 */
@Mapper
public interface HeadFreightMapper {

    /**
     * 根据店铺、区域、msku获取一条数据
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    HeadFreight getOneInfoByShopAndAreaAndMsku(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);


    List<HeadFreight> getList(Map map);
}
