package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.UserShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/12 9:33
 */
@Mapper
public interface UserShopMapper {

    /**
     * 根据用户id获取所有网店列表
     *
     * @param userId
     * @return
     */
    List<Map<String, Object>> getAllShopListByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id删除用户网店列表
     *
     * @param userId
     * @return
     */
    int deleteShopListByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id和网店id新增
     *
     * @param userId
     * @param shopIds
     * @return
     */
    int addShopListByUserIdAndShopIds(@Param("userId") Long userId, @Param("shopIds") Long[] shopIds);

    /**
     * 根据网店id获取一个关联信息
     *
     * @param shopId
     * @return
     */
    UserShop getOneInfoByShopId(@Param("shopId") Long shopId);

    /**
     * 根据网店id获取用户列表
     *
     * @param shopId
     * @return
     */
    List<Map<String, Object>> getUserListByShopId(@Param("shopId") Long shopId);

    /**
     * 根据用户id获取网店列表
     *
     * @param userId
     * @return
     */
    List<Map<String, Object>> getShopListByUserId(@Param("userId") Long userId);
}
