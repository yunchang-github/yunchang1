package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 物流时效
 * logistics_time_limitation
 * @author WeiziPlus
 * @date 2019-07-26 16:54:48
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("logistics_time_limitation")
public class LogisticsTimeLimitation implements Serializable {
    /**
     * 主键，自增
     */
    @Id("id")
    private Long id;

    /**
     * 物流方式代码
     */
    @Column("logistics_mode_code")
    private String logisticsModeCode;

    /**
     * 物流方式
     */
    @Column("logistics_mode")
    private String logisticsMode;

    /**
     * 物流时效
     */
    @Column("logistics_time_limitation")
    private String logisticsTimeLimitation;

    /**
     * 入仓检出时间
     */
    @Column("check_in_time")
    private String checkInTime;

    /**
     * 物流类型
     */
    @Column("logistics_type")
    private String logisticsType;

    private static final long serialVersionUID = 1L;
}