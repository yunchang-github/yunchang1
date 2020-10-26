package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayPromotion;

public interface PayPromotionMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayPromotion record);

    int insertSelective(PayPromotion record);

    PayPromotion selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayPromotion record);

    int updateByPrimaryKey(PayPromotion record);
}