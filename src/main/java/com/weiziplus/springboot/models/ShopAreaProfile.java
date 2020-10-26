package com.weiziplus.springboot.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("shop_area_profile")
public class ShopAreaProfile implements Serializable {
    @Id("id")
    private Integer id;
    @Column("shop_id")
    private Long shopId;
    @Column("area_id")
    private Long areaId;
    @Column("profile_id")
    private String profileId;
    @Column("region_code")
    private String regionCode;
    @Column("status")
    private Integer status;
}
