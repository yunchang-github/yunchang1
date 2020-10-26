package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.PlacePerspectiveService;
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
 * @data 2019/7/5 14:17
 */
@Api(value="PlacePerspectiveController",tags={"SSPA位置透视"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sspa/placePerspective")
public class PlacePerspectiveController {

    @Autowired
    PlacePerspectiveService service;

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
            @RequestParam(value = "date", required = false) String date) {
        return service.getPageList(pageNum, pageSize, date);
    }



    /**
     * 获取列表数据
     *
     * @param
     * @param
     * @return
     */
//    @ApiOperation(value = "获取列表数据", notes = "获取列表数据")
//    @GetMapping("/getList")
//    public ResultUtil getList(
//            @RequestParam(value = "type", defaultValue = "week") String type,
//            @RequestParam(value = "date", required = false) String date,
//            @RequestParam(value = "paramDateArry", required = false) String paramDateArry,
//            @RequestParam(value = "campaignName",required = false) String campaignName,
//            @RequestParam(value = "biddingStrategy",required = false) String biddingStrategy,
//            @RequestParam(value = "shop",required = false) String shop,
//            @RequestParam(value = "area",required = false) String area,
//            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
//            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
//            ) {
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("type",type);
//        map.put("date",date);
//        map.put("paramDateArry",StringUtil.parseReqParamToArrayUseTrim(date));
//        map.put("campaignName",campaignName);
//        map.put("campaignNameArray", StringUtil.parseReqParamToArrayUseTrim(campaignName));
//        map.put("biddingStrategy",biddingStrategy);
//        map.put("biddingStrategyArray",StringUtil.parseReqParamToArrayUseTrim(biddingStrategy));
//        map.put("shop",shop);
//        map.put("shopArray",StringUtil.parseReqParamToArrayUseTrim(shop));
//        map.put("area",area);
//        map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
//        return service.getList(pageNum,pageSize,map);
//    }

    /**
     * 获取位置透视列表数据
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "获取列表数据", notes = "获取位置透视列表数据")
    @GetMapping("/getPlaceList")
    public ResultUtil getPlaceList(
            @RequestParam(value = "type", defaultValue = "week") String type,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "paramDateArry", required = false) String paramDateArry,
            @RequestParam(value = "campaignName",required = false) String campaignName,
            @RequestParam(value = "biddingStrategy",required = false) String biddingStrategy,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortName",required = false) String sortName,
            @RequestParam(value = "sortRule",required = false) String sortRule
    ) {
        if(StringUtils.isEmpty(shop) || StringUtils.isEmpty(area)){
            return ResultUtil.success("未选择店铺及区域");
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("type",type);
        map.put("date",date);
        map.put("paramDateArry",StringUtil.parseReqParamToArrayUseTrim(date));
        map.put("campaignName",campaignName);
        map.put("campaignNameArray", StringUtil.parseReqParamToArrayUseTrim(campaignName));
        map.put("biddingStrategy",biddingStrategy);
        map.put("biddingStrategyArray",StringUtil.parseReqParamToArrayUseTrim(biddingStrategy));
        map.put("shop",shop);
        map.put("shopArray",StringUtil.parseReqParamToArrayUseTrim(shop));
        map.put("area",area);
        map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
        map.put("sortName",sortName);
        map.put("sortRule",sortRule);
        return service.getPlaceList(pageNum,pageSize,map);
    }

    /**
     * 获取campaign_name
     *
     * @return
     */
    @ApiOperation(value = "获取campaign_name", notes = "获取campaign_name")
    @GetMapping("/getCampaignName")
    public ResultUtil getCampaignName(String sellerId,String shopArea) {
        if(null == sellerId || sellerId == ""){
            return ResultUtil.error("sellerId为空",sellerId);
        }
        if(null == shopArea || shopArea == ""){
            return ResultUtil.error("shopArea为空",shopArea);
        }
        return service.getCampaignName(sellerId,shopArea);
    }

}
