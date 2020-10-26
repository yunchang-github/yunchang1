package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.ReturnReasonClassification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/26 16:42
 */
@Mapper
public interface ReturnReasonClassificationMapper {

    /**
     * 根据中文原因和英文原因获取一个数据
     *
     * @param zh
     * @param eg
     * @return
     */
    ReturnReasonClassification getOneInfoByZhReasonAndEgReason(@Param("zh") String zh, @Param("eg") String eg);
}
