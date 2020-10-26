package com.weiziplus.springboot.service.datadictionary;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.datadictionary.DataDictionaryMapper;
import com.weiziplus.springboot.models.DataDictionary;
import com.weiziplus.springboot.models.DataDictionaryValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:51
 */
@Service
public class DataDictionaryService extends BaseService {

    @Autowired
    DataDictionaryMapper mapper;

    /**
     * 指标参数code
     */
    public static final String INDEX_PARAMETER_CODE = "index_parameter";

    /**
     * 获取指标参数
     *
     * @return
     */
    public List<DataDictionary> getIndexParameterList() {
        List<DataDictionary> indexParameterList = mapper.getListByCode(INDEX_PARAMETER_CODE);
        for (DataDictionary dataDictionary : indexParameterList) {
            dataDictionary.setValues(mapper.getListByParentId(dataDictionary.getId()));
        }
        return indexParameterList;
    }

    /**
     * 获取指标参数所对应的值（值从小到大排列）
     *
     * @return
     */
    public List<Map<String, Object>> getIndexParameterValuesList() {
        List<DataDictionary> indexParameterList = mapper.getListByCode(INDEX_PARAMETER_CODE);
        List<Map<String, Object>> result = new ArrayList<>(indexParameterList.size());
        for (DataDictionary dataDictionary : indexParameterList) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("value", dataDictionary.getValue());
            List<DataDictionaryValue> values = mapper.getListByParentId(dataDictionary.getId());
            Integer[] list = new Integer[values.size()];
            for (int i = 0; i < values.size(); i++) {
                list[i] = Integer.valueOf(values.get(i).getValue());
            }
            Arrays.sort(list);
            map.put("values", list);
            result.add(map);
        }
        return result;
    }
}
