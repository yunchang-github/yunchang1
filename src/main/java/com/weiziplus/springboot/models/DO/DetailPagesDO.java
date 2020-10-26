package com.weiziplus.springboot.models.DO;

import lombok.Data;

import java.util.List;

@Data
public class DetailPagesDO {
    private String sellerId;
    private String areaUrl;
    private String date;
    private List<String> column;
    private List<List<String>> data;

}
