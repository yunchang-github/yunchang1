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
public class LatestManageFbaInventorySchedule {
    @Autowired
    SqlTableMapper sqlTableMapper;

    //@Scheduled(cron = "00 00 04 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateLatestManageFbaInventory(){
        log.info("每日更新latest_manage_fba_inventory表任务开始");
        try {
            sqlTableMapper.delLatestManageFbaInventory();
            sqlTableMapper.insertLatestManageFbaInventory();
        }catch (Exception e){
            log.error("每日更新latest_manage_fba_inventory表任务失败，详情：" + e.getMessage());
        }
        log.info("每日更新latest_manage_fba_inventory表任务结束");
    }
}
