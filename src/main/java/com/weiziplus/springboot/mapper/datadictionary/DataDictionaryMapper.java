package com.weiziplus.springboot.mapper.datadictionary;

import com.weiziplus.springboot.models.DataDictionary;
import com.weiziplus.springboot.models.DataDictionaryValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:51
 */
@Mapper
public interface DataDictionaryMapper {

    /**
     * 根据code获取列表
     *
     * @param code
     * @return
     */
    List<DataDictionary> getListByCode(@Param("code") String code);

    /**
     * 根据parentId获取列表
     *
     * @param parentId
     * @return
     */
    List<DataDictionaryValue> getListByParentId(@Param("parentId") Long parentId);
}
