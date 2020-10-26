package com.weiziplus.springboot.scheduled.amazonAdvert;

import com.weiziplus.springboot.mapper.shop.ProfileMapper;
import com.weiziplus.springboot.mapper.shop.ShopAreaMapper;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.ShopAreaProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@Service
@EnableAsync
public class AmazonAdvertDataLastMonthsSchedule {

    @Autowired
    private AmazonAdvertOriginalDataSchedule amazonAdvertOriginalDataSchedule;

    @Autowired
    private ProfileMapper profileMapper;

    //@Scheduled(cron = "0 0 06 ? * *")
    public void addAdvertMonthOriginalData() {
        //当前时间
        log.info("Current Thread : {}", Thread.currentThread().getName());
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime twoDaysAgo = localDateTime.minusDays(2L);
        String date;
        //从前天开始拿之前的数据，昨天的数据交由晚上8点的定时任务去拿
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        OUT:
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            if (1 == shopAreaProfile.getStatus()) {
                continue OUT;
            }
            LocalDateTime someDay = twoDaysAgo;
            for (int i = 0; i < 28; i++) {
                date = someDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvProductadsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的ProductadsReport添加" + (i + 1) + "次");
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvCampaignsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的CampaignsReport添加" + (i + 1) + "次");
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvKeywordsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的KeywordsReport添加" + (i + 1) + "次");
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvTargetsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的的TargetsReport添加" + (i + 1) + "次");
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvAdGroupsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的AdGroupsReport添加" + (i + 1) + "次");
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvAsinsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的AsinsReport添加" + (i + 1) + "次");
                someDay = someDay.minusDays(1L);
                log.info("-----------------------店铺id：" + shopAreaProfile.getShopId() + "和区域id:" + shopAreaProfile.getAreaId() + "和配置文件id:" + shopAreaProfile.getProfileId() + "获取" + date + "的六份报表数据任务完成-------------------------------");
            }
            log.info("-----------------------店铺id：" + shopAreaProfile.getShopId() + "和区域id:" + shopAreaProfile.getAreaId() + "和配置文件id:" + shopAreaProfile.getProfileId() + "获取一个月的六份报表数据任务完成-------------------------------");
            profileMapper.updateProfileStatusByProfileId(shopAreaProfile.getProfileId());
        }
        log.info("-----------------------目前所有区域获取前一个月广告数据任务完成-------------------------------");
    }
}
