package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀列表（后台爬取）
 * seckill
 *
 * @author WeiziPlus
 * @date 2019-08-28 17:12:57
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("seckill")
public class Seckill implements Serializable {
    /**
     */
    @Id("id")
    private String id;

    /**
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("area")
    private String area;

    /**
     * 秒杀数据获取时间
     */
    @Column("date")
    private String date;

    /**
     * 秒杀项目id
     */
    @Column("campaign_id")
    private String campaignId;

    /**
     * 类型，0：已结束，1：即将开始
     */
    @Column("type")
    private Integer type;

    /**
     * 秒杀内部描述
     */
    @Column("content")
    private String content;

    /**
     * 秒杀开始时间
     */
    @Column("start_time")
    private String startTime;

    /**
     * 秒杀结束时间
     */
    @Column("end_time")
    private String endTime;

    /**
     * 秒杀费用
     */
    @Column("fee")
    private Double fee;

    private static final long serialVersionUID = 1L;
}