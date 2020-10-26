package com.weiziplus.springboot.models.DO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import lombok.Data;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * fba_replenishment_batch
 * @author 1
 * @date 2020-03-20 14:39:28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("fba_replenishment_batch")
public class FbaReplenishmentBatch implements Serializable {
    /**
     */
    @Id("id")
    private Integer id;

    /**
     * 补货批次号
     */
    @Column("replenishment_batch_no")
    private String replenishmentBatchNo;

    /**
     * msku
     */
    @Column("msku")
    private String msku;

    /**
     * title
     */
    @Column("title")
    private String title;

    /**
     * 本地sku
     */
    @Column("local_sku")
    private String localSku;

    /**
     * 补货量
     */
    @Column("replenishment")
    private Integer replenishment;

    /**
     * fnsku
     */
    @Column("fnsku")
    private String fnsku;

    /**
     * 图片流
     */
    private String barcodeBase64Str;

    private static final long serialVersionUID = 1L;
}