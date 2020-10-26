package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * shipment_us_west_warehouse_record
 * @author Administrator
 * @date 2019-10-31 11:46:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("shipment_us_west_warehouse_record")
public class ShipmentUsWestWarehouseRecord implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     */
    @Column("fnsku")
    private String fnsku;

    /**
     * 库存SKU
     */
    @Column("instock_sku")
    private String instockSku;

    /**
     * 原店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 原区域
     */
    @Column("area")
    private String area;

    /**
     * shipmentID
     */
    @Column("shipment_id")
    private String shipmentId;

    /**
     * 数量
     */
    @Column("number")
    private String number;

    /**
     * 箱号
     */
    @Column("case_number")
    private String caseNumber;

    /**
     * 出库时间
     */
    @Column("outbound_time")
    private String outboundTime;

    /**
     * 海外仓预计接收时间
     */
    @Column("expected_receive_time")
    private String expectedReceiveTime;

    /**
     * 接收数量
     */
    @Column("receive_number")
    private Integer receiveNumber;

    /**
     * 接收时间
     */
    @Column("receive_time")
    private String receiveTime;

    private static final long serialVersionUID = 1L;
}