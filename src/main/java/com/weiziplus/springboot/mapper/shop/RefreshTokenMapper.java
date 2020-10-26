package com.weiziplus.springboot.mapper.shop;

import com.weiziplus.springboot.models.RefreshTokenDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RefreshTokenMapper {
    /**
     * 添加店铺在某个区域的refreshtoken
     * */
    int addRefreshToken(RefreshTokenDO refreshToken);

    /**
     * 根据shopID和regioncode查找是否有refreshtoken
     * */
    RefreshTokenDO selectRefreshTokenBySellerIdAndRegionCode(@Param("sellerId") String sellerId, @Param("regionCode") String regionCode);

    /**
     * 根据shopID和regioncode查找是否有refreshtoken
     * */
    List<RefreshTokenDO> selectRefreshTokensBySellerId(@Param("sellerId") String sellerId);

}
