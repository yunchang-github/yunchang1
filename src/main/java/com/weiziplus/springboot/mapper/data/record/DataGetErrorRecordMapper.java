package com.weiziplus.springboot.mapper.data.record;

import com.weiziplus.springboot.models.DataGetErrorRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:51
 */
@Mapper
public interface DataGetErrorRecordMapper {

    /**
     * 获取数据
     *
     * @param shop
     * @param area
     * @param date
     * @param type
     * @param isHandle
     * @param createTime
     * @return
     */
    List<DataGetErrorRecord> getList(@Param("shop") String shop, @Param("area") String area, @Param("date") String date
            , @Param("type") Integer type, @Param("isHandle") Integer isHandle, @Param("createTime") String createTime);


    /**
     * 根据任务名称获取 错误任务的信息列表
     * @param taskName 任务名
     * @param taskTime 任务的结束时间，精确到天
     * @return
     */
    List<DataGetErrorRecord> getListByTaskName(@Param("taskName")String taskName,@Param("taskTime") String taskTime);

    /**
     * 根据ID修改is_handle状态
     * @param id
     * @param isHandle
     * @return
     */
    Integer updateIsHandle(@Param("id") Long id,@Param("isHandle")Integer isHandle);
}
