package com.weiziplus.springboot.scheduled.advertInventoryReport;

import com.weiziplus.springboot.mapper.advertisingInventoryReport.AdvertInventoryReportMapper;
import com.weiziplus.springboot.mapper.advertisingInventoryReport.InventoryReportMapper;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.shop.UserShopMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.DO.ChildBodyInventoryReportDO;
import com.weiziplus.springboot.models.DO.ParentBodyInventoryReportDO;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.VO.ChildItemAdInventoryReportVO;
import com.weiziplus.springboot.models.VO.ParentItemAdInventoryReportVO;
import com.weiziplus.springboot.utils.AreaCastUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.SortListUtils;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不用
 * */
@Slf4j
@Component
@Service
public class AdvertInventoryReportSchedule {
    @Autowired
    UserShopMapper userShopMapper;
    @Autowired
    ShopMapper shopMapper;
    @Autowired
    ShopAreaMapper shopAreaMapper;
    @Autowired
    AdvertInventoryReportMapper advertInventoryReportMapper;
    @Autowired
    InventoryReportMapper inventoryReportMapper;

    //@Scheduled(cron = "0 40 00 ? * *")
    //@Transactional(rollbackFor = Exception.class)
    public void addChildAdvertInventoryReportData() {
        log.info("-------------------子体广告库存报表数据计算并存入Redis任务启动----------------------");
        String date = "";
        String type = "week";
        Map<String, Object> maps = new HashMap();
        maps.put("type", type);
        List<ChildBodyInventoryReportDO> childBodyInventoryReportDOList = new ArrayList<>();
        List<Shop> shopList = shopMapper.getAllList();
        if (null == shopList || 0 >= shopList.size()) {
            log.warn("您还没有网店，请联系管理员添加");
            return;
        }
        for (Shop shop : shopList) {
            shop.setAreas(shopAreaMapper.getAreaListByShopId(Long.valueOf(shop.getId())));
            String sellerId = shop.getSellerId();
            for (Area area : shop.getAreas()) {
                maps.put("sellerId", sellerId);
                maps.put("shop", shop.getShopName());
                String areaCode = area.getAdvertCountryCode();
                maps.put("area", areaCode);
                //将areaCode转换为对应的站点，用于匹配all_order表的数据
                String salesChannel = AreaCastUtil.castToSalesChannel(areaCode);
                maps.put("salesChannel", salesChannel);
                //循环获取前一年的各周
                LocalDate localDate = LocalDate.now();
                WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
                IN:
                for (int i = 0; i < 8; i++) {
                    for (int errorNum = 0; errorNum < 3; errorNum++) {
                        try {
                            Integer weekOfYear = localDate.minusWeeks(i).get(weekFields.weekOfYear());
                            String year = localDate.minusWeeks(i).toString().replace("-", "").substring(0, 4);
                            String week = String.valueOf(weekOfYear);
                            if (weekOfYear < 10) {
                                week = "0" + weekOfYear;
                            }
                            date = year + week;
                            maps.put("date", date);
                            //搜索子体报表数据
                            childBodyInventoryReportDOList = advertInventoryReportMapper.getChildInventoryAdvertisingReport(maps);
                            if (childBodyInventoryReportDOList.size() == 0) {
                                RedisUtil.set("childAdvertInventoryReport" + sellerId + areaCode + type + date, childBodyInventoryReportDOList, 60L * 60 * 24);
                                continue IN;
                            }
                            for (ChildBodyInventoryReportDO childBodyInventoryReportDO : childBodyInventoryReportDOList) {
                                childBodyInventoryReportDO.setSellerId(sellerId);
                                childBodyInventoryReportDO.setArea(areaCode);
                                childBodyInventoryReportDO.setDateType(type);
                                childBodyInventoryReportDO.setDate(date);
                                childBodyInventoryReportDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            }
                            RedisUtil.set("childAdvertInventoryReport" + sellerId + areaCode + type + date, childBodyInventoryReportDOList, 60L * 60 * 24);
                            log.info("-------------------广告库存报表数据计算并存入Redis任务:" + "childAdvertInventoryReport" + sellerId + areaCode + type + date + "存入成功----------------------");
                            continue IN;
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("第" + (errorNum + 1) + "次出错,错误详情:" + e);

                            try {
                                Thread.sleep(900000L);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
//        if (("".equals(date) || date == null) && type.equals("month")) {
//            firstDate = localDate.minusMonths(4).toString().replace("-", "").substring(0, 6);
//            secondDate = localDate.minusMonths(3).toString().replace("-", "").substring(0, 6);
//            thirdDate = localDate.minusMonths(2).toString().replace("-", "").substring(0, 6);
//            fourthDate = localDate.minusMonths(1).toString().replace("-", "").substring(0, 6);
//        }
        }
        log.info("-------------------子体广告库存报表数据计算并存入Redis任务结束----------------------");
    }

    /**
     * 获取父分页数据
     *
     * @return
     */
    //@Scheduled(cron = "0 25 00 ? * *")
    //@Transactional(rollbackFor = Exception.class)
    public void addParentAdvertInventoryReportData() {
        log.info("-------------------父体广告库存报表数据计算并存入Redis任务启动----------------------");
        String date = "";
        String type = "week";
        Map<String, Object> maps = new HashMap();
        maps.put("type", type);
        List<ParentBodyInventoryReportDO> parentBodyInventoryReportDOList = new ArrayList<>();
        List<Shop> shopList = shopMapper.getAllList();
        if (null == shopList || 0 >= shopList.size()) {
            log.warn("您还没有网店，请联系管理员添加");
            return;
        }
        for (Shop shop : shopList) {
            shop.setAreas(shopAreaMapper.getAreaListByShopId(Long.valueOf(shop.getId())));
            String sellerId = shop.getSellerId();
            for (Area area : shop.getAreas()) {
                maps.put("sellerId", sellerId);
                maps.put("shop", shop.getShopName());
                String areaCode = area.getAdvertCountryCode();
                maps.put("area", areaCode);
                //将areaCode转换为对应的站点，用于匹配all_order表的数据
                String salesChannel = AreaCastUtil.castToSalesChannel(areaCode);
                maps.put("salesChannel", salesChannel);
                //循环获取前一年的各周
                LocalDate localDate = LocalDate.now();
                WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
                IN:
                for (int i = 0; i < 8; i++) {
                    for (int errorNum = 0; errorNum < 3; errorNum++) {
                        try {
                            Integer weekOfYear = localDate.minusWeeks(i).get(weekFields.weekOfYear());
                            String year = localDate.minusWeeks(i).toString().replace("-", "").substring(0, 4);
                            String week = String.valueOf(weekOfYear);
                            if (weekOfYear < 10) {
                                week = "0" + weekOfYear;
                            }
                            date = year + week;
                            maps.put("date", date);
                            //搜索父体报表数据
                            parentBodyInventoryReportDOList = advertInventoryReportMapper.getParentInventoryAdvertisingReport(maps);
                            if (parentBodyInventoryReportDOList.size() == 0) {
                                RedisUtil.set("parentAdvertInventoryReport" + sellerId + areaCode + type + date, parentBodyInventoryReportDOList, 60L * 60 * 24);
                                continue IN;
                            }
                            for (ParentBodyInventoryReportDO parentBodyInventoryReportDO : parentBodyInventoryReportDOList) {
                                parentBodyInventoryReportDO.setSellerId(sellerId);
                                parentBodyInventoryReportDO.setArea(areaCode);
                                parentBodyInventoryReportDO.setDateType(type);
                                parentBodyInventoryReportDO.setDate(date);
                                parentBodyInventoryReportDO.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            }
                            //inventoryReportMapper.addParentBodyInventoryReport(parentBodyInventoryReportDOList);
                            RedisUtil.set("parentAdvertInventoryReport" + sellerId + areaCode + type + date, parentBodyInventoryReportDOList, 60L * 60 * 24);
                            log.info("-------------------广告库存报表数据计算并存入Redis任务:" + "parentAdvertInventoryReport" + sellerId + areaCode + type + date + "存入成功----------------------");
                            continue IN;
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("第" + (errorNum + 1) + "次出错,错误详情:" + e);
                            try {
                                Thread.sleep(900000L);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
//            if (("".equals(date) || date == null) && type.equals("month")) {
//                firstDate = localDate.minusMonths(4).toString().replace("-", "").substring(0, 6);
//                secondDate = localDate.minusMonths(3).toString().replace("-", "").substring(0, 6);
//                thirdDate = localDate.minusMonths(2).toString().replace("-", "").substring(0, 6);
//                fourthDate = localDate.minusMonths(1).toString().replace("-", "").substring(0, 6);
//            }
        }
        log.info("-------------------父体广告库存报表数据计算并存入Redis任务结束----------------------");
    }
}