package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/2 14:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("data_dictionary")
public class DataDictionary implements Serializable {
    @Id("id")
    private Long id;

    @Id("code")
    private String code;

    @Column("name")
    private String name;

    @Column("value")
    private String value;

    @Column("remark")
    private String remark;

    private List<DataDictionaryValue> values;
}
