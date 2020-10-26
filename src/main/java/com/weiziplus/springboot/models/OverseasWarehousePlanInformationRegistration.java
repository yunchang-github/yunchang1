package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * overseas_warehouse_plan_information_registration
 * @author Administrator
 * @date 2019-10-30 23:23:09
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("overseas_warehouse_plan_information_registration")
public class OverseasWarehousePlanInformationRegistration implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 区域
     */
    @Column("area")
    private String area;

    /**
     * 发货时间
     */
    @Column("fh_time")
    private String fhTime;

    /**
     * 放置箱号
     */
    @Column("place_box_number")
    private String placeBoxNumber;

    /**
     * 箱子状态
     */
    @Column("box_status")
    private String boxStatus;

    /**
     * ShipmentID
     */
    @Column("shipment_id")
    private String shipmentId;

    /**
     * 数量
     */
    @Column("number")
    private Integer number;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     * 本地库存
     */
    @Column("local_sku")
    private String localSku;

    private static final long serialVersionUID = 1L;
}