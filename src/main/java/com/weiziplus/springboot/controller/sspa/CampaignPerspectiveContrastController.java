package com.weiziplus.springboot.controller.sspa;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.sspa.CampaignPerspectiveContrastService;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/7/8 9:27
 */

@Api(value="CampaignPerspectiveContrastController",tags={"SSPACampaign表现透视对比"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/sspa/campaignPerspectiveContrast")
public class CampaignPerspectiveContrastController {

    @Autowired
    CampaignPerspectiveContrastService service;

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
            @RequestParam(value = "monday", required = false) String monday) {
        return service.getPageList(pageNum, pageSize, monday);
    }

    /**
     * 得到周一列表
     *
     * @return
     */
    @ApiOperation(value = "得到周一列表", notes = "得到周一列表")
    @GetMapping("/getMondayList")
    public ResultUtil getMondayList() {
        return service.getMondayList();
    }
}
