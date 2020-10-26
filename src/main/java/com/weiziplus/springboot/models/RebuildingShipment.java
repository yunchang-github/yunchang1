package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 重建货件(手动导入)
 * rebuilding_shipment
 * @author WeiziPlus
 * @date 2019-07-26 17:48:13
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("rebuilding_shipment")
public class RebuildingShipment implements Serializable {
    /**
     * 主键，自增
     */
    @Id("id")
    private Long id;

    /**
     * 新ShipmentID
     */
    @Column("new_shipment_id")
    private String newShipmentId;

    /**
     * 被替换的ShipmentID
     */
    @Column("replace_shipment_id")
    private String replaceShipmentId;

    private static final long serialVersionUID = 1L;
}