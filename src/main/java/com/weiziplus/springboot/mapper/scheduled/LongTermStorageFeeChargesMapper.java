package com.weiziplus.springboot.mapper.scheduled;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/31 16:08
 */
@Mapper
public interface LongTermStorageFeeChargesMapper {

    /**
     * 获取最新的时间
     *
     * @return
     */
    String getLatestDay(@Param("shop") String shop, @Param("area") String area);
}
