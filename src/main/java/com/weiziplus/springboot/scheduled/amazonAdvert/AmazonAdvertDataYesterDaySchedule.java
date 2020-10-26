package com.weiziplus.springboot.scheduled.amazonAdvert;

import com.weiziplus.springboot.mapper.originalAdvertData.ErrorDateMapper;
import com.weiziplus.springboot.mapper.shop.AreaMapper;
import com.weiziplus.springboot.mapper.shop.ProfileMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.DO.ErrorDateDO;
import com.weiziplus.springboot.models.Shop;
import com.weiziplus.springboot.models.ShopAreaProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class AmazonAdvertDataYesterDaySchedule {
    @Autowired
    private AmazonAdvertOriginalDataSchedule amazonAdvertOriginalDataSchedule;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private ErrorDateMapper errorDateMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private AreaMapper areaMapper;

    //@Scheduled(cron = "0 0 20 ? * *")
    public void addAdvertYesterdayOriginalData() {
        //当前时间
        log.info("Current Thread : {}", Thread.currentThread().getName());
        LocalDateTime localDateTime = LocalDateTime.now();
        String date;
        //晚上8点  定时任务去拿昨天的数据
        List<ShopAreaProfile> shopAreaProfileList = profileMapper.getAllDatas();
        for (ShopAreaProfile shopAreaProfile : shopAreaProfileList) {
            date = localDateTime.minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            amazonAdvertOriginalDataSchedule.addOriginalDataAdvProductadsReport(date, shopAreaProfile);
            System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的ProductadsReport添加" + (1) + "次");
            amazonAdvertOriginalDataSchedule.addOriginalDataAdvCampaignsReport(date, shopAreaProfile);
            System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的CampaignsReport添加" + (1) + "次");
            amazonAdvertOriginalDataSchedule.addOriginalDataAdvKeywordsReport(date, shopAreaProfile);
            System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的KeywordsReport添加" + (1) + "次");
            amazonAdvertOriginalDataSchedule.addOriginalDataAdvTargetsReport(date, shopAreaProfile);
            System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的的TargetsReport添加" + (1) + "次");
            amazonAdvertOriginalDataSchedule.addOriginalDataAdvAdGroupsReport(date, shopAreaProfile);
            System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的AdGroupsReport添加" + (1) + "次");
            amazonAdvertOriginalDataSchedule.addOriginalDataAdvAsinsReport(date, shopAreaProfile);
            System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的AsinsReport添加" + (1) + "次");
            log.info("-----------------------店铺id：" + shopAreaProfile.getShopId() + "和区域id:" + shopAreaProfile.getAreaId() + "和配置文件id:" + shopAreaProfile.getProfileId() + "获取" + date + "的六份报表数据任务完成-------------------------------");
        }
        log.info("-----------------------目前所有区域获取前昨天:" + localDateTime.toString() + "的广告数据任务完成-------------------------------");
    }

    //@Scheduled(cron = "0 0 22 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void reAddAdvertErrorDateData() {
        log.info("Current Thread : {}", Thread.currentThread().getName());
        log.info("-----------------------获取之前错误日期的数据任务开始-------------------------------");
        List<ErrorDateDO> errorDateDOList = errorDateMapper.selectAllErrorDateData();
        for (ErrorDateDO errorDateDO : errorDateDOList) {
            String sellerId = errorDateDO.getSellerId();
            String areaCode = errorDateDO.getArea();
            Shop shop = shopMapper.getOneInfoBySellerId(sellerId);
            Area area = areaMapper.getAreaByAdvertCountryCode(areaCode);
            ShopAreaProfile shopAreaProfile = profileMapper.getDatasByShopIdAreaId(shop.getId(), area.getId());
            Integer type = errorDateDO.getType();
            String date = errorDateDO.getErrorDate();
            if (type == 1) {
                errorDateMapper.delErrorDate(errorDateDO);
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvProductadsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的ProductadsReport添加" + (1) + "次");
                continue;
            }
            if (type == 2) {
                errorDateMapper.delErrorDate(errorDateDO);
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvCampaignsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的CampaignsReport添加" + (1) + "次");
                continue;
            }
            if (type == 3) {
                errorDateMapper.delErrorDate(errorDateDO);
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvAdGroupsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的AdGroupsReport添加" + (1) + "次");
                continue;
            }
            if (type == 4) {
                errorDateMapper.delErrorDate(errorDateDO);
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvTargetsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的的TargetsReport添加" + (1) + "次");
                continue;
            }
            if (type == 5) {
                errorDateMapper.delErrorDate(errorDateDO);
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvKeywordsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的KeywordsReport添加" + (1) + "次");
                continue;
            }
            if (type == 6) {
                errorDateMapper.delErrorDate(errorDateDO);
                amazonAdvertOriginalDataSchedule.addOriginalDataAdvAsinsReport(date, shopAreaProfile);
                System.out.println("店铺id：" + shopAreaProfile.getShopId() + "的AsinsReport添加" + (1) + "次");
                continue;
            }
        }
        log.info("-----------------------获取之前错误日期的数据任务完成-------------------------------");
    }
}
