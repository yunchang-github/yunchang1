package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.GoodsCorrespondenceLogisticsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/29 10:03
 */
@Mapper
public interface GoodsCorrespondenceLogisticsInfoMapper {

    /**
     * 根据箱号获取一条数据
     *
     * @param number
     * @return
     */
    GoodsCorrespondenceLogisticsInfo getOneInfoByCaseNumber(@Param("number") String number);

    List <GoodsCorrespondenceLogisticsInfo> getList(Map map);
}
