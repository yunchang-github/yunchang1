package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * original_data_financial_shipment_event_list_shipment_event_item
 * @author Administrator
 * @date 2019-11-27 08:49:20
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("original_data_financial_shipment_event_list_shipment_event_item")
public class OriginalDataFinancialShipmentEventListShipmentEventItem implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * original_data_financial_shipment_event_list_shipment_event的id
     */
    @Column("p_id")
    private Long pId;

    /**
     */
    @Column("seller_SKU")
    private String sellerSku;

    /**
     */
    @Column("quantity_shipped")
    private Integer quantityShipped;

    /**
     */
    @Column("order_item_id")
    private String orderItemId;

    private static final long serialVersionUID = 1L;
}