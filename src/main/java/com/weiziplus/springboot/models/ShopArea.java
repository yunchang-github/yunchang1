package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/11 11:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("shop_area")
public class ShopArea implements Serializable {

    @Id("id")
    private Long id;

    @Column("shop_id")
    private Long shopId;

    @Column("area_id")
    private Long areaId;
}
