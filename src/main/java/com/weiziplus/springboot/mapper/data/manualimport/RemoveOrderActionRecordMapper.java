package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.RemoveOrderActionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/29 11:03
 */
@Mapper
public interface RemoveOrderActionRecordMapper {

    /**
     * 根据订单号和fnsku获取一条数据
     *
     * @param number
     * @param fusku
     * @return
     */
    RemoveOrderActionRecord getOneInfoByTrackingNumberAndFnSku(@Param("number") String number, @Param("fnsku") String fusku);

    List<RemoveOrderActionRecord> getPageList(Map map);
}
