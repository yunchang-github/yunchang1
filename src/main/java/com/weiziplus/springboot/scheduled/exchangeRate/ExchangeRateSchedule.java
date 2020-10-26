package com.weiziplus.springboot.scheduled.exchangeRate;

import com.weiziplus.springboot.mapper.shop.ExchangeRateMapper;
import com.weiziplus.springboot.models.DO.ExchangeRateDO;
import com.weiziplus.springboot.utils.JuheDemo;
import com.weiziplus.springboot.utils.JuheExchangeRateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@Service
public class ExchangeRateSchedule {
//    @Autowired
//    ExchangeRateMapper exchangeRateMapper;
//
//    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "00 00 05 ? * *")
//    public void getLatestExchangeRate(){
//        log.info("获取每天的汇率任务开始");
//        List<ExchangeRateDO> exchangeRateList = JuheExchangeRateUtil.getRequest2();
//        try {
//            exchangeRateMapper.insertExchangeRate(exchangeRateList);
//        }catch (Exception e){
//            log.error("获取每天的汇率任务失败。详情：" + e.getMessage());
//        }
//        log.info("获取每天的汇率任务结束");
//    }
}
