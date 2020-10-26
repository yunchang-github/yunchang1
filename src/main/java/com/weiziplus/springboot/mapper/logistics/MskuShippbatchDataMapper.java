package com.weiziplus.springboot.mapper.logistics;

import com.weiziplus.springboot.models.MskuShippbatchData;
import com.weiziplus.springboot.models.MskuShippbatchDataItem;
import com.weiziplus.springboot.models.MskuShippbatchItemPack;
import com.weiziplus.springboot.models.MskuShippbatchPack;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author wangzhiqiang
 * @data 2019年8月26日14:28:16
 */
@Mapper
public interface MskuShippbatchDataMapper {
    
    MskuShippbatchData getMskuShippbatchDataByShippNo(String shippNo);

	MskuShippbatchPack getMskuShippbatchPackByPackNo(String packNo);

	MskuShippbatchDataItem getMskuShippbatchDataByDataIdAndSku(@Param("cm")Map<String, Object> conditionMap);

	MskuShippbatchItemPack getMskuShippbatchDataBypackIdAndItemId(@Param("cm")Map<String, Object> conditionMap);

	int insert(MskuShippbatchData mskuShippbatchData);

	int insertItem(MskuShippbatchDataItem mskuShippbatchDataItem);

	int insertMskuShippbatchPack(MskuShippbatchPack mskuShippbatchPack);

	int insertMskuShippbatchItemPack(MskuShippbatchItemPack mskuShippbatchItemPack);
    
}
