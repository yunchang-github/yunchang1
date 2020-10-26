package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.SearchPerspectiveService;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/5 11:15
 */
@Api(value="SearchPerspectiveController",tags={"SSPA搜索词透视"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sspa/searchPerspective")
public class SearchPerspectiveController {

    @Autowired
    SearchPerspectiveService service;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
//    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
//    @GetMapping("/getPageList")
//    public ResultUtil getPageList(
//            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
//            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
//            @RequestParam(value = "type", defaultValue = "week") String type,
//            @RequestParam(value = "date", required = false) String date,
//            @RequestParam(value = "campaignName", required = false) String campaignName,
//            @RequestParam(value = "adGroupName", required = false) String adGroupName,
//            @RequestParam(value = "customerSearchTerm",required = false) String customerSearchTerm,
//            @RequestParam(value = "shop",required = false) String shop,
//            @RequestParam(value = "area",required = false) String area
//    ) {
//        Map<String,Object> map = new HashMap<String, Object>();
//        map.put("type",type);
//        map.put("date",date);
//        map.put("customerSearchTerm",customerSearchTerm);
//        map.put("campaignName", StringUtil.parseReqParamToString(campaignName));
//        map.put("adGroupName",StringUtil.parseReqParamToString(adGroupName));
//        map.put("shop",StringUtil.parseReqParamToString(shop));
//        map.put("area",StringUtil.parseReqParamToString(area));
//        return service.getPageList(pageNum, pageSize,map);
//    }


    /**
     * 获取搜索词透视分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    @GetMapping("/getSearchWordPageList")
    public ResultUtil getSearchWordPageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "type", defaultValue = "week") String type,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "campaignName", required = false) String campaignName,
            @RequestParam(value = "adGroupName", required = false) String adGroupName,
            @RequestParam(value = "customerSearchTerm",required = false) String customerSearchTerm,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area,
            @RequestParam(value = "sortName",required = false,defaultValue = "sumImpressionsO") String sortName,
            @RequestParam(value = "sortRule",required = false,defaultValue = "desc") String sortRule
    ) throws Exception {
        if(StringUtils.isEmpty(shop) || StringUtils.isEmpty(area)){
            return ResultUtil.success("未选择店铺及区域");
        }
        Map<String,Object> map = new HashMap<String, Object>();

        // 对参数日期数组进行处理
        List<String> dateArrayList = (List<String>) StringUtil.parseReqParamToArrayUseTrim(date);
        // 记录原始的数据顺序
        List<String> dateSortWeekList = new ArrayList<>();
        for (String dateA : dateArrayList) {
            if(dateA.contains("53")){
                dateA = (Integer.valueOf(dateA.substring(0,4)) + 1) + "01";
            }
            dateSortWeekList.add(String.valueOf(dateA));
        }
        map.put("type",type);
        map.put("date",date);
        map.put("dateWeekList",dateSortWeekList);
        map.put("paramDateArry",StringUtil.parseReqParamToArrayUseTrim(date));
        map.put("customerSearchTerm",customerSearchTerm);
        map.put("campaignName", StringUtil.parseReqParamToString(campaignName));
        map.put("adGroupName",StringUtil.parseReqParamToString(adGroupName));
        map.put("shop",StringUtil.parseReqParamToString(shop));
        map.put("area",StringUtil.parseReqParamToString(area));
        map.put("sortName",sortName);
        map.put("sortRule",sortRule);
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
//        return service.getAllSearchWordList(map);
        return service.getSearchWordListByObject(map);
//        return service.getSearchWordListNoContain(map);
//        return service.getAllSearchWordListObject(map);
    }

    /**
     * 删除redis中的数据
     * @param key 模糊删除key值
     */
    @PostMapping("/redisDelete")
    public ResultUtil redisDelete(String key){
        return ResultUtil.success(RedisUtil.deleteLikeKey(key));
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @ApiOperation(value = "获取列表数据", notes = "获取列表数据")
    @GetMapping("/getList")
    public ResultUtil getList(
            @RequestParam(value = "type", defaultValue = "date") String type,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "campaignName", required = false) String campaignName,
            @RequestParam(value = "adGroupName", required = false) String adGroupName,
            @RequestParam(value = "customerSearchTerm",required = false) String customerSearchTerm,
            @RequestParam(value = "shop",required = false) String shop,
            @RequestParam(value = "area",required = false) String area
            ) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("type",type);
        map.put("date",date);
        map.put("customerSearchTerm",customerSearchTerm);
        map.put("campaignName", StringUtil.parseReqParamToString(campaignName));
        map.put("adGroupName",StringUtil.parseReqParamToString(adGroupName));
        map.put("shop",StringUtil.parseReqParamToString(shop));
        map.put("area",StringUtil.parseReqParamToString(area));
        return service.getList(map);
    }

    /**
     * 获取总数据
     *
     * @return
     */
    @ApiOperation(value = "获取总数据", notes = "获取总数据")
    @GetMapping("/getSum")
    public ResultUtil getSum() {
        return service.getSum();
    }

    /**
     * 获取campaign_name
     *
     * @return
     */
    @ApiOperation(value = "获取campaign_name", notes = "获取campaign_name")
    @GetMapping("/getCampaignName")
    public ResultUtil getCampaignName(String sellerId,String shopArea,String adGroupName) {
        return service.getCampaignName(sellerId,shopArea,adGroupName);
    }

    /**
     * 获取ad_group_name
     *
     * @return
     */
    @ApiOperation(value = "获取ad_group_name", notes = "获取ad_group_name")
    @GetMapping("/getAdGroupName")
    public ResultUtil getAdGroupName(String sellerId,String shopArea,String campaignName) {
        return service.getAdGroupName(sellerId,shopArea,campaignName);
    }
}
