package com.weiziplus.springboot.mapper.caravan;

import com.weiziplus.springboot.models.StorageLogData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/9/6 10:55
 */
@Mapper
public interface StorageLogDataMapper {

    /**
     * 根据流水id获取一条数据
     *
     * @param logId
     * @return
     */
    StorageLogData getOneInfoByLogId(@Param("logId") Long logId);
}
