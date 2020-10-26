package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.ShipmentUsWestWarehouseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/29 14:44
 */
@Mapper
public interface ShipmentUsWestWarehouseRecordMapper {

    /**
     * 根据msku、fnsku、箱号获取一条数据
     *
     * @param msku
     * @param fnsku
     * @param caseNumber
     * @return
     */
    ShipmentUsWestWarehouseRecord getOneInfoByMskuAndFnSkuAndCaseNumber(@Param("msku") String msku, @Param("fnsku") String fnsku, @Param("caseNumber") String caseNumber);

    List<ShipmentUsWestWarehouseRecord> getPageList(Map map);

    ShipmentUsWestWarehouseRecord getOneInfoByCondition(@Param("msku")String msku, @Param("fnsku")String fnsku, @Param("caseNumber") String caseNumber, @Param("shipmentId") String shipmentId, @Param("shop") String shop, @Param("area") String area);
}
