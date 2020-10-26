package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.CampaignPerspectiveService;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/3 15:31
 */
@Api(value="CampaignPerspectiveController",tags={"SSPACampaign表现透视"})
@RestController
@AdminAuthToken
@RequestMapping("/pc/sspa/campaignPerspective")
public class CampaignPerspectiveController {

    @Autowired
    CampaignPerspectiveService service;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */


    @ApiOperation(value = "获取分页数据", notes = "获取Campaign表现透视")
    @GetMapping("/getCampaignPageList")
    public ResultUtil getCampaignPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "type", defaultValue = "week") String type,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "asin", required = false) String asin,
            @RequestParam(value = "campaignName",required = false) String campaignName,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area,
            @RequestParam(value = "sortName",required = false,defaultValue = "sumImpressions") String sortName,
            @RequestParam(value = "sortRule",required = false) String sortRule
    ) {
        if(StringUtils.isEmpty(shop) || StringUtils.isEmpty(area)){
            return ResultUtil.success("未选择店铺及区域");
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        map.put("type",type);
        map.put("date",date);
        map.put("paramDateArry",StringUtil.parseReqParamToArrayUseTrim(date));
        map.put("asin",asin);
        map.put("asinArray",StringUtil.parseReqParamToArrayUseTrim(asin));
        map.put("campaignName",campaignName);
        map.put("shop",shop);
        map.put("shopArray", StringUtil.parseReqParamToArrayUseTrim(shop));
        map.put("area",area);
        map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
        map.put("sortName",sortName);
        map.put("sortRule",sortRule);
        return service.getCampaignPageList(map);
    }


    /**
     * 获取列表数据
     *
     * @return
     */
    @ApiOperation(value = "获取列表数据", notes = "获取列表数据")
    @GetMapping("/getList")
    public ResultUtil getList(
            @RequestParam(value = "type", defaultValue = "week") String type,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "asin", required = false) String asin,
            @RequestParam(value = "campaignName",required = false) String campaignName,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area
            ) {
            Map<String,Object> map  = new HashMap<String,Object>();
            map.put("type",type);
            map.put("date",date);
            map.put("dateArray",StringUtil.parseReqParamToArrayUseTrim(date));
            map.put("asin",asin);
            map.put("asinArray",StringUtil.parseReqParamToArrayUseTrim(asin));
            map.put("campaignName",campaignName);
            map.put("shop",shop);
            map.put("shopArray",StringUtil.parseReqParamToArrayUseTrim(shop));
            map.put("area",area);
            map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
        return service.getList(map);
    }

    /**
     * 获取Advertised ASIN
     * @return
//     */
    @ApiOperation(value = "获取Advertised ASIN", notes = "获取Advertised ASIN")
    @GetMapping("/getAdvertisedAsin")
    public ResultUtil getAdvertisedAsin(String sellerId,String shopArea) {
        if(null == sellerId || sellerId == ""){
            return ResultUtil.error("sellerId为空",sellerId);
        }
        if(null == shopArea || shopArea == ""){
            return ResultUtil.error("shopArea为空",shopArea);
        }
        return service.getAdvertisedAsin(sellerId,shopArea);
    }
}
