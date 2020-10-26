package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 货件对应物流信息
 * goods_correspondence_logistics_info
 * @author WeiziPlus
 * @date 2019-07-29 09:22:23
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("goods_correspondence_logistics_info")
public class GoodsCorrespondenceLogisticsInfo implements Serializable {
    /**
     * 主键，自增
     */
    @Id("id")
    private Long id;

    @Column("shop")
    private String shop;

    @Column("area")
    private String area;
    /**
     * 建计划日期
     */
    @Column("date")
    private String date;

    /**
     * ShipmentID
     */
    @Column("shipment_id")
    private String shipmentId;

    /**
     * WMS货件批次号
     */
    @Column("WMS_goods_number")
    private String wmsGoodsNumber;

    /**
     * 箱号
     */
    @Column("case_number")
    private String caseNumber;

    /**
     * 物流单号
     */
    @Column("logistics_number")
    private String logisticsNumber;

    /**
     * 收费物流单号
     */
    @Column("charge_logistics_number")
    private String chargeLogisticsNumber;

    /**
     * 预计物流方式
     */
    @Column("estimated_logistics_mode")
    private String estimatedLogisticsMode;

    /**
     * 实际物流方式
     */
    @Column("practical_logistics_mode")
    private String practicalLogisticsMode;

    /**
     * 承运商
     */
    @Column("carrier")
    private String carrier;

    /**
     * Code
     */
    @Column("code")
    private String code;



    /**
     * 长
     */
    @Column("long_column")
    private Double longColumn;

    /**
     * 宽
     */
    @Column("width")
    private Double width;

    /**
     * 高
     */
    @Column("height")
    private Double height;

    /**
     * 实际重
     */
    @Column("actual_weight")
    private Double actualWeight;

    /**
     * 体积重
     */
    @Column("volume_weight")
    private Double volumeWeight;

    /**
     * 申报金额
     */
    @Column("amount_declared")
    private Double amountDeclared;

    /**
     * 预估税金
     */
    @Column("estimated_tax")
    private Double estimatedTax;

    /**
     * 其他费用
     */
    @Column("other_expenses")
    private Double otherExpenses;

    /**
     * 预计运费
     */
    @Column("expected_freight")
    private Double expectedFreight;

    /**
     * 预计发货时间
     */
    @Column("estimated_delivery_time")
    private String estimatedDeliveryTime;

    /**
     * 实际发出时间
     */
    @Column("actual_departure_time")
    private String actualDepartureTime;

    /**
     * 预计检出时间
     */
    @Column("predicted_detection_time")
    private String predictedDetectionTime;

    private static final long serialVersionUID = 1L;
}
