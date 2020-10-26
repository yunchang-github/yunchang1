package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * remove_order_action_record
 * @author Administrator
 * @date 2019-08-15 17:08:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("remove_order_action_record")
public class RemoveOrderActionRecord implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 移除店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 移除区域
     */
    @Column("area")
    private String area;

    /**
     * 接收区域
     */
    @Column("receive_shop")
    private String receiveShop;

    /**
     * 接收区域
     */
    @Column("receive_area")
    private String receiveArea;

    /**
     * 订单号
     */
    @Column("order_number")
    private String orderNumber;

    /**
     * 运单号
     */
    @Column("tracking_number")
    private String trackingNumber;

    /**
     * fnsku
     */
    @Column("fnsku")
    private String fnsku;

    /**
     */
    @Column("msku")
    private String msku;

    /**
     * 订单类型(0:退仓,1:赠品)
     */
    @Column("order_type")
    private Integer orderType;

    /**
     * 接收仓位
     */
    @Column("receiving_position")
    private String receivingPosition;

    /**
     * 退仓类型(0:发走,!:存放)
     */
    @Column("refund_type")
    private Integer refundType;

    /**
     * 分仓代号
     */
    @Column("warehouse_code")
    private String warehouseCode;

    /**
     * 是否换标(0:换标,1:不换标)
     */
    @Column("is_change")
    private Integer isChange;

    /**
     * 新msku
     */
    @Column("new_msku")
    private String newMsku;

    /**
     * 新fnsku
     */
    @Column("new_fnsku")
    private String newFnsku;

    /**
     */
    @Column("date")
    private String date;

    private static final long serialVersionUID = 1L;
}
