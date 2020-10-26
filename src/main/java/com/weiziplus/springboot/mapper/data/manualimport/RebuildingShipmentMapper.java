package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.RebuildingShipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/26 17:50
 */
@Mapper
public interface RebuildingShipmentMapper {

    /**
     * 根据新id获取一条数据
     *
     * @param newId
     * @return
     */
    RebuildingShipment getOneInfoNewShipmentId(@Param("newId") String newId);

    List<RebuildingShipment> getPageList(Map map);
}
