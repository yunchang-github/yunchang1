package com.weiziplus.springboot.models;

import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/3 11:53
 */
@Data
@Table("sku_asin")
public class SkuAsin implements Serializable {
    @Id("id")
    private Long id;

    @Column("sku")
    private String sku;

    @Column("asin")
    private String asin;

    @Column("fnsku")
    private String fnsku;
}
