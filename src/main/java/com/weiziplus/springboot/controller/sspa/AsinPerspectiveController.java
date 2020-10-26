package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.AsinPerspectiveService;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/3 11:55
 */

@Api(value="AsinPerspectiveController",tags={"SSPA Campaign表现透视"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sspa/asinPerspective")
public class AsinPerspectiveController {

    @Autowired
    AsinPerspectiveService service;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "campaignName", required = false) String campaignName,
            @RequestParam(value = "adGroupName", required = false) String adGroupName,
            @RequestParam(value = "asin", required = false) String asin) {
        return service.getPageList(pageNum, pageSize, month, date, campaignName, adGroupName, asin);
    }

    /**
     * 获取列表数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取列表数据", notes = "获取列表数据")
    @GetMapping("/getList")
    public ResultUtil getList(
            @RequestParam(value = "type", defaultValue = "date") String type,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "campaignName", required = false) String campaignName,
            @RequestParam(value = "adGroupName", required = false) String adGroupName,
            @RequestParam(value = "asin", required = false) String asin,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area
            ) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("type",type);
        map.put("month",month);
        map.put("date",date);
        map.put("campaignName",campaignName);
        map.put("campaignNameArray", StringUtil.parseReqParamToArrayUseTrim(campaignName));
        map.put("adGroupName",adGroupName);
        map.put("adGroupNameArray",StringUtil.parseReqParamToArrayUseTrim(adGroupName));
        map.put("asin",asin);
        map.put("asinArray",StringUtil.parseReqParamToArrayUseTrim(asin));
        map.put("shop",shop);
        map.put("shopArray",StringUtil.parseReqParamToArrayUseTrim(shop));
        map.put("area",area);
        map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));

        return service.getList(map);
    }

    /**
     * 获取年月列表
     */
    @ApiOperation(value = "获取年月列表", notes = "获取年月列表")
    @GetMapping("/getYearMonthList")
    public ResultUtil getYearMonthList() {
        return service.getYearMonthList();
    }

    /**
     * 获取Advertised ASIN
     *
     * @return
     */
//    @ApiOperation(value = "获取Advertised ASIN", notes = "获取Advertised ASIN")
//    @GetMapping("/getAdvertisedAsin")
//    public ResultUtil getAdvertisedAsin() {
//        Map map=null;
//        return service.getAdvertisedAsin(map);
//    }

    /**
     * 获取campaign_name
     *
     * @return
     */
    @ApiOperation(value = "获取campaign_name", notes = "获取campaign_name")
    @GetMapping("/getCampaignName")
    public ResultUtil getCampaignName() {
        Map map=null;
        return service.getCampaignName(map);
    }

    /**
     * 获取ad_group_name
     *
     * @return
     */
    @ApiOperation(value = "获取ad_group_name", notes = "获取ad_group_name")
    @GetMapping("/getAdGroupName")
    public ResultUtil getAdGroupName() {
        return service.getAdGroupName();
    }
}
