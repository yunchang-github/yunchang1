package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import java.io.Serializable;
import lombok.Data;

/**
 * 获取数据出错记录
 * data_get_error_record
 * @author WeiziPlus
 * @date 2019-08-21 17:03:33
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("data_get_error_record")
public class DataGetErrorRecord implements Serializable {
    /**
     */
    @Id("id")
    private Long id;

    /**
     * 数据获取出错时间
     */
    @Column("date")
    private String date;

    /**
     * 类型，
     * 0:亚马逊MwsApi定时任务
     * 1:浏览器亚马逊后台定时任务
     */
    @Column("type")
    private Integer type;

    /**
     * 任务名称
     */
    @Column("name")
    private String name;

    /**
     * 是否处理，0:未处理,1:已处理
     */
    @Column("is_handle")
    private Integer isHandle;

    /**
     * 网点名称
     */
    @Column("shop")
    private String shop;

    /**
     * 国家代码
     */
    @Column("area")
    private String area;

    /**
     * 错误详情备注
     */
    @Column("remark")
    private String remark;

    /**
     * 记录创建时间
     */
    @Column("create_time")
    private String createTime;

    private static final long serialVersionUID = 1L;
}