package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.LogisticsBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/29 9:05
 */
@Mapper
public interface LogisticsBillMapper {

    /**
     * 根据物流单号获取一个信息
     *
     * @param number
     * @return
     */
    LogisticsBill getOneInfoByNumber(@Param("number") String number);

    /**
     * 获取列表
     *
     * @param number
     * @param carrier
     * @return
     */
    List<LogisticsBill> getList(@Param("number") String number, @Param("carrier") String carrier);
}
