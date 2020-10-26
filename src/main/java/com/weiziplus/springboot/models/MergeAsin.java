package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * ASIN合并信息(手动导入)
 * merge_asin
 * @author WeiziPlus
 * @date 2019-07-26 17:33:18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("merge_asin")
public class MergeAsin implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

    /**
     * 区域
     */
    @Column("area")
    private String area;

    /**
     * 店铺
     */
    @Column("shop")
    private String shop;

    /**
     * asin
     */
    @Column("asin")
    private String asin;

    /**
     * 被合并ASIN
     */
    @Column("merge_asin")
    private String mergeAsin;

    private static final long serialVersionUID = 1L;
}