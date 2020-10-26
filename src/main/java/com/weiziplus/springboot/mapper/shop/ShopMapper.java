package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.DO.AuthorizationDO;
import com.weiziplus.springboot.models.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/11 9:47
 */
@Mapper
public interface ShopMapper {

    /**
     * 获取列表
     *
     * @param name
     * @param createTime
     * @return
     */
    List<Shop> getList(@Param("name") String name, @Param("createTime") String createTime);

    /**
     * 获取所有列表
     *
     * @return
     */
    List<Shop> getAllList();

    /**
     * 根据名称获取一个网店信息
     *
     * @param name
     * @return
     */
    Shop getOneInfoByName(@Param("name") String name);

    /**
     * 根据SellerId获取一个网店信息
     *
     * @param sellerId
     * @return
     */
    Shop getOneInfoBySellerId(@Param("sellerId") String sellerId);

    /**
     * 获取授权后的店铺信息
     * @return
     */
    List<Shop> getAuthedShopList();

    /**
     * 根据shopID获取一个网店信息
     * */
    Shop getOneInfoByShopId(@Param("shopId") Long shopId);

    /**
     * 根据shopID获取一个网店信息
     * */
    Shop insertShopInfo(@Param("authorizationDO") AuthorizationDO authorizationDO);
}
