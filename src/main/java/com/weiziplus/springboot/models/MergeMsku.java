package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * MSKU合并信息(手动导入)
 * merge_msku
 * @author WeiziPlus
 * @date 2019-07-26 17:14:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("merge_msku")
public class MergeMsku implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

    /**
     * 店铺
     */
    @Column("shop")
    private String shop;

    /**
     * 区域
     */
    @Column("area")
    private String area;

    /**
     * msku
     */
    @Column("msku")
    private String msku;

    /**
     * 被合并的msku
     */
    @Column("merge_msku")
    private String mergeMsku;

    private static final long serialVersionUID = 1L;
}