package com.weiziplus.springboot.mapper.seckill;

import com.weiziplus.springboot.models.SeckillItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/8/29 11:38
 */
@Mapper
public interface SeckillItemMapper {

    /**
     * 根据SeckillId删除数据
     *
     * @param seckillId
     * @return
     */
    int deleteItemBySeckillId(@Param("seckillId") String seckillId);

    /**
     * 根据seckillId获取item列表
     *
     * @param seckillId
     * @return
     */
    List<SeckillItem> getItemListBySeckillId(@Param("seckillId") String seckillId);
}
