package com.weiziplus.springboot.models;

import lombok.Data;

@Data
public class RefreshTokenDO {
    private Long id;
    private String sellerId;
    private String regionCode;
    private String refreshToken;
}
