package com.weiziplus.springboot.scheduled.SQLTableUpdate;

import com.weiziplus.springboot.mapper.sqlTable.SqlTableMapper;
import com.weiziplus.springboot.models.Area;
import com.weiziplus.springboot.models.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@Service
public class LatestSumQuantitySchedule {
    @Autowired
    SqlTableMapper sqlTableMapper;

    //@Scheduled(cron = "05 03 04 ? * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateLatestSumQuantity(){
        log.info("每日更新latest_sum_quantity表任务开始");
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd");
            String createTime = LocalDate.now().minusDays(1).format(dateTimeFormatter).toString();
            sqlTableMapper.delLatestSumQuantity();
            for (int i = 1; i <= 4; i++) {
                sqlTableMapper.insertLatestSumQuantity(i,createTime);
            }
        } catch (Exception e) {
            log.error("每日更新latest_sum_quantity表任务失败，详情：" + e.getMessage());
        }
        log.info("每日更新latest_sum_quantity表任务结束");
    }
}
