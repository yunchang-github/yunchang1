package com.weiziplus.springboot.service.sspa;

import com.weiziplus.springboot.mapper.middle.SearchWordDateMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsSearchTermReportMapper;
import com.weiziplus.springboot.models.VO.searchWord.SearchWordVo;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/7/5 11:16
 */
@Service
@Slf4j
public class SearchPerspectiveService {

    @Autowired
    SponsoredProductsSearchTermReportMapper sponsoredProductsSearchTermReportMapper;

    @Autowired
    SearchWordDateMapper searchWordDateMapper;

    @Autowired
    ShopMapper shopMapper;

    @Autowired
    AreaMapper areaMapper;

    @Autowired
    ShopAreaMapper shopAreaMapper;

    private static final String SHOP = "shop";

    private static final String AREA = "area";

    private static final String YEAR = "year";

    private static final String WEEK = "week";

    private static final String MONTH = "month";

    private static final String CUSTOMERSEARCHTERM = "customerSearchTerm";

    private static final String REPORTDATATYPE = "searchWordData";

    private static Integer PAGESIZE = 20;

    private static Integer PAGENUM = 1;



    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
//    public ResultUtil getPageList(Integer pageNum, Integer pageSize,Map map) {
//        List dataList = sponsoredProductsSearchTermReportMapper.getSearchPerspectiveList(map);
//        //这个initmap需要改成所有数据为0的map，下面方法传的date参数要指定
//        Map initMap = (Map) dataList.get(0);
//        List list = ListUtil.mergeList(dataList,"customerSearchTerm", (String) map.get("date"),initMap);
//        PageUtil pageUtil = PageUtil.Pagination(pageNum,pageSize,list);
//        return ResultUtil.success(pageUtil);
//    }


    /**
     * 获取两年内的全部的数据
     * @param paramMap
     */
    public ResultUtil getAllSearchWordList(Map paramMap) throws Exception {
        String campaignName = MapUtils.getString(paramMap,"campaignName");
        List<String> dateArrayList = (List<String>) paramMap.get("paramDateArry");
        String dateSign = MapUtils.getString(paramMap,"type");
        List<SearchWordVo> searchWordVoList = new ArrayList<>();

        String sortName = MapUtils.getString(paramMap,"sortName");
        String sortRule = MapUtils.getString(paramMap,"sortRule");

        String dateYearWeekO = dateArrayList.get(0);
        String dateYearWeekS = dateArrayList.get(1);
        String dateYearWeekT = dateArrayList.get(2);
        String dateYearWeekF = dateArrayList.get(3);

        // 去除字符串中的引号
        String regexp = "\'";
        String shopName = MapUtils.getString(paramMap,"shop");
        shopName = shopName.replaceAll(regexp, "");
        String areaName = MapUtils.getString(paramMap,"area");
        areaName = areaName.replaceAll(regexp,"");

        List<Map<String,Object>> searchDateMapListO = (List<Map<String, Object>>) RedisUtil.get(REPORTDATATYPE+shopName+areaName+dateYearWeekO+dateSign);
        List<Map<String,Object>> searchDateMapListS = (List<Map<String, Object>>) RedisUtil.get(REPORTDATATYPE+shopName+areaName+dateYearWeekS+dateSign);
        List<Map<String,Object>> searchDateMapListT = (List<Map<String, Object>>) RedisUtil.get(REPORTDATATYPE+shopName+areaName+dateYearWeekT+dateSign);
        List<Map<String,Object>> searchDateMapListF = (List<Map<String, Object>>) RedisUtil.get(REPORTDATATYPE+shopName+areaName+dateYearWeekF+dateSign);

        if(null == searchDateMapListO || searchDateMapListO.size()==0){
            log.warn("redis中数据已过期! 该键为"+REPORTDATATYPE+shopName+areaName+dateYearWeekO+dateSign+"");
        }
        for(int index = 0;index<searchDateMapListO.size();index++){
            SearchWordVo searchWordVO = new SearchWordVo();
            searchWordVO.setCustomerSearchTerm(MapUtils.getString(searchDateMapListO.get(index),"customerSearchTerm"));
            searchWordVO.setCtrO(MapUtils.getDouble(searchDateMapListO.get(index),"ctr"));
            searchWordVO.setWeekO(MapUtils.getInteger(searchDateMapListO.get(index),"week"));
            searchWordVO.setYearO(MapUtils.getInteger(searchDateMapListO.get(index),"year"));
            searchWordVO.setSevenDayTotalSalesO(MapUtils.getDouble(searchDateMapListO.get(index),"sevenDayTotalSales"));
            searchWordVO.setSevenDayTotalUnitsO(MapUtils.getInteger(searchDateMapListO.get(index),"sevenDayTotalUnits"));
            searchWordVO.setAcosO(MapUtils.getInteger(searchDateMapListO.get(index),"acos"));
            searchWordVO.setSumofSalesO(MapUtils.getDouble(searchDateMapListO.get(index),"sumofSales"));
            searchWordVO.setSumImpressionsO(MapUtils.getInteger(searchDateMapListO.get(index),"sumImpressions"));
            searchWordVO.setCrO(MapUtils.getDouble(searchDateMapListO.get(index),"cr"));
            searchWordVO.setSumofClickO(MapUtils.getDouble(searchDateMapListO.get(index),"sumofClick"));
            searchWordVO.setSumClicksO(MapUtils.getInteger(searchDateMapListO.get(index),"sumClicks"));
            searchWordVO.setMonthO(MapUtils.getInteger(searchDateMapListO.get(index),"month"));
            searchWordVO.setSevenDayAdvertisedSkuSalesO(MapUtils.getDouble(searchDateMapListO.get(index),"sevenDayAdvertisedSkuSales"));
            searchWordVO.setSpendO(MapUtils.getDouble(searchDateMapListO.get(index),"spend"));
            searchWordVO.setCpcO(MapUtils.getDouble(searchDateMapListO.get(index),"cpc"));
            searchWordVO.setSumImpressionO(MapUtils.getDouble(searchDateMapListO.get(index),"sumImpression"));
            searchWordVO.setCtrS(MapUtils.getDouble(searchDateMapListS.get(index),"ctr"));
            searchWordVO.setWeekS(MapUtils.getInteger(searchDateMapListS.get(index),"week"));
            searchWordVO.setYearS(MapUtils.getInteger(searchDateMapListS.get(index),"year"));
            searchWordVO.setSevenDayTotalSalesS(MapUtils.getDouble(searchDateMapListS.get(index),"sevenDayTotalSales"));
            searchWordVO.setSevenDayTotalUnitsS(MapUtils.getInteger(searchDateMapListS.get(index),"sevenDayTotalUnits"));
            searchWordVO.setAcosS(MapUtils.getInteger(searchDateMapListS.get(index),"acos"));
            searchWordVO.setSumofSalesS(MapUtils.getDouble(searchDateMapListS.get(index),"sumofSales"));
            searchWordVO.setSumImpressionsS(MapUtils.getInteger(searchDateMapListS.get(index),"sumImpressions"));
            searchWordVO.setCrS(MapUtils.getDouble(searchDateMapListS.get(index),"cr"));
            searchWordVO.setSumofClickS(MapUtils.getDouble(searchDateMapListS.get(index),"sumofClick"));
            searchWordVO.setSumClicksS(MapUtils.getInteger(searchDateMapListS.get(index),"sumClicks"));
            searchWordVO.setMonthS(MapUtils.getInteger(searchDateMapListS.get(index),"month"));
            searchWordVO.setSevenDayAdvertisedSkuSalesS(MapUtils.getDouble(searchDateMapListS.get(index),"sevenDayAdvertisedSkuSales"));
            searchWordVO.setSpendS(MapUtils.getDouble(searchDateMapListS.get(index),"spend"));
            searchWordVO.setCpcS(MapUtils.getDouble(searchDateMapListS.get(index),"cpc"));
            searchWordVO.setSumImpressionS(MapUtils.getDouble(searchDateMapListS.get(index),"sumImpression"));
            searchWordVO.setCtrT(MapUtils.getDouble(searchDateMapListT.get(index),"ctr"));
            searchWordVO.setWeekT(MapUtils.getInteger(searchDateMapListT.get(index),"week"));
            searchWordVO.setYearT(MapUtils.getInteger(searchDateMapListT.get(index),"year"));
            searchWordVO.setSevenDayTotalSalesT(MapUtils.getDouble(searchDateMapListT.get(index),"sevenDayTotalSales"));
            searchWordVO.setSevenDayTotalUnitsT(MapUtils.getInteger(searchDateMapListT.get(index),"sevenDayTotalUnits"));
            searchWordVO.setAcosT(MapUtils.getInteger(searchDateMapListT.get(index),"acos"));
            searchWordVO.setSumofSalesT(MapUtils.getDouble(searchDateMapListT.get(index),"sumofSales"));
            searchWordVO.setSumImpressionsT(MapUtils.getInteger(searchDateMapListT.get(index),"sumImpressions"));
            searchWordVO.setCrT(MapUtils.getDouble(searchDateMapListT.get(index),"cr"));
            searchWordVO.setSumofClickT(MapUtils.getDouble(searchDateMapListT.get(index),"sumofClick"));
            searchWordVO.setSumClicksT(MapUtils.getInteger(searchDateMapListT.get(index),"sumClicks"));
            searchWordVO.setMonthT(MapUtils.getInteger(searchDateMapListT.get(index),"month"));
            searchWordVO.setSevenDayAdvertisedSkuSalesT(MapUtils.getDouble(searchDateMapListT.get(index),"sevenDayAdvertisedSkuSales"));
            searchWordVO.setSpendT(MapUtils.getDouble(searchDateMapListT.get(index),"spend"));
            searchWordVO.setCpcT(MapUtils.getDouble(searchDateMapListT.get(index),"cpc"));
            searchWordVO.setSumImpressionT(MapUtils.getDouble(searchDateMapListT.get(index),"sumImpression"));
            searchWordVO.setCtrF(MapUtils.getDouble(searchDateMapListF.get(index),"ctr"));
            searchWordVO.setWeekF(MapUtils.getInteger(searchDateMapListF.get(index),"week"));
            searchWordVO.setYearF(MapUtils.getInteger(searchDateMapListF.get(index),"year"));
            searchWordVO.setSevenDayTotalSalesF(MapUtils.getDouble(searchDateMapListF.get(index),"sevenDayTotalSales"));
            searchWordVO.setSevenDayTotalUnitsF(MapUtils.getInteger(searchDateMapListF.get(index),"sevenDayTotalUnits"));
            searchWordVO.setAcosF(MapUtils.getInteger(searchDateMapListF.get(index),"acos"));
            searchWordVO.setSumofSalesF(MapUtils.getDouble(searchDateMapListF.get(index),"sumofSales"));
            searchWordVO.setSumImpressionsF(MapUtils.getInteger(searchDateMapListF.get(index),"sumImpressions"));
            searchWordVO.setCrF(MapUtils.getDouble(searchDateMapListF.get(index),"cr"));
            searchWordVO.setSumofClickF(MapUtils.getDouble(searchDateMapListF.get(index),"sumofClick"));
            searchWordVO.setSumClicksF(MapUtils.getInteger(searchDateMapListF.get(index),"sumClicks"));
            searchWordVO.setMonthF(MapUtils.getInteger(searchDateMapListF.get(index),"month"));
            searchWordVO.setSevenDayAdvertisedSkuSalesF(MapUtils.getDouble(searchDateMapListF.get(index),"sevenDayAdvertisedSkuSales"));
            searchWordVO.setSpendF(MapUtils.getDouble(searchDateMapListF.get(index),"spend"));
            searchWordVO.setCpcF(MapUtils.getDouble(searchDateMapListF.get(index),"cpc"));
            searchWordVO.setSumImpressionF(MapUtils.getDouble(searchDateMapListF.get(index),"sumImpression"));
            searchWordVoList.add(searchWordVO);
        }
        SortListUtils.sort(searchWordVoList,sortName,sortRule);
        PageUtil pageUtil = PageUtil.Pagination2(1, 20, searchWordVoList,1);
        return ResultUtil.success("ye!",pageUtil);
    }

    /**
     * 使用对象排序
     * @param paramMap
     * @return
     * @throws Exception
     */
    public ResultUtil getAllSearchWordListObject(Map paramMap) throws Exception {
        String sortName = MapUtils.getString(paramMap,"sortName");
        String sortRule = MapUtils.getString(paramMap,"sortRule");
        List<SearchWordVo> searchWordVoList = new ArrayList<>();
        int pageNum = MapUtils.getInteger(paramMap, "pageNum");
        int pageSize = MapUtils.getInteger(paramMap,"pageSize");
        String dateSign = MapUtils.getString(paramMap,"type");
        List<Map<String, Object>> dataLists = sponsoredProductsSearchTermReportMapper.getSearchPerspectiveList(paramMap);
        if(null != dataLists && dataLists.size() > 0){
            // 最终结果集合
            List<List<Map<String,Object>>> finalDataMapLists = new ArrayList<>();
            // 结果集合
            List<List<Map<String,Object>>> dataMapLists = new ArrayList<>();
            // 取出结果中的全部campaignName值
            List<String> campaignNames = new ArrayList<>();
            for (Map<String,Object> dataMap: dataLists) {
                campaignNames.add(MapUtils.getString(dataMap,CUSTOMERSEARCHTERM));
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
                    case "week": nullMap.put(a.getKey(),0);
                        break;
                    case "year": nullMap.put(a.getKey(),0);
                        break;
                    case "sevenDayTotalSales": nullMap.put(a.getKey(),0.0);
                        break;
                    case "sevenDayTotalUnits": nullMap.put(a.getKey(),0.0);
                        break;
                    case "acos": nullMap.put(a.getKey(),0);
                        break;
                    case "sunofSales": nullMap.put(a.getKey(),0.0);
                        break;
                    case "sumImpressions": nullMap.put(a.getKey(),0.0);
                        break;
                    case "cr": nullMap.put(a.getKey(),0.0);
                        break;
                    case "sumofClick": nullMap.put(a.getKey(),0);
                        break;
                    case "sumClicks": nullMap.put(a.getKey(),0);
                        break;
                    case "Month": nullMap.put(a.getKey(),0);
                        break;
                    case "sevenDayAdvertisedSkuSales": nullMap.put(a.getKey(),0.0);
                        break;
                    case "spend": nullMap.put(a.getKey(),0.0);
                        break;
                    case "cpc": nullMap.put(a.getKey(),0.0);
                        break;
                    case "sumImpression": nullMap.put(a.getKey(),0.0);
                        break;
                    default: nullMap.put(a.getKey(),0.0);
                }
            }
            // 对参数日期数组进行处理
            List<String>  dateArrayList = (List<String>) paramMap.get("paramDateArry");
            // 记录原始的数据顺序
            List<Integer>  dateSortWeekList = new ArrayList<>();
            for (String date : dateArrayList) {
                dateSortWeekList.add(Integer.valueOf(date.substring(4,6)));
            }

            List<Map<String,Object>> searchDateMapListO = new ArrayList<>();
            List<Map<String,Object>> searchDateMapListS = new ArrayList<>();
            List<Map<String,Object>> searchDateMapListT = new ArrayList<>();
            List<Map<String,Object>> searchDateMapListF = new ArrayList<>();

            // 根据campaign来分组，将不满四周的补0
            for (String campaignName: sortCampaignLinkedList) {
                // 临时存放CampaignList
                List<Map<String,Object>>  tempCampaignList = new ArrayList<>();
                // 存放tempCampaignList中的week
                List<Integer> temDateWeek = new ArrayList<>();
                for (Map<String,Object> dataMap: dataLists) {
                    if(campaignName.equals(dataMap.get(CUSTOMERSEARCHTERM))){
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
                                nullMap.put(CUSTOMERSEARCHTERM,campaignName);
                                Map<String,Object> newTemMap = new HashMap<>(1);
                                newTemMap.putAll(nullMap);
                                tempCampaignList.add(newTemMap);
                                break;
                            }
                        }
                    }
                }
                for (Map<String,Object> temMapObject:tempCampaignList){
                    if (dateSortWeekList.get(0).equals(MapUtils.getInteger(temMapObject,dateSign))){
                        searchDateMapListO.add(temMapObject);
                    }else if (dateSortWeekList.get(1).equals(MapUtils.getInteger(temMapObject,dateSign))){
                        searchDateMapListS.add(temMapObject);
                    }
                    else if (dateSortWeekList.get(2).equals(MapUtils.getInteger(temMapObject,dateSign))){
                        searchDateMapListT.add(temMapObject);
                    }
                    else  {
                        searchDateMapListF.add(temMapObject);
                    }
                }
//                dataMapLists.add(tempCampaignList);
            }

            for(int index = 0;index<searchDateMapListO.size();index++){
                SearchWordVo searchWordVO = new SearchWordVo();
                searchWordVO.setCustomerSearchTerm(MapUtils.getString(searchDateMapListO.get(index),"customerSearchTerm"));
                searchWordVO.setCtrO(MapUtils.getDouble(searchDateMapListO.get(index),"ctr"));
                searchWordVO.setWeekO(MapUtils.getInteger(searchDateMapListO.get(index),"week"));
                searchWordVO.setYearO(MapUtils.getInteger(searchDateMapListO.get(index),"year"));
                searchWordVO.setSevenDayTotalSalesO(MapUtils.getDouble(searchDateMapListO.get(index),"sevenDayTotalSales"));
                searchWordVO.setSevenDayTotalUnitsO(MapUtils.getInteger(searchDateMapListO.get(index),"sevenDayTotalUnits"));
                searchWordVO.setAcosO(MapUtils.getInteger(searchDateMapListO.get(index),"acos"));
                searchWordVO.setSumofSalesO(MapUtils.getDouble(searchDateMapListO.get(index),"sumofSales"));
                searchWordVO.setSumImpressionsO(MapUtils.getInteger(searchDateMapListO.get(index),"sumImpressions"));
                searchWordVO.setCrO(MapUtils.getDouble(searchDateMapListO.get(index),"cr"));
                searchWordVO.setSumofClickO(MapUtils.getDouble(searchDateMapListO.get(index),"sumofClick"));
                searchWordVO.setSumClicksO(MapUtils.getInteger(searchDateMapListO.get(index),"sumClicks"));
                searchWordVO.setMonthO(MapUtils.getInteger(searchDateMapListO.get(index),"month"));
                searchWordVO.setSevenDayAdvertisedSkuSalesO(MapUtils.getDouble(searchDateMapListO.get(index),"sevenDayAdvertisedSkuSales"));
                searchWordVO.setSpendO(MapUtils.getDouble(searchDateMapListO.get(index),"spend"));
                searchWordVO.setCpcO(MapUtils.getDouble(searchDateMapListO.get(index),"cpc"));
                searchWordVO.setSumImpressionO(MapUtils.getDouble(searchDateMapListO.get(index),"sumImpression"));
                searchWordVO.setCtrS(MapUtils.getDouble(searchDateMapListS.get(index),"ctr"));
                searchWordVO.setWeekS(MapUtils.getInteger(searchDateMapListS.get(index),"week"));
                searchWordVO.setYearS(MapUtils.getInteger(searchDateMapListS.get(index),"year"));
                searchWordVO.setSevenDayTotalSalesS(MapUtils.getDouble(searchDateMapListS.get(index),"sevenDayTotalSales"));
                searchWordVO.setSevenDayTotalUnitsS(MapUtils.getInteger(searchDateMapListS.get(index),"sevenDayTotalUnits"));
                searchWordVO.setAcosS(MapUtils.getInteger(searchDateMapListS.get(index),"acos"));
                searchWordVO.setSumofSalesS(MapUtils.getDouble(searchDateMapListS.get(index),"sumofSales"));
                searchWordVO.setSumImpressionsS(MapUtils.getInteger(searchDateMapListS.get(index),"sumImpressions"));
                searchWordVO.setCrS(MapUtils.getDouble(searchDateMapListS.get(index),"cr"));
                searchWordVO.setSumofClickS(MapUtils.getDouble(searchDateMapListS.get(index),"sumofClick"));
                searchWordVO.setSumClicksS(MapUtils.getInteger(searchDateMapListS.get(index),"sumClicks"));
                searchWordVO.setMonthS(MapUtils.getInteger(searchDateMapListS.get(index),"month"));
                searchWordVO.setSevenDayAdvertisedSkuSalesS(MapUtils.getDouble(searchDateMapListS.get(index),"sevenDayAdvertisedSkuSales"));
                searchWordVO.setSpendS(MapUtils.getDouble(searchDateMapListS.get(index),"spend"));
                searchWordVO.setCpcS(MapUtils.getDouble(searchDateMapListS.get(index),"cpc"));
                searchWordVO.setSumImpressionS(MapUtils.getDouble(searchDateMapListS.get(index),"sumImpression"));
                searchWordVO.setCtrT(MapUtils.getDouble(searchDateMapListT.get(index),"ctr"));
                searchWordVO.setWeekT(MapUtils.getInteger(searchDateMapListT.get(index),"week"));
                searchWordVO.setYearT(MapUtils.getInteger(searchDateMapListT.get(index),"year"));
                searchWordVO.setSevenDayTotalSalesT(MapUtils.getDouble(searchDateMapListT.get(index),"sevenDayTotalSales"));
                searchWordVO.setSevenDayTotalUnitsT(MapUtils.getInteger(searchDateMapListT.get(index),"sevenDayTotalUnits"));
                searchWordVO.setAcosT(MapUtils.getInteger(searchDateMapListT.get(index),"acos"));
                searchWordVO.setSumofSalesT(MapUtils.getDouble(searchDateMapListT.get(index),"sumofSales"));
                searchWordVO.setSumImpressionsT(MapUtils.getInteger(searchDateMapListT.get(index),"sumImpressions"));
                searchWordVO.setCrT(MapUtils.getDouble(searchDateMapListT.get(index),"cr"));
                searchWordVO.setSumofClickT(MapUtils.getDouble(searchDateMapListT.get(index),"sumofClick"));
                searchWordVO.setSumClicksT(MapUtils.getInteger(searchDateMapListT.get(index),"sumClicks"));
                searchWordVO.setMonthT(MapUtils.getInteger(searchDateMapListT.get(index),"month"));
                searchWordVO.setSevenDayAdvertisedSkuSalesT(MapUtils.getDouble(searchDateMapListT.get(index),"sevenDayAdvertisedSkuSales"));
                searchWordVO.setSpendT(MapUtils.getDouble(searchDateMapListT.get(index),"spend"));
                searchWordVO.setCpcT(MapUtils.getDouble(searchDateMapListT.get(index),"cpc"));
                searchWordVO.setSumImpressionT(MapUtils.getDouble(searchDateMapListT.get(index),"sumImpression"));
                searchWordVO.setCtrF(MapUtils.getDouble(searchDateMapListF.get(index),"ctr"));
                searchWordVO.setWeekF(MapUtils.getInteger(searchDateMapListF.get(index),"week"));
                searchWordVO.setYearF(MapUtils.getInteger(searchDateMapListF.get(index),"year"));
                searchWordVO.setSevenDayTotalSalesF(MapUtils.getDouble(searchDateMapListF.get(index),"sevenDayTotalSales"));
                searchWordVO.setSevenDayTotalUnitsF(MapUtils.getInteger(searchDateMapListF.get(index),"sevenDayTotalUnits"));
                searchWordVO.setAcosF(MapUtils.getInteger(searchDateMapListF.get(index),"acos"));
                searchWordVO.setSumofSalesF(MapUtils.getDouble(searchDateMapListF.get(index),"sumofSales"));
                searchWordVO.setSumImpressionsF(MapUtils.getInteger(searchDateMapListF.get(index),"sumImpressions"));
                searchWordVO.setCrF(MapUtils.getDouble(searchDateMapListF.get(index),"cr"));
                searchWordVO.setSumofClickF(MapUtils.getDouble(searchDateMapListF.get(index),"sumofClick"));
                searchWordVO.setSumClicksF(MapUtils.getInteger(searchDateMapListF.get(index),"sumClicks"));
                searchWordVO.setMonthF(MapUtils.getInteger(searchDateMapListF.get(index),"month"));
                searchWordVO.setSevenDayAdvertisedSkuSalesF(MapUtils.getDouble(searchDateMapListF.get(index),"sevenDayAdvertisedSkuSales"));
                searchWordVO.setSpendF(MapUtils.getDouble(searchDateMapListF.get(index),"spend"));
                searchWordVO.setCpcF(MapUtils.getDouble(searchDateMapListF.get(index),"cpc"));
                searchWordVO.setSumImpressionF(MapUtils.getDouble(searchDateMapListF.get(index),"sumImpression"));
                searchWordVoList.add(searchWordVO);
            }
            SortListUtils.sort(searchWordVoList,sortName,sortRule);
//            List<String> sortList = sortedByFields(MapUtils.getString(paramMap,"sortName"),dateSortWeekList.get(0),dataMapLists,"campaignName", MapUtils.getString(paramMap,"sortRule"),dateSign);
//            if(null != sortList && sortList.size()>0){
//                flag:for (String nameField : sortList) {
//                    // 临时存放CampaignList
//                    List<Map<String,Object>>  tempCampaignList = new ArrayList<>();
//                    // 临时存放CampaignList 用于日期排序
//                    List<Map<String,Object>>  tempDateCampaignList = new ArrayList<>();
//                    for (List<Map<String,Object>> list :dataMapLists) {
//                        for (Map<String,Object> secondMap: list) {
//                            if(nameField.equals(MapUtils.getString(secondMap,"campaignName"))){
//                                tempCampaignList.add(secondMap);
//                            }
//                        }
//                        if(4 == tempCampaignList.size()){
//                            for (Integer weekDate: dateSortWeekList) {
//                                for (Map<String,Object> dateMap: tempCampaignList) {
//                                    if(weekDate.equals(MapUtils.getInteger(dateMap,dateSign))){
//                                        tempDateCampaignList.add(dateMap);
//                                    }
//                                }
//                                if(tempDateCampaignList.size()==4){
//                                    finalDataMapLists.add(tempDateCampaignList);
//                                    continue flag;
//                                }
//                            }
//                        }
//                    }
//                }
//            }else{
//                return ResultUtil.error("暂无数据");
//            }
            PageUtil pageUtil = PageUtil.Pagination(pageNum, pageSize, searchWordVoList);
            return ResultUtil.success(pageUtil);
        }else{
            return ResultUtil.success("暂无数据");
        }
    }

    /**
     * 根据工具类补充空数据排序
     * @param paramMap
     * @return
     * @throws Exception
     */
    public ResultUtil getSearchWordListByObject(Map paramMap) throws Exception {
        PAGESIZE = MapUtils.getInteger(paramMap,"pageSize");
        PAGENUM = MapUtils.getInteger(paramMap,"pageNum");
        String dateSign = MapUtils.getString(paramMap, "type");
        List<SearchWordVo> searchWordVoList = new ArrayList<>();
        List<Map<String, Object>> dataLists = sponsoredProductsSearchTermReportMapper.getSearchPerspectiveList(paramMap);
        if(null != dataLists  && dataLists.size()>0) {
            String sortName = MapUtils.getString(paramMap, "sortName");
            String sortRule = MapUtils.getString(paramMap, "sortRule");

            List<String> campaignNames = new ArrayList<>();
            for (Map<String, Object> dataMap : dataLists) {
                campaignNames.add(MapUtils.getString(dataMap, CUSTOMERSEARCHTERM));
            }
            // 对参数日期数组进行处理
            List<String> dateArrayList = (List<String>) paramMap.get("paramDateArry");
            // 记录原始的数据顺序
            List<String> dateSortWeekList = new ArrayList<>();
            for (String date : dateArrayList) {
                if(date.contains("53")){
                    date = (Integer.valueOf(date.substring(0,4)) + 1) + "01";
                }
                dateSortWeekList.add(String.valueOf(date));
            }

            Map<String, Object> initMap = (Map) dataLists.get(0);
            Map<String, Object> nullMap = new HashMap<>(1);
            for (Map.Entry<String, Object> a : initMap.entrySet()) {
                nullMap.put(a.getKey(), 0);
            }
            List<List<Map<String, Object>>> dataLists2 = ListUtil.mergeList(dataLists, CUSTOMERSEARCHTERM, dateSortWeekList, nullMap, dateSign);

            for (List<Map<String, Object>> tempList : dataLists2) {
                SearchWordVo searchWordVO = new SearchWordVo();
                for (Map<String, Object> temMap : tempList) {
                    if (temMap.get(dateSign).equals(dateSortWeekList.get(0))) {
                        searchWordVO.setCustomerSearchTerm(MapUtils.getString(temMap, "customerSearchTerm"));
                        searchWordVO.setCtrO(MapUtils.getDouble(temMap, "ctr"));
                        searchWordVO.setWeekO(MapUtils.getInteger(temMap, "week"));
                        searchWordVO.setYearO(MapUtils.getInteger(temMap, "year"));
                        searchWordVO.setSevenDayTotalSalesO(MapUtils.getDouble(temMap, "sevenDayTotalSales"));
                        searchWordVO.setSevenDayTotalUnitsO(MapUtils.getInteger(temMap, "sevenDayTotalUnits"));
                        searchWordVO.setAcosO(MapUtils.getInteger(temMap, "acos"));
                        searchWordVO.setSumofSalesO(MapUtils.getDouble(temMap, "sumofSales"));
                        searchWordVO.setSumImpressionsO(MapUtils.getInteger(temMap, "sumImpressions"));
                        searchWordVO.setCrO(MapUtils.getDouble(temMap, "cr"));
                        searchWordVO.setSumofClickO(MapUtils.getDouble(temMap, "sumofClick"));
                        searchWordVO.setSumClicksO(MapUtils.getInteger(temMap, "sumClicks"));
                        searchWordVO.setMonthO(MapUtils.getInteger(temMap, "month"));
                        searchWordVO.setSevenDayAdvertisedSkuSalesO(MapUtils.getDouble(temMap, "sevenDayAdvertisedSkuSales"));
                        searchWordVO.setSpendO(MapUtils.getDouble(temMap, "spend"));
                        searchWordVO.setCpcO(MapUtils.getDouble(temMap, "cpc"));
                        searchWordVO.setSumImpressionO(MapUtils.getDouble(temMap, "sumImpression"));
                    } else if (temMap.get(dateSign).equals(dateSortWeekList.get(1))) {
                        searchWordVO.setCtrS(MapUtils.getDouble(temMap, "ctr"));
                        searchWordVO.setWeekS(MapUtils.getInteger(temMap, "week"));
                        searchWordVO.setYearS(MapUtils.getInteger(temMap, "year"));
                        searchWordVO.setSevenDayTotalSalesS(MapUtils.getDouble(temMap, "sevenDayTotalSales"));
                        searchWordVO.setSevenDayTotalUnitsS(MapUtils.getInteger(temMap, "sevenDayTotalUnits"));
                        searchWordVO.setAcosS(MapUtils.getInteger(temMap, "acos"));
                        searchWordVO.setSumofSalesS(MapUtils.getDouble(temMap, "sumofSales"));
                        searchWordVO.setSumImpressionsS(MapUtils.getInteger(temMap, "sumImpressions"));
                        searchWordVO.setCrS(MapUtils.getDouble(temMap, "cr"));
                        searchWordVO.setSumofClickS(MapUtils.getDouble(temMap, "sumofClick"));
                        searchWordVO.setSumClicksS(MapUtils.getInteger(temMap, "sumClicks"));
                        searchWordVO.setMonthS(MapUtils.getInteger(temMap, "month"));
                        searchWordVO.setSevenDayAdvertisedSkuSalesS(MapUtils.getDouble(temMap, "sevenDayAdvertisedSkuSales"));
                        searchWordVO.setSpendS(MapUtils.getDouble(temMap, "spend"));
                        searchWordVO.setCpcS(MapUtils.getDouble(temMap, "cpc"));
                        searchWordVO.setSumImpressionS(MapUtils.getDouble(temMap, "sumImpression"));
                    } else if (temMap.get(dateSign).equals(dateSortWeekList.get(2))) {
                        searchWordVO.setCtrT(MapUtils.getDouble(temMap, "ctr"));
                        searchWordVO.setWeekT(MapUtils.getInteger(temMap, "week"));
                        searchWordVO.setYearT(MapUtils.getInteger(temMap, "year"));
                        searchWordVO.setSevenDayTotalSalesT(MapUtils.getDouble(temMap, "sevenDayTotalSales"));
                        searchWordVO.setSevenDayTotalUnitsT(MapUtils.getInteger(temMap, "sevenDayTotalUnits"));
                        searchWordVO.setAcosT(MapUtils.getInteger(temMap, "acos"));
                        searchWordVO.setSumofSalesT(MapUtils.getDouble(temMap, "sumofSales"));
                        searchWordVO.setSumImpressionsT(MapUtils.getInteger(temMap, "sumImpressions"));
                        searchWordVO.setCrT(MapUtils.getDouble(temMap, "cr"));
                        searchWordVO.setSumofClickT(MapUtils.getDouble(temMap, "sumofClick"));
                        searchWordVO.setSumClicksT(MapUtils.getInteger(temMap, "sumClicks"));
                        searchWordVO.setMonthT(MapUtils.getInteger(temMap, "month"));
                        searchWordVO.setSevenDayAdvertisedSkuSalesT(MapUtils.getDouble(temMap, "sevenDayAdvertisedSkuSales"));
                        searchWordVO.setSpendT(MapUtils.getDouble(temMap, "spend"));
                        searchWordVO.setCpcT(MapUtils.getDouble(temMap, "cpc"));
                        searchWordVO.setSumImpressionT(MapUtils.getDouble(temMap, "sumImpression"));
                    } else if (temMap.get(dateSign).equals(dateSortWeekList.get(3))) {
                        searchWordVO.setCtrF(MapUtils.getDouble(temMap, "ctr"));
                        searchWordVO.setWeekF(MapUtils.getInteger(temMap, "week"));
                        searchWordVO.setYearF(MapUtils.getInteger(temMap, "year"));
                        searchWordVO.setSevenDayTotalSalesF(MapUtils.getDouble(temMap, "sevenDayTotalSales"));
                        searchWordVO.setSevenDayTotalUnitsF(MapUtils.getInteger(temMap, "sevenDayTotalUnits"));
                        searchWordVO.setAcosF(MapUtils.getInteger(temMap, "acos"));
                        searchWordVO.setSumofSalesF(MapUtils.getDouble(temMap, "sumofSales"));
                        searchWordVO.setSumImpressionsF(MapUtils.getInteger(temMap, "sumImpressions"));
                        searchWordVO.setCrF(MapUtils.getDouble(temMap, "cr"));
                        searchWordVO.setSumofClickF(MapUtils.getDouble(temMap, "sumofClick"));
                        searchWordVO.setSumClicksF(MapUtils.getInteger(temMap, "sumClicks"));
                        searchWordVO.setMonthF(MapUtils.getInteger(temMap, "month"));
                        searchWordVO.setSevenDayAdvertisedSkuSalesF(MapUtils.getDouble(temMap, "sevenDayAdvertisedSkuSales"));
                        searchWordVO.setSpendF(MapUtils.getDouble(temMap, "spend"));
                        searchWordVO.setCpcF(MapUtils.getDouble(temMap, "cpc"));
                        searchWordVO.setSumImpressionF(MapUtils.getDouble(temMap, "sumImpression"));
                    } else {
                        return ResultUtil.error("暂无数据");
                    }
                }
                searchWordVoList.add(searchWordVO);
            }
            SortListUtils.sort(searchWordVoList, sortName, sortRule);
        }else {
            return ResultUtil.success("暂无数据");
        }
        PageUtil pageUtil = PageUtil.Pagination2(PAGENUM, PAGESIZE, searchWordVoList,1);
        return ResultUtil.success(pageUtil);
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
        Map<String,Float> resultFloatDataMap = new LinkedHashMap<>();
        Map<String,Integer> resultIntegerDataMap = new LinkedHashMap<>();
        if(null == fields || "" == fields ){
            fields = "sumImpressions";
        }
        for (List<Map<String, Object>> dataMapList: dataList) {
            for (Map<String,Object> sortFieldsMap: dataMapList) {
                if(week.equals(MapUtils.getInteger(sortFieldsMap,dateSign))){
                    if(MapUtils.getObject(sortFieldsMap,fields).toString().contains(".")){
                        resultFloatDataMap.put(MapUtils.getString(sortFieldsMap,nameFields),MapUtils.getFloat(sortFieldsMap,fields));
                    }
                    resultIntegerDataMap.put(MapUtils.getString(sortFieldsMap,nameFields),MapUtils.getInteger(sortFieldsMap,fields));
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
    public List<String> getSortFloatMap(Map<String,Float> map,String sortRule){
        if("desc" .equals(sortRule)){
            List<Map.Entry<String, Float>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Float> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }else if("asc" .equals(sortRule)){
            List<Map.Entry<String, Float>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
                //升序排序
                @Override
                public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Float> mapping : list) {
                sortNameFieldList.add(mapping.getKey());
            }
            return sortNameFieldList;
        }else {
            List<Map.Entry<String, Float>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> sortNameFieldList = new LinkedList<>();
            for (Map.Entry<String, Float> mapping : list) {
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
     * 对List<Map<String, Object>>类型中的Map中的某个value进行排序-----降序
     * @return
     */
    public List<Map<String, Object>> returnSortListDESCByStringField(List<Map<String, Object>> list , String valueName){
        if (null == list || list.size() == 0 ){
            return new ArrayList<>();
        }else if(StringUtils.isEmpty(valueName)){
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        String name1 = String.valueOf(o1.get(valueName).toString()) ; //name1是从你list里面拿出来的一个
                        String name2 = String.valueOf(o2.get(valueName).toString()) ; //name1是从你list里面拿出来的第二个name
                        return name2.compareTo(name1);
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
     * @param
     * @param
     * @return
     */
    public ResultUtil getList(Map map) {
        return ResultUtil.success(sponsoredProductsSearchTermReportMapper.getSearchPerspectiveList(map));
    }

    /**
     * 获取总数据
     *
     * @return
     */
    public ResultUtil getSum() {
        return ResultUtil.success(sponsoredProductsSearchTermReportMapper.getSearchPerspectiveSumData());
    }

    /**
     * 获取campaign_name
     *
     * @return
     */
    public ResultUtil getCampaignName(String sellerId,String shopArea,String adGroupName) {
        List<String> adGroupNameList = StringUtil.parseReqParamToArrayUseTrim(adGroupName);
        return ResultUtil.success(sponsoredProductsSearchTermReportMapper.getCampaignName(sellerId,shopArea,adGroupNameList));
    }

    /**
     * 获取ad_group_name
     *
     * @return
     */
    public ResultUtil getAdGroupName(String sellerId,String shopArea,String campaignName) {
        List<String> campaignNameList = StringUtil.parseReqParamToArrayUseTrim(campaignName);
        return ResultUtil.success(sponsoredProductsSearchTermReportMapper.getAdGroupName(sellerId,shopArea,campaignNameList));
    }

}
