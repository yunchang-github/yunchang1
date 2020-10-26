package com.weiziplus.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import com.weiziplus.springboot.base.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanglongwei
 * @data 2019/7/12 9:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table("user_shop")
public class UserShop implements Serializable {

    @Id("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("shop_id")
    private Long shopId;
}
