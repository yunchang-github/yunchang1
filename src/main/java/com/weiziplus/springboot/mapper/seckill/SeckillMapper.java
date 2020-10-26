package com.weiziplus.springboot.mapper.seckill;

import com.weiziplus.springboot.models.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/8/29 11:38
 */
@Mapper
public interface SeckillMapper {

    /**
     * 根据CampaignId获取一条数据
     *
     * @param campaignId
     * @return
     */
   // Seckill getOneInfoByCampaignId(@Param("campaignId") String campaignId);

    /**
     * 根据网店和区域获取秒杀总数量
     *
     * @param shop
     * @param area
     * @return
     */
  //  List<Map<String, Object>> getSeckillNumByShopAndArea(@Param("shop") String shop, @Param("area") String area);
}
