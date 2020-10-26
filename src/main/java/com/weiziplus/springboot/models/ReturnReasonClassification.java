package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 退货原因分类（手动导入）
 * return_reason_classification
 * @author WeiziPlus
 * @date 2019-07-26 16:38:55
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("return_reason_classification")
public class ReturnReasonClassification implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 英文退货原因
     */
    @Column("eg_reason")
    private String egReason;

    /**
     * 中文退货原因
     */
    @Column("cn_reason")
    private String cnReason;

    /**
     * 退货分类
     */
    @Column("classification")
    private String classification;

    private static final long serialVersionUID = 1L;
}