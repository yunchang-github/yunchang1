package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.UsXicangGoodsIssuedRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/29 11:45
 */
@Mapper
public interface UsXicangGoodsIssuedRecordsMapper {

    /**
     * 根据msku、fnsku、shipmentId、箱号获取一条数据
     *
     * @param msku
     * @param fnsku
     * @param shipmentId
     * @param caseNumber
     * @return
     */
    UsXicangGoodsIssuedRecords getOneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber(@Param("msku") String msku, @Param("fusku") String fnsku
            , @Param("shipmentId") String shipmentId, @Param("caseNumber") String caseNumber);

    List<UsXicangGoodsIssuedRecords> getPageList(Map map);
}
