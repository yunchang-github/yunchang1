package com.weiziplus.springboot.models.DO;

import lombok.Data;

/**
 * 库存
 * */
@Data
public class InventorySumDO {
    private String shopName;
    private String areaCode;
    private String sellerId;
    //可售库存
    private Integer sumAfnFulfillableQuantity;
    //待签收库存
    private Integer sumAfnInboundShippedQuantity;
    //已签收库存
    private Integer sumAfnInboundReceivingQuantity;
    //转库中库存
    private Integer sumReservedFcTransfers;

}
