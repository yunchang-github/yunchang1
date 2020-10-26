package com.weiziplus.springboot.service.sspa;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsAdvertisedProductReportMapper;
import com.weiziplus.springboot.utils.MapSortUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/7/3 15:32
 */
@Service
public class CampaignPerspectiveService extends BaseService {

    @Autowired
    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;

    private static final String  CAMPAIGNNAME = "campaignName";

    /**
     * 获取分页数据
     * @return
     */
//    public ResultUtil getPageList(Map map) {
//        int pageNum = MapUtils.getInteger(map, "pageNum");
//        int pageSize = MapUtils.getInteger(map,"pageSize");
//        List dataList = sponsoredProductsAdvertisedProductReportMapper.getCampaignPerspectivePageList(map);
//        //这个initmap需要改成所有数据为0的map，下面方法传的date参数要指定    未对dataList进行判空，若为空则会报错
//        if(null != dataList || dataList.size() != 0) {
//            Map initMap = (Map) dataList.get(0);
//            List list = ListUtil.mergeList(dataList, "campaignName", (String) map.get("date"), initMap);
//            PageUtil pageUtil = PageUtil.Pagination(pageNum, pageSize, list);
//            return ResultUtil.success(pageUtil);
//        }
//        return ResultUtil.success("暂无数据");
//    }

    /**
     * 获取Campaign分页数据
     * @return
     */
    public ResultUtil getCampaignPageList(Map map) {
        String dateSign = MapUtils.getString(map,"type");
        int pageNum = MapUtils.getInteger(map, "pageNum");
        int pageSize = MapUtils.getInteger(map,"pageSize");
        // 返回结果
        List<Map<String, Object>>  dataLists = sponsoredProductsAdvertisedProductReportMapper.getCampaignPerspectivePageSortList(map);
        if(null != dataLists && dataLists.size() > 0){
            // 最终结果集合
            List<List<Map<String,Object>>> finalDataMapLists = new ArrayList<>();
            // 结果集合
            List<List<Map<String,Object>>> dataMapLists = new ArrayList<>();
            // 取出结果中的全部campaignName值
            List<String> campaignNames = new ArrayList<>();
            for (Map<String,Object> dataMap: dataLists) {
                campaignNames.add(MapUtils.getString(dataMap,"campaignName"));
            }
//             对campaign进行去重
            LinkedList<String> sortCampaignLinkedList=new LinkedList<>();
            Iterator<String> it=campaignNames.iterator();
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
                    switch (a.getKey()) {
                        case "ctr": nullMap.put(a.getKey(),0.0);
                        break;
                        case "sumAllAttributedSales1d": nullMap.put(a.getKey(),0.0);
                        break;
                        case "sumCost": nullMap.put(a.getKey(),0.0);
                        break;
                        case "sumAttributedSales1d": nullMap.put(a.getKey(),0.0);
                        break;
                        case "acos": nullMap.put(a.getKey(),0.0);
                        break;
                        case "sumImpressions": nullMap.put(a.getKey(),0);
                        break;
                        case "sumAttributedSales1dSameSku": nullMap.put(a.getKey(),0.0);
                        break;
                        case "sales": nullMap.put(a.getKey(),0.0);
                        break;
                        case "cr": nullMap.put(a.getKey(),0.0);
                        break;
                        case "sumClicks": nullMap.put(a.getKey(),0);
                        break;
                        case "sumAttributedUnitsOrdered1d": nullMap.put(a.getKey(),0);
                        break;
                        case "cpc": nullMap.put(a.getKey(),0.0);
                        break;
                        default: nullMap.put(a.getKey(),0.0);
                }
            }
            // 对参数日期数组进行处理
            List<String>  dateArrayList = (List<String>) map.get("paramDateArry");
            // 记录原始的数据顺序
            List<Integer>  dateSortWeekList = new ArrayList<>();
            for (String date : dateArrayList) {
                dateSortWeekList.add(Integer.valueOf(date.substring(4,6)));
            }
            // 根据campaign来分组，将不满四周的补0
            for (String campaignName: sortCampaignLinkedList) {
                // 临时存放CampaignList
                List<Map<String,Object>>  tempCampaignList = new ArrayList<>();
                // 存放tempCampaignList中的week
                List<Integer> temDateWeek = new ArrayList<>();
                for (Map<String,Object> dataMap: dataLists) {
                    if(campaignName.equals(dataMap.get("campaignName"))){
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
                                nullMap.put("campaignName",campaignName);
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
            List<String> sortList = sortedByFields(MapUtils.getString(map,"sortName"),dateSortWeekList.get(0),dataMapLists,"campaignName", MapUtils.getString(map,"sortRule"),dateSign);
            if(null != sortList && sortList.size()>0){
                flag:for (String nameField : sortList) {
                    // 临时存放CampaignList
                    List<Map<String,Object>>  tempCampaignList = new ArrayList<>();
                    // 临时存放CampaignList 用于日期排序
                    List<Map<String,Object>>  tempDateCampaignList = new ArrayList<>();
                    for (List<Map<String,Object>> list :dataMapLists) {
                        for (Map<String,Object> secondMap: list) {
                            if(nameField.equals(MapUtils.getString(secondMap,"campaignName"))){
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
     * 对Integer类型的字段数据进行排序
     * @param map
     * @param sortRule
     * @return
     */
    public Map<String,? extends Number> mapSortByIntegerValue(Map<String,Integer> map,String sortRule){
        if(0 != map.size() && "asc".equals(sortRule)){
            MapSortUtil.sortByValueAsc(map);
            return map;
        }else if(0 != map.size() && "desc".equals(sortRule)){
            MapSortUtil.sortByValueDesc(map);
            return map;
        }else {
            MapSortUtil.sortByValueDesc(map);
            return map;
        }
    }

    /**
     * 根据map的值来进行排序
     * @param map
     * @param sortRule 排序规则
     * @return
     */
    public Map<String,Object> sortMap(Map<String,Object> map,String sortRule){
        List<Map.Entry<String, Object>> listEntry = new ArrayList<>();
        listEntry.addAll(map.entrySet());
        Collections.sort(listEntry, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                if (null == sortRule || "" == sortRule) {
                    if (o1.getValue().toString().contains(".")) {
                        Float name1 = Float.valueOf(o1.getValue().toString());
                        Float name2 = Float.valueOf(o2.getValue().toString());
                        return name2.compareTo(name1);
                    } else {
                        Integer name1 = Integer.valueOf(o1.getValue().toString());
                        Integer name2 = Integer.valueOf(o2.getValue().toString());
                        return name2.compareTo(name1);
                    }
                } else if (sortRule == "asc") {
                    if (o1.getValue().toString().contains(".")) {
                        Float name1 = Float.valueOf(o1.getValue().toString());
                        Float name2 = Float.valueOf(o2.getValue().toString());
                        return name1.compareTo(name2);
                    } else {
                        Integer name1 = Integer.valueOf(o1.getValue().toString());
                        Integer name2 = Integer.valueOf(o2.getValue().toString());
                        return name1.compareTo(name2);
                    }
                } else {
                    if (o1.getValue().toString().contains(".")) {
                        Float name1 = Float.valueOf(o1.getValue().toString()); //name1是从你list里面拿出来的一个
                        Float name2 = Float.valueOf(o2.getValue().toString()); //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    } else {
                        Integer name1 = Integer.valueOf(o1.getValue().toString()); //name1是从你list里面拿出来的一个
                        Integer name2 = Integer.valueOf(o2.getValue().toString()); //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    }
                }
            }
        });
        return  map;
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
                    if(o1.get("sumImpressions").toString().contains(".")){
                        Float name1 = Float.valueOf(o1.get("sumImpressions").toString()) ; //name1是从你list里面拿出来的一个
                        Float name2 = Float.valueOf(o2.get("sumImpressions").toString()) ; //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
                    }else {
                        Integer name1 = Integer.valueOf(o1.get("sumImpressions").toString()) ; //name1是从你list里面拿出来的一个
                        Integer name2 = Integer.valueOf(o2.get("sumImpressions").toString()) ; //name1是从你list里面拿出来的第二个name
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
     *
     * @return
     */
    public ResultUtil getList(Map map) {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getCampaignPerspectiveList(map));
    }

    /**
     * 获取Advertised ASIN
     *
     * @return
     */
    public ResultUtil getAdvertisedAsin(String sellerId,String shopArea) {
        return ResultUtil.success(sponsoredProductsAdvertisedProductReportMapper.getAdvertisedAsin(sellerId,shopArea));
    }
}
