package com.weiziplus.springboot.mapper.sspa;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdvertOriginalDataMapper {

    /**
     * 获取各个表中当前日期的数据数量，用来判断是否已经有数据
     * */
    Integer getDataCountFromOriginalProductAds(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);

    Integer getDataCountFromOriginalCampaigns(@Param("shop") String shop, @Param("area") String area,@Param("date") String date);

    Integer getDataCountFromOriginalAdGroups(@Param("shop") String shop, @Param("area") String area,@Param("date") String date);

    Integer getDataCountFromOriginalTargets(@Param("shop") String shop, @Param("area") String area,@Param("date") String date);

    Integer getDataCountFromOriginalKeywords(@Param("shop") String shop, @Param("area") String area,@Param("date") String date);

    Integer getDataCountFromOriginalAsins(@Param("shop") String shop, @Param("area") String area,@Param("date") String date);

    /**
     * 删除各个表中指定店铺指定区域指定时间的数据
     * */
    void deleteDataFromProductAdsByShopAreaDate(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);

    void deleteDataFromCampaignsByShopAreaDate(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);

    void deleteDataFromAdGroupsByShopAreaDate(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);

    void deleteDataFromTargetsByShopAreaDate(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);

    void deleteDataFromKeywordsByShopAreaDate(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);

    void deleteDataFromAsinsByShopAreaDate(@Param("shop")String shop, @Param("area")String area, @Param("date") String date);
}
