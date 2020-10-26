package com.weiziplus.springboot.scheduled.SQLTableUpdate;

import com.weiziplus.springboot.mapper.sqlTable.SqlTableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Slf4j
public class LatestInventoryAgeSchedule {
    @Autowired
    SqlTableMapper sqlTableMapper;

    //@Scheduled(cron = "15 00 04 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateLatestInventoryAge(){
        log.info("每日更新latest_inventory_age表任务开始");
        try {
            sqlTableMapper.delLatestInventoryAge();
            sqlTableMapper.insertLatestInventoryAge();
        }catch (Exception e){
            log.error("每日更新latest_inventory_age表任务失败，详情：" + e.getMessage());
        }
        log.info("每日更新latest_inventory_age表任务结束");
    }
}
