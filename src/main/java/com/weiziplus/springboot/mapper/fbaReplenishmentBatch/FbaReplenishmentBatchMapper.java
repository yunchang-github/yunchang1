package com.weiziplus.springboot.mapper.fbaReplenishmentBatch;

import com.weiziplus.springboot.models.DO.FbaReplenishmentBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FbaReplenishmentBatchMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FbaReplenishmentBatch record);

    int insertSelective(FbaReplenishmentBatch record);

    FbaReplenishmentBatch selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FbaReplenishmentBatch record);

    int updateByPrimaryKey(FbaReplenishmentBatch record);

    /**
     * 根据补货批次号和本地sku做模糊查询
     * */
    List<FbaReplenishmentBatch> selectDataByReplenishmentBatchNoAndLocalSku(@Param("replenishmentBatchNo")String replenishmentBatchNo,@Param("localSku")String localSku );
}