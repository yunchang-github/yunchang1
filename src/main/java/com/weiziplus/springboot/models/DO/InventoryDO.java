package com.weiziplus.springboot.models.DO;

import lombok.Data;

@Data
public class InventoryDO {
    private String cSku;
    private String cAsin;
    private String pSku;
    private String shop;
    private String area;
    private Integer afnFulfillableQuantity;//可售库存
    private Integer afnInboundShippedQuantity;//待签收库存
    private Integer afnInboundReceivingQuantity;//已签收库存
    private Integer reservedFcTransfers;//转库中库存
    private Integer totalInventory;//总库存
    private Integer afnUnsellableQuantity;//不可售库存
    private Integer sumInvAge90PlusDays;//库存大于90天库存

}
