package com.weiziplus.springboot.utils.amazon;

import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.scheduled.amazonAdvert.AmazonAdvertOriginalDataSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class AdvertMonthOriginalDataUtil {
    @Autowired
    private AmazonAdvertOriginalDataSchedule amazonAdvertOriginalDataSchedule;


    public void addAdvertMonthOriginalData(Shop shop) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //当前时间
        Date nowDate = new Date();
        long dateTime = nowDate.getTime();
        //从前天开始拿之前的数据，昨天的数据交由晚上8点的定时任务去拿
        dateTime = dateTime - 172800000;
        for (int i = 0; i < 30; i++) {
            Date someDay = new Date(dateTime);
            String date = simpleDateFormat.format(someDay);
//            amazonAdvertOriginalDataSchedule.addOriginalDataAdvProductadsReport(date, shop);
//            System.out.println(shop.getShopName() + "ProductadsReport添加成功" + (i + 1) + "次");
//            amazonAdvertOriginalDataSchedule.addOriginalDataAdvCampaignsReport(date, shop);
//            System.out.println(shop.getShopName() + "CampaignsReport添加成功" + (i + 1) + "次");
//            amazonAdvertOriginalDataSchedule.addOriginalDataAdvKeywordsReport(date, shop);
//            System.out.println(shop.getShopName() + "KeywordsReport添加成功" + (i + 1) + "次");
//            amazonAdvertOriginalDataSchedule.addOriginalDataAdvTargetsReport(date, shop);
//            System.out.println(shop.getShopName() + "的TargetsReport添加成功" + (i + 1) + "次");
//            amazonAdvertOriginalDataSchedule.addOriginalDataAdvAdGroupsReport(date, shop);
//            System.out.println(shop.getShopName() + "AdGroupsReport添加成功" + (i + 1) + "次");
//            amazonAdvertOriginalDataSchedule.addOriginalDataAdvAsinsReport(date, shop);
//            System.out.println(shop.getShopName() + "AsinsReport添加成功" + (i + 1) + "次");
            dateTime -= 86400000;
        }
        log.info("-----------------------获取前一个月广告数据任务完成-------------------------------");
    }
}
