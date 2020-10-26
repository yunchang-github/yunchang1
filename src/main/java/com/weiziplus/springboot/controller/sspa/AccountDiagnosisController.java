package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.AccountDiagnosisService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/1 16:08
 */

@Api(value = "AccountDiagnosisController", tags = {"SSPA账户诊断"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sspa/accountDiagnosis")
public class AccountDiagnosisController {

    @Autowired
    AccountDiagnosisService service;


    /**
     * 账户总体数据
     *
     * @return
     */
    @ApiOperation(value = " 账户总体数据", notes = " 账户总体数据")
    @GetMapping("/accountOverall")
    public ResultUtil accountOverall(String shop, String area) {
        return service.accountOverall(shop, area);
    }

    /**
     * 过去四周TOP 10 ASIN
     *
     * @return
     */
    @ApiOperation(value = "过去四三个月TOP 10 ASIN", notes = "过去三个月TOP 10 ASIN")
    @GetMapping("/pastThreeMonths")
    public ResultUtil pastThreeMonths(String shop, String area) {
        return service.pastThreeMonths(shop,area);
    }

    /**
     * 账户广告活动诊断
     *
     * @return
     */
    @ApiOperation(value = "账户广告活动诊断", notes = "账户广告活动诊断")
    @GetMapping("/accountCampaignDiagnostics")
    public ResultUtil accountCampaignDiagnostics(String date, String shop, String area) {
        return service.accountCampaignDiagnostics(date, shop, area);
    }

    /**
     * 账户核心广告数据过去三个月变化趋势
     *
     * @return
     */
    @ApiOperation(value = "账户核心广告数据过去三个月变化趋势", notes = "账户核心广告数据过去三个月变化趋势")
    @GetMapping("/accountCoreAdvertisingDataTrendsThreeMonths")
    public ResultUtil accountCoreAdvertisingDataTrendsThreeMonths() {
        return service.accountCoreAdvertisingDataTrendsThreeMonths();
    }

    /**
     * 广告clicks/访问次数
     *
     * @return
     */
    @ApiOperation(value = "广告clicks/访问次数", notes = "广告clicks/访问次数")
    @GetMapping("/adClicksVisits")
    public ResultUtil adClicksVisits() {
        return service.adClicksVisits();
    }

    /**
     * 展现量
     *
     * @return
     */
    @ApiOperation(value = "展现量", notes = "展现量")
    @GetMapping("/impressions")
    public ResultUtil impressions(String shop, String area) {
        return service.impressions(shop,area);
    }
}
