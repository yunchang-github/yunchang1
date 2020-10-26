package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * us_xicang_case_number_logistics_progress
 * @author Administrator
 * @date 2019-10-31 15:46:01
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("us_xicang_case_number_logistics_progress")
public class UsXicangCaseNumberLogisticsProgress implements Serializable {
    /**
     * 自增
     */
    @Id("id")
    private Long id;

    /**
     * 箱号
     */
    @Column("case_number")
    private String caseNumber;

    /**
     * 接收数量
     */
    @Column("receive_number")
    private Integer receiveNumber;

    /**
     * 接收时间
     */
    @Column("receive_time")
    private String receiveTime;

    private static final long serialVersionUID = 1L;
}