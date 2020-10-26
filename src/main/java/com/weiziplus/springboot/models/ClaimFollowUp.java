package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 索赔跟进
 * claim_follow_up
 * @author Administrator
 * @date 2019-09-06 14:23:52
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("claim_follow_up")
public class ClaimFollowUp implements Serializable {
    /**
     */
    @Id("id")
    private Integer id;

    /**
     * 批次号
     */
    @Column("shipment_id")
    private String shipmentId;

    /**
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("area")
    private String area;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     * 索赔进度记录
     */
    @Column("claim_progress_record")
    private String claimProgressRecord;

    /**
     * 延期货件
     */
    @Column("extended_futures")
    private String extendedFutures;

    /**
     * 延期原因
     */
    @Column("reason_delay")
    private String reasonDelay;

    /**
     * 预计延期时间
     */
    @Column("estimated_delay_time")
    private String estimatedDelayTime;

    /**
     * 索赔结果
     */
    @Column("claim_result")
    private String claimResult;

    private static final long serialVersionUID = 1L;
}