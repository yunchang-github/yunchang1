package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.LogisticsTimeLimitation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/26 17:00
 */
@Mapper
public interface LogisticsTimeLimitationMapper {

    /**
     * 根据物流方式获取一条数据
     *
     * @param mode
     * @return
     */
    LogisticsTimeLimitation getOneInfoByMode(@Param("mode") String mode);

    /**
     * 获取列表
     *
     * @param code
     * @param mode
     * @param type
     * @return
     */
    List<LogisticsTimeLimitation> getList(@Param("code") String code, @Param("mode") String mode, @Param("type") String type);
}
