package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:41
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("data_dictionary_value")
public class DataDictionaryValue implements Serializable {
    @Id("id")
    private Long id;

    @Column("parent_id")
    private Long parentId;

    @Column("value")
    private String value;

    @Column("remark")
    private String remark;
}
