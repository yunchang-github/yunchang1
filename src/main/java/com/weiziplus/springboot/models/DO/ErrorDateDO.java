package com.weiziplus.springboot.models.DO;

import lombok.Data;

@Data
public class ErrorDateDO {
    private Integer id;
    private String sellerId;
    private String area;
    private String errorDate;
    /**
     * type  未获得的报告类型（1：productads，2：campaigns，3：adgroups，4：targets，5：keywords，6：asins）
     * */
    private Integer type;
    /**
     * status  状态分为1：获取数据失败 2：获取数据为空
     * */
    private Integer status;
    private String createTime;
}
