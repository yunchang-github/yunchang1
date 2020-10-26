package com.weiziplus.springboot.scheduled.SQLTableUpdate;

import com.weiziplus.springboot.mapper.sqlTable.SqlTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@Service
public class LatestSkuAsinPskuSchedule {
    @Autowired
    SqlTableMapper sqlTableMapper;

    //@Scheduled(cron = "05 00 04 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuAsinPsku(){
        log.info("每日更新sku_asin_psku表任务开始");
        try {
            sqlTableMapper.delSkuAsinPsku();
            sqlTableMapper.insertSkuAsinPsku();
        }catch (Exception e){
            log.error("每日更新sku_asin_psku表任务失败，详情：" + e.getMessage());
        }
        log.info("每日更新sku_asin_psku表任务结束");
    }
}
