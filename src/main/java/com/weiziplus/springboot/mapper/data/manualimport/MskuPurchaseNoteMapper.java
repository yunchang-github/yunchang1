package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.MskuParentskuChildrenasinParentasin;
import com.weiziplus.springboot.models.MskuPurchaseNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/29 8:36
 */
@Mapper
public interface MskuPurchaseNoteMapper {
    /**
     * 根据店铺、区域、msku获取一条数据
     *
     * @param shop
     * @param area
     * @param msku
     * @return
     */
    MskuPurchaseNote getOneInfoByShopAndAreaAndMsku(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);

    List<MskuPurchaseNote> getList(@Param("shop") String shop, @Param("area") String area, @Param("msku") String msku);
}
