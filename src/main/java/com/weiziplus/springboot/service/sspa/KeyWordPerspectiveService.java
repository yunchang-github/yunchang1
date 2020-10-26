package com.weiziplus.springboot.service.sspa;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsTargetingReportMapper;
import com.weiziplus.springboot.utils.ListUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/7/3 17:39
 */
@Service
public class KeyWordPerspectiveService extends BaseService {

    @Autowired
    SponsoredProductsTargetingReportMapper sponsoredProductsTargetingReportMapper;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
//    public ResultUtil getPageList(Integer pageNum, Integer pageSize, Map map) {
//        List dataList = sponsoredProductsTargetingReportMapper.getKeyWordPerspectivePageList(map);
//        //这个initmap需要改成所有数据为0的map，下面方法传的date参数要指定
//        if(null != dataList || dataList.size() != 0){
//            Map initMap = (Map) dataList.get(0);
//            List list = ListUtil.mergeList(dataList,"targeting", (String) map.get("date"),initMap);
//            PageUtil pageUtil = PageUtil.Pagination(pageNum,pageSize,list);
//            return ResultUtil.success(pageUtil);
//        }
//        return ResultUtil.success("暂无数据");
//    }


    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getKeyWordPageList(Integer pageNum, Integer pageSize, Map map) {
        String dateSign = MapUtils.getString(map,"type");
        List<Map<String,Object>> dataLists = sponsoredProductsTargetingReportMapper.getKeyWordPerspectivePageList(map);
        if(null != dataLists && dataLists.size() > 0){
            // 最终结果集合
            List<List<Map<String,Object>>> finalDataMapLists = new ArrayList<>();
            // 结果集合
            List<List<Map<String,Object>>> dataMapLists = new ArrayList<>();
            // 取出结果中的全部targeting值
            List<String> targetingNames = new ArrayList<>();
            for (Map<String,Object> dataMap: dataLists) {
                targetingNames.add(MapUtils.getString(dataMap,"targeting"));
            }
//             对campaign进行去重
            LinkedList<String> sortCampaignLinkedList=new LinkedList<>();
            Iterator<String> it=targetingNames.iterator();
            while (it.hasNext()){
                String str=it.next();
                if (!sortCampaignLinkedList.contains(str)){
                    sortCampaignLinkedList.add(str);
                }
            }
            // 取出一个map 使其为空，作为空map模板使用
            Map<String,Object> initMap = (Map) dataLists.get(0);
            Map<String,Object> nullMap = new HashMap<>();
            for(Map.Entry<String, Object> a:initMap.entrySet()){
                nullMap.put(a.getKey(),0);
            }
            // 对参数日期数组进行处理
            List<String>  dateArrayList = (List<String>) map.get("paramDateArry");
            // 记录原始的数据顺序
            List<Integer>  dateSortWeekList = new ArrayList<>();
            for (String date : dateArrayList) {
                dateSortWeekList.add(Integer.valueOf(date.substring(4,6)));
            }
            // 根据campaign来分组，将不满四周的补0
            for (String targetingName: sortCampaignLinkedList) {
                // 临时存放CampaignList
                List<Map<String,Object>>  tempCampaignList = new ArrayList<>();
                // 存放tempCampaignList中的week
                List<Integer> temDateWeek = new ArrayList<>();
                for (Map<String,Object> dataMap: dataLists) {
                    if(targetingName.equals(dataMap.get("targeting"))){
                        tempCampaignList.add(dataMap);
                        temDateWeek.add(MapUtils.getInteger(dataMap,dateSign));
                    }
                }
                if(tempCampaignList.size()!=4){
                    for(Integer dateWeek:dateSortWeekList){
                        flag:  for (Map<String,Object> temMap: tempCampaignList){
                            if(tempCampaignList.size() == 4){
                                break;
                            }
                            if(temDateWeek.contains(dateWeek)){
                                continue flag;
                            }
                            if(!dateWeek.equals(MapUtils.getInteger(temMap,dateSign))){
                                nullMap.put(dateSign,dateWeek);
                                nullMap.put("targeting",targetingName);
                                Map<String,Object> newTemMap = new HashMap<>(1);
                                newTemMap.putAll(nullMap);
                                tempCampaignList.add(newTemMap);
                                break;
                            }
                        }
                    }
                }
                dataMapLists.add(tempCampaignList);
            }
            List<String> sortList = sortedByFields(MapUtils.getString(map,"sortName"),dateSortWeekList.get(0),dataMapLists,"targeting", MapUtils.getString(map,"sortRule"),dateSign);
            if(null != sortList && sortList.size()>0){
                flag:for (String nameField : sortList) {
                    // 临时存放CampaignList
                    List<Map<String,Object>>  tempCampaignList = new ArrayList<>();
                    // 临时存放CampaignList 用于日期排序
                    List<Map<String,Object>>  tempDateCampaignList = new ArrayList<>();
                    for (List<Map<String,Object>> list :dataMapLists) {
                        for (Map<String,Object> secondMap: list) {
                            if(nameField.equals(MapUtils.getString(secondMap,"targeting"))){
                                tempCampaignList.add(secondMap);
                            }
                        }
                        if(4 == tempCampaignList.size()){
                            for (Integer weekDate: dateSortWeekList) {
                                for (Map<String,Object> dateMap: tempCampaignList) {
                                    if(weekDate.equals(MapUtils.getInteger(dateMap,dateSign))){
                                        tempDateCampaignList.add(dateMap);
                                    }
                                }
                                if(tempDateCampaignList.size()==4){
                                    finalDataMapLists.add(tempDateCampaignList);
                                    continue flag;
                                }
                            }
                        }
                    }
                }
            }else{
                return ResultUtil.error("暂无数据");
            }
            PageUtil pageUtil = PageUtil.Pagination(pageNum, pageSize, finalDataMapLists);
            return ResultUtil.success(pageUtil);
        }else{
            return ResultUtil.success("暂无数据");
        }
    }


    /**
     *  List<List<Map<String,Object>>>类型的集合，把该集合中的fields提取出来放在map中并根据sortRule进行排序，根据week条件
     *  nameFields作为map的key
     * @param fields  需要排序的字段
     * @param week 指定周
     * @param dataList  集合
     * @param nameFields  比如 campaignName
     * @param sortRule 排序规则
     * @return
     */
    public List<String> sortedByFields(String fields, Integer week, List<List<Map<String,Object>>> dataList, String nameFields, String sortRule,String dateSign){
        Map<String,Double> resultFloatDataMap = new LinkedHashMap<>();
        Map<String,Integer> resultIntegerDataMap = new LinkedHashMap<>();
        if(null == fields || "" == fields ){
            fields = "sumImpressions";
        }
        for (List<Map<String, Object>> dataMapList: dataList) {
            for (Map<String,Object> sortFieldsMap: dataMapList) {
                if(week.equals(MapUtils.getInteger(sortFieldsMap,dateSign))){
                    if(MapUtils.getObject(sortFieldsMap,fields).toString().contains(".")){
                        resultFloatDataMap.put(MapUtils.getString(sortFieldsMap,nameFields),MapUtils.getDouble(sortFieldsMap,fields));
                    }
//                    else {
                        resultIntegerDataMap.put(MapUtils.getString(sortFieldsMap, nameFields), MapUtils.getInteger(sortFieldsMap, fields));
//                    }
                }
            }
        }
        if(0 != resultIntegerDataMap.size()){
            return getSortIntegerMap(resultIntegerDataMap,sortRule);
        }
        if(0 != resultFloatDataMap.size()){
            return getSortFloatMap(resultFloatDataMap,sortRule);
        }
        return new LinkedList<>();
    }


    /**
     * 对值为Float类型的map进行排序
     * @param map
     * @param sortRule 排序规则
     * @return
     */
    public List<String> getSortFloatMap(Map<String,Double> map,String sortRule){
        if("desc" .equals(sortRule)){
            List<Map.Entry<String, Double>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Double> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }else if("asc" .equals(sortRule)){
            List<Map.Entry<String, Double>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                //升序排序
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Double> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }else {
            List<Map.Entry<String, Double>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Double> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }
    }


    /**
     * 对值为Integer类型的map进行排序
     * @param map
     * @param sortRule 排序规则
     * @return
     */
    public List<String> getSortIntegerMap(Map<String,Integer> map,String sortRule){
        if("desc".equals(sortRule)){
            List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Integer> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }else if("asc".equals(sortRule)){
            List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                //升序排序
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Integer> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }else {
            List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Integer> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }
    }


    /**
     * 对List<Map<String, Object>>类型中的Map中的某个value进行排序-----降序
     * @return
     */
    public List<Map<String, Object>> returnSortListDESC(List<Map<String, Object>> list , String valueName){
        if (null == list || list.size() == 0 ){
            return new ArrayList<>();
        }else if(StringUtils.isEmpty(valueName)){
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    if(o1.get("sumofImpression").toString().contains(".")){
                        Float name1 = Float.valueOf(o1.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的一个
                        Float name2 = Float.valueOf(o2.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    }else {
                        Integer name1 = Integer.valueOf(o1.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的一个
                        Integer name2 = Integer.valueOf(o2.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    }
                }
            });
        }else{
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    if(o1.get(valueName).toString().contains(".")){
                        Float name1 = Float.valueOf(o1.get(valueName).toString()) ; //name1是从你list里面拿出来的一个
                        Float name2 = Float.valueOf(o2.get(valueName).toString()) ; //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    }else{
                        Integer name1 = Integer.valueOf(o1.get(valueName).toString()) ; //name1是从你list里面拿出来的一个
                        Integer name2 = Integer.valueOf(o2.get(valueName).toString()) ; //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    }
                }
            });
        }
        return list;
    }

    /**
     * 对List<Map<String, Object>>类型中的Map中的某个value进行排序-----升序
     * @return
     */
    public List<Map<String, Object>> returnSortListASC(List<Map<String, Object>> list , String valueName){
        if (null == list || list.size() == 0 ){
            return new ArrayList<>();
        }else if(StringUtils.isEmpty(valueName)){
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    if(o1.get("sumofImpression").toString().contains(".")){
                        Float name1 = Float.valueOf(o1.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的一个
                        Float name2 = Float.valueOf(o2.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的第二个name
                        return name1.compareTo(name2);
                    }else {
                        Integer name1 = Integer.valueOf(o1.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的一个
                        Integer name2 = Integer.valueOf(o2.get("sumofImpression").toString()) ; //name1是从你list里面拿出来的第二个name
                        return name1.compareTo(name2);
                    }
                }
            });
        }else{
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    if(o1.get(valueName).toString().contains(".")){
                        Float name1 = Float.valueOf(o1.get(valueName).toString()) ; //name1是从你list里面拿出来的一个
                        Float name2 = Float.valueOf(o2.get(valueName).toString()) ; //name1是从你list里面拿出来的第二个name
                        return name1.compareTo(name2);
                    }else{
                        Integer name1 = Integer.valueOf(o1.get(valueName).toString()) ; //name1是从你list里面拿出来的一个
                        Integer name2 = Integer.valueOf(o2.get(valueName).toString()) ; //name1是从你list里面拿出来的第二个name
                        return name1.compareTo(name2);
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取列表数据
     * @return
     */
    public ResultUtil getList(Map map) {
        return ResultUtil.success(sponsoredProductsTargetingReportMapper.getKeyWordPerspectiveList(map));
    }

    /**
     * 获取总数据
     *
     * @return
     */
    public ResultUtil getSum() {
        return ResultUtil.success(sponsoredProductsTargetingReportMapper.getKeyWordPerspectiveSumData());
    }

    /**
     * 获取campaign_name
     *
     * @return
     */
    public ResultUtil getCampaignName(String sellerId,String shopArea,String adGroupName) {
        List<String> adGroupNameList = StringUtil.parseReqParamToArrayUseTrim(adGroupName);
        return ResultUtil.success(sponsoredProductsTargetingReportMapper.getCampaignName(sellerId,shopArea,adGroupNameList));
    }

    /**
     * 获取ad_group_name
     *
     * @return
     */
    public ResultUtil getAdGroupName(String sellerId,String shopArea,String campaignName) {
        List<String> adGroupNameList = StringUtil.parseReqParamToArrayUseTrim(campaignName);
        return ResultUtil.success(sponsoredProductsTargetingReportMapper.getAdGroupName(sellerId,shopArea,adGroupNameList));
    }


}
