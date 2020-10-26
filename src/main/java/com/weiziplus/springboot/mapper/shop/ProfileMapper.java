package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.ShopAreaProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
* 主要是对店铺不同区域的配置文件ID进行操作，配置文件ID是用于获取广告接口数据时必要的属性
* */
@Mapper
public interface ProfileMapper {
    /**
    * 将指定的shop和area和profileId对应并存入数据库
    * */
    int addShopAreaProfileId(@Param("shopId") Long shopId,@Param("areaId") Long areaId,@Param("profileId") String profileId,@Param("regionCode") String regionCode,@Param("status") Integer status);

    /**
     * 获取所有的区域店铺的profileId
     * */
    List<ShopAreaProfile> getAllDatas();

    /**
     * 获取指定的店铺的profileId
     * */
    List<ShopAreaProfile> getDatasByShopId(@Param("shopId")Long shopId);

    /**
     * 获取指定的区域店铺的profileId
     * */
    ShopAreaProfile getDatasByShopIdAreaId(@Param("shopId")Long shopId,@Param("areaId")Long areaId);

    /**
    * 删除所有的ProfilesID
    * */
    int delShopAreaProfileId(Long shopId);

    /**
     * 修改该配置文件是否拉取前一个月数据的状态的状态（1：拉取 0：未拉）
     * */
    int updateProfileStatusByProfileId(@Param("profileId") String profileId);
}
