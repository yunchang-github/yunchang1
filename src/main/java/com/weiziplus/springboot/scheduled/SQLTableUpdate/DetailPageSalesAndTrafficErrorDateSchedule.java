package com.weiziplus.springboot.scheduled.SQLTableUpdate;

import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.sqlTable.SqlTableMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Service
@Slf4j
public class DetailPageSalesAndTrafficErrorDateSchedule {
    @Autowired
    SqlTableMapper sqlTableMapper;
    @Autowired
    ShopAreaMapper shopAreaMapper;
    @Autowired
    ShopMapper shopMapper;

    private static String[] TABLE_NAME = {"detail_page_sales_and_traffic", "detail_page_sales_and_traffic_by_child_items", "detail_page_sales_and_traffic_by_parent_items"};

    //@Scheduled(cron = "00 10 04 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateDetailPageSalesAndTrafficErrorDate() {
        log.info("每日更新detail_page_sales_and_traffic_error_date表任务开始");
        try {
            sqlTableMapper.delDetailPageSalesAndTrafficErrorDate();
            List<Shop> shopList = shopMapper.getAllList();
            for (int i = 0; i < TABLE_NAME.length; i++) {
                for (Shop shop : shopList) {
                    String shopName = shop.getShopName();
                    String sellerId = shop.getSellerId();
                    List<Area> areaList = shopAreaMapper.getAreaListByShopId(shop.getId());
                    for (Area area : areaList) {
                        String areaCode = area.getAdvertCountryCode();
                        sqlTableMapper.insertDetailPageSalesAndTrafficErrorDate(shopName, sellerId, areaCode, TABLE_NAME[i]);
                    }
                }
            }
        } catch (Exception e) {
            log.error("每日更新detail_page_sales_and_traffic_error_date表任务失败，详情：" + e.getMessage());
        }
        log.info("每日更新detail_page_sales_and_traffic_error_date表任务结束");
    }
}
