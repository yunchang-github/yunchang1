package com.weiziplus.springboot.scheduled.redisTask;


import com.weiziplus.springboot.mapper.middle.SearchWordDateMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.sspa.SponsoredProductsSearchTermReportMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author 1
 */
@Component
@Configuration
@Service
@Slf4j
@EnableScheduling
public class SearchWordSchedule {

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

    private static final String WEEK = "week";

    private static final String YEAR = "year";

    private static final String MONTH = "month";

    private static final String CUSTOMERSEARCHTERM = "customerSearchTerm";



    /**
     * 定时获取最近俩月的搜索词分析数据并存入redis中
     */
    //@Scheduled(cron = "0 02 00 * * ?")
//    @Scheduled(cron = "0 18 10 * * ?")
    public void serRedisSearchWord() {
        log.info("搜索词数据获取任务开始！");
        List<Shop> shopList = shopMapper.getAllList();
        List<Area> areaList = new ArrayList<>();
        for (Shop shop : shopList) {
            areaList = shopAreaMapper.getAreaListByShopId(shop.getId());
            for (Area areas : areaList) {
                List<String> dateYearWeekList = new ArrayList<>();
                // 获取两年前到现在的所有周
                for (int index = 0; index < 8; index++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.WEEK_OF_YEAR, -index);
                    String year = String.valueOf(calendar.get(Calendar.YEAR));
                    String week = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
                    if (week.length() < 2) {
                        week = "0" + week;
                    }
                    dateYearWeekList.add(year + week);
                }
                List<String> dateYearMonthList = new ArrayList<>();
                // 获取两年前到现在的所有月
//        for(int index=0;index<24;index++){
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            calendar.add(Calendar.MONTH,-index);
//            String year = String.valueOf(calendar.get(Calendar.YEAR));
//            String week = String.valueOf(calendar.get(Calendar.MONTH));
//            if(week.length()<2){
//                week = "0"+week;
//            }
//            dateYearMonthList.add(year+week);
//        }
                String reportDataType = "searchWordData";
                String type = "week";
                Map<String, Object> testMap = new HashMap<String, Object>();
                testMap.put("type", type);
                testMap.put("yearWeekList", dateYearWeekList);
                testMap.put("yearMonthList", dateYearMonthList);
                testMap.put("shop", shop.getShopName());
                testMap.put("area", areas.getMwsCountryCode());
                // 某个店铺下的一个区域的全部数据
                List<Map<String, Object>> allDataLists = sponsoredProductsSearchTermReportMapper.getAllSearchPerspectiveList(testMap);
                if (null != allDataLists && allDataLists.size() > 0) {
                    // 结果集合
                    List<List<Map<String, Object>>> dataMapLists = new ArrayList<>();
                    // 取出结果中的全部campaignName值
                    List<String> customerSearchTerms = new ArrayList<>();
                    for (Map<String, Object> dataMap : allDataLists) {
                        customerSearchTerms.add(MapUtils.getString(dataMap, "customerSearchTerm"));
                        String week = MapUtils.getString(dataMap, WEEK);
                        if (week.length() < 2) {
                            week = "0" + week;
                        }
                        dataMap.put(WEEK, week);
                    }
//                  对campaign进行去重
                    LinkedList<String> sortCampaignLinkedList = new LinkedList<>();
                    Iterator<String> it = customerSearchTerms.iterator();
                    while (it.hasNext()) {
                        String str = it.next();
                        if (!sortCampaignLinkedList.contains(str)) {
                            sortCampaignLinkedList.add(str);
                        }
                    }
                    // 取出一个map 使其为空，作为空map模板使用
                    Map<String, Object> initMap = (Map) allDataLists.get(0);
                    Map<String, Object> nullMap = new HashMap<>();
                    for (Map.Entry<String, Object> a : initMap.entrySet()) {
                        nullMap.put(a.getKey(), 0);
                    }
                    // 对参数日期数组进行处理
//            List<String> dateArrayList = (List<String>) paramMap.get("paramDateArry");
                    // 记录原始的数据顺序
//            List<Integer> dateSortWeekList = new ArrayList<>();
//            for (String date : dateArrayList) {
//                dateSortWeekList.add(Integer.valueOf(date.substring(4, 6)));
//            }
                    // 根据campaign来分组，将不满四周的补0
                    for (String customerSearchTerm : sortCampaignLinkedList) {
                        // 临时存放CampaignList
                        List<Map<String, Object>> tempCampaignList = new ArrayList<>();
                        // 存放tempCampaignList中的week
                        List<String> temDateWeek = new ArrayList<>();
                        for (Map<String, Object> dataMap : allDataLists) {
                            if (customerSearchTerm.equals(dataMap.get("customerSearchTerm"))) {
                                tempCampaignList.add(dataMap);
                                String week = MapUtils.getString(dataMap, WEEK);
                                if (week.length() < 2) {
                                    week = "0" + week;
                                }
                                temDateWeek.add(MapUtils.getString(dataMap, YEAR) + week);
                            }
                        }
                        if (tempCampaignList.size() != dateYearWeekList.size()) {
                            for (String dateWeek : dateYearWeekList) {
                                flag:  for (Map<String, Object> temMap : tempCampaignList) {
                                    if (tempCampaignList.size() == dateYearWeekList.size()) {
                                        break;
                                    }
                                    for (String temYearWeek : temDateWeek) {
                                        if (temYearWeek.equals(dateWeek)) {
                                            continue flag;
                                        }
                                    }
                                    if (!dateWeek.equals(MapUtils.getInteger(temMap, "week"))) {
                                        nullMap.put("week", dateWeek.substring(4, 6));
                                        nullMap.put("year", dateWeek.substring(0, 4));
                                        nullMap.put("customerSearchTerm", customerSearchTerm);
                                        nullMap.put("shop", shop.getShopName());
                                        nullMap.put("area", areas.getMwsCountryCode());
                                        Map<String, Object> newTemMap = new HashMap<>(1);
                                        newTemMap.putAll(nullMap);
                                        tempCampaignList.add(newTemMap);
                                        break;
                                    }
                                }
                            }
                        }
                        dataMapLists.add(tempCampaignList);
                    }
                    for (String yearWeeks : dateYearWeekList) {
                        // 存放每周的数据
                        List<Map<String, Object>> tempWeekDataList = new ArrayList<>();
                        String redisKey = null;
                        String redisPart = null;
                        List<String> redisKeyList = new ArrayList<>(1);
                        for (List<Map<String, Object>> dataMapList : dataMapLists) {
                            for (Map<String, Object> dataWeekMap : dataMapList) {
                                String yearWeek = MapUtils.getString(dataWeekMap, "year") + MapUtils.getString(dataWeekMap, "week");
                                String shopName = MapUtils.getString(dataWeekMap, "shop");
                                String areaName = MapUtils.getString(dataWeekMap, "area");
                                if (yearWeek.equals(yearWeeks)) {
                                    tempWeekDataList.add(dataWeekMap);
                                    redisPart = shopName + areaName + yearWeek;
                                }
                            }
                        }
                        // 按照搜索词进行排序
                        returnSortListDESCByStringField(tempWeekDataList, CUSTOMERSEARCHTERM);
                        redisKey = reportDataType + redisPart + WEEK;
                        RedisUtil.set(redisKey, tempWeekDataList, 24 * 60 * 60L);
                    }
                    log.info("*********店铺：" + shop.getShopName() + "*******区域：" + areas.getMwsCountryCode() + "数据已插入！****");
                } else {
                    log.warn("*********店铺：" + shop.getShopName() + "*******区域：" + areas.getMwsCountryCode() + "无数据！****");
                    continue;
                }
            }
        }
        log.info("搜索词数据获取任务结束！");
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
}
