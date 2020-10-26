package com.weiziplus.springboot.models.VO;

import com.weiziplus.springboot.models.DO.SalesIndicatorDO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 大组维度的销售数据
 */
@Data
public class SalesStatisticsReportVO {
    private String groupName;
    private List<ShopSalesStatisticsDataVO> shopSalesStatisticsDataVOList;//具体店铺的销售数据集合
    private SalesIndicatorDO salesIndicatorDO;//该组的销售指标
    private String groupAnnualSalesCompletion;//全年组销售完成度
    private String groupMonthSalesCompletion;//当月组销售完成度
    private BigDecimal totalGroupAnnualSales;//全年组销售额合计
    private BigDecimal totalGroupMonthSales;//当月组销售额合计

    public SalesStatisticsReportVO() {
        this.groupAnnualSalesCompletion = "0.00%";
        this.groupMonthSalesCompletion = "0.00%";
        this.totalGroupAnnualSales = BigDecimal.valueOf(0);
        this.totalGroupMonthSales = BigDecimal.valueOf(0);
    }
}
