package com.weiziplus.springboot.models.VO.searchWord;

import com.weiziplus.springboot.base.Column;
import com.weiziplus.springboot.base.Id;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 1
 */
@Data
public class SearchWordVo implements Serializable {

    @Column("customer_search_term")
    private String customerSearchTerm;

    @Column("ctr_o")
    private Double ctrO;

    @Column("ctr_s")
    private Double ctrS;

    @Column("ctr_t")
    private Double ctrT;

    @Column("ctr_f")
    private Double ctrF;

    @Column("week_o")
    private Integer weekO;

    @Column("week_s")
    private Integer weekS;

    @Column("week_t")
    private Integer weekT;

    @Column("week_f")
    private Integer weekF;

    @Column("year_o")
    private Integer yearO;

    @Column("year_s")
    private Integer yearS;

    @Column("year_t")
    private Integer yearT;

    @Column("year_f")
    private Integer yearF;

    @Column("seven_day_total_sales_o")
    private Double sevenDayTotalSalesO;

    @Column("seven_day_total_sales_s")
    private Double sevenDayTotalSalesS;

    @Column("seven_day_total_sales_t")
    private Double sevenDayTotalSalesT;

    @Column("seven_day_total_sales_f")
    private Double sevenDayTotalSalesF;

    @Column("seven_day_total_units_o")
    private Integer sevenDayTotalUnitsO;

    @Column("seven_day_total_units_s")
    private Integer sevenDayTotalUnitsS;

    @Column("seven_day_total_units_t")
    private Integer sevenDayTotalUnitsT;

    @Column("seven_day_total_units_f")
    private Integer sevenDayTotalUnitsF;

    @Column("acos_o")
    private Integer acosO;

    @Column("acos_s")
    private Integer acosS;

    @Column("acos_t")
    private Integer acosT;

    @Column("acos_f")
    private Integer acosF;

    @Column("sum_of_sales_o")
    private Double sumofSalesO;

    @Column("sum_of_sales_s")
    private Double sumofSalesS;

    @Column("sum_of_sales_t")
    private Double sumofSalesT;

    @Column("sum_of_sales_f")
    private Double sumofSalesF;

    @Column("sum_impressions_o")
    private Integer sumImpressionsO;

    @Column("sum_impressions_s")
    private Integer sumImpressionsS;

    @Column("sum_impressions_t")
    private Integer sumImpressionsT;

    @Column("sum_impressions_f")
    private Integer sumImpressionsF;

    @Column("cr_o")
    private Double crO;

    @Column("cr_s")
    private Double crS;

    @Column("cr_t")
    private Double crT;

    @Column("cr_f")
    private Double crF;

    @Column("sum_of_click_o")
    private Double sumofClickO;

    @Column("sum_of_click_s")
    private Double sumofClickS;

    @Column("sum_of_click_t")
    private Double sumofClickT;

    @Column("sum_of_click_f")
    private Double sumofClickF;

    @Column("sum_clicks_o")
    private Integer sumClicksO;

    @Column("sum_clicks_s")
    private Integer sumClicksS;

    @Column("sum_clicks_t")
    private Integer sumClicksT;

    @Column("sum_clicks_f")
    private Integer sumClicksF;

    @Column("month_o")
    private Integer monthO;

    @Column("month_s")
    private Integer monthS;

    @Column("month_t")
    private Integer monthT;

    @Column("month_f")
    private Integer monthF;

    @Column("seven_day_advertised_sku_sales_o")
    private Double sevenDayAdvertisedSkuSalesO;

    @Column("seven_day_advertised_sku_sales_s")
    private Double sevenDayAdvertisedSkuSalesS;

    @Column("seven_day_advertised_sku_sales_t")
    private Double sevenDayAdvertisedSkuSalesT;

    @Column("seven_day_advertised_sku_sales_f")
    private Double sevenDayAdvertisedSkuSalesF;

    @Column("spend_o")
    private Double spendO;

    @Column("spend_s")
    private Double spendS;

    @Column("spend_t")
    private Double spendT;

    @Column("spend_f")
    private Double spendF;

    @Column("cpc_o")
    private Double cpcO;

    @Column("cpc_s")
    private Double cpcS;

    @Column("cpc_t")
    private Double cpcT;

    @Column("cpc_f")
    private Double cpcF;

    @Column("sum_impression_o")
    private Double sumImpressionO;

    @Column("sum_impression_s")
    private Double sumImpressionS;

    @Column("sum_impression_t")
    private Double sumImpressionT;

    @Column("sum_impression_f")
    private Double sumImpressionF;

    public SearchWordVo() {
        this.ctrO = 0.0;
        this.ctrS = 0.0;
        this.ctrT = 0.0;
        this.ctrF = 0.0;
        this.sevenDayTotalSalesO = 0.0;
        this.sevenDayTotalSalesS = 0.0;
        this.sevenDayTotalSalesT = 0.0;
        this.sevenDayTotalSalesF = 0.0;
        this.sevenDayTotalUnitsO = 0;
        this.sevenDayTotalUnitsS = 0;
        this.sevenDayTotalUnitsT = 0;
        this.sevenDayTotalUnitsF = 0;
        this.acosO = 0;
        this.acosS = 0;
        this.acosT = 0;
        this.acosF = 0;
        this.sumofSalesO = 0.0;
        this.sumofSalesS = 0.0;
        this.sumofSalesT = 0.0;
        this.sumofSalesF = 0.0;
        this.sumImpressionsO = 0;
        this.sumImpressionsS = 0;
        this.sumImpressionsT = 0;
        this.sumImpressionsF = 0;
        this.crO = 0.0;
        this.crS = 0.0;
        this.crT = 0.0;
        this.crF = 0.0;
        this.sumofClickO = 0.0;
        this.sumofClickS = 0.0;
        this.sumofClickT = 0.0;
        this.sumofClickF = 0.0;
        this.sumClicksO = 0;
        this.sumClicksS = 0;
        this.sumClicksT = 0;
        this.sumClicksF = 0;
        this.sevenDayAdvertisedSkuSalesO = 0.0;
        this.sevenDayAdvertisedSkuSalesS = 0.0;
        this.sevenDayAdvertisedSkuSalesT = 0.0;
        this.sevenDayAdvertisedSkuSalesF = 0.0;
        this.spendO = 0.0;
        this.spendS = 0.0;
        this.spendT = 0.0;
        this.spendF = 0.0;
        this.cpcO = 0.0;
        this.cpcS = 0.0;
        this.cpcT = 0.0;
        this.cpcF = 0.0;
        this.sumImpressionO = 0.0;
        this.sumImpressionS = 0.0;
        this.sumImpressionT = 0.0;
        this.sumImpressionF = 0.0;
    }
}
