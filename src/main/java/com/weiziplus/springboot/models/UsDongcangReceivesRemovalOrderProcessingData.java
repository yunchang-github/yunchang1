package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * us_dongcang_receives_removal_order_processing_data
 * @author Administrator
 * @date 2019-08-15 08:51:42
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("us_dongcang_receives_removal_order_processing_data")
public class UsDongcangReceivesRemovalOrderProcessingData implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("shop")
    private String shop;

    /**
     */
    @Column("area")
    private String area;

    /**
     * 运单号
     */
    @Column("tracking_number")
    private String trackingNumber;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     * 处理方式
     */
    @Column("processing_method")
    private String processingMethod;

    /**
     * 新fnsku
     */
    @Column("new_fnsku")
    private String newFnsku;

    /**
     * 是否换标
     */
    @Column("is_change_standard")
    private String isChangeStandard;

    /**
     * 分仓代号
     */
    @Column("warehouse_code")
    private String warehouseCode;

    /**
     * 实际签收数量
     */
    @Column("actual_number_receipts")
    private Integer actualNumberReceipts;

    /**
     * 放置箱号
     */
    @Column("place_box_number")
    private String placeBoxNumber;

    /**
     * 箱号状态
     */
    @Column("box_status")
    private String boxStatus;

    /**
     */
    @Column("shipmentID")
    private String shipmentid;

    /**
     * 签收时间
     */
    @Column("receive_time")
    private String receiveTime;

    private static final long serialVersionUID = 1L;
}