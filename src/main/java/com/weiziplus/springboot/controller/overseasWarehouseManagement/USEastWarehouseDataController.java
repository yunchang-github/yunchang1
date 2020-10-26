package com.weiziplus.springboot.controller.overseasWarehouseManagement;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.service.overseasWarehouseManagement.USEastWarehouseDataService;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(value="USEastWarehouseDataController",tags={"海外仓管理美东仓库数据"})

@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/overseasWarehouseManagement/USEastWarehouseData")
public class USEastWarehouseDataController {
    @Autowired
    USEastWarehouseDataService service;

    @ApiOperation(value = "分页列表", notes = "分页列表")
    @GetMapping(value = "/getPageList")
    public ResultUtil getPageList(
            @ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "当前页", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @ApiParam(value = "开始时间", required = false)@RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(value = "结束时间", required = false)@RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(value = "msku", required = false)@RequestParam(value = "msku", required = false) String msku,
            @ApiParam(value = "箱号", required = false)@RequestParam(value = "placeBoxNumber", required = false) String placeBoxNumber
    ) {

        if (StringUtils.isBlank(endDate)) {
            endDate = DateUtil.getDate();
        }
        return service.getPageList(pageNum, pageSize, startDate, endDate, msku, placeBoxNumber);
    }


    //单个或者批量修改是否另行
    @ApiOperation(value = "单个或者批量修改是否另行", notes = "单个或者批量修改是否另行")
    @PutMapping(value = "/updateByIds")
    public ResultUtil updateByIds(Long[] ids, String flag) {
        return service.updateByIds(ids, flag);
    }

    /**
     * 修改是否另行安排发货
     *
     * @param id
     * @param value
     * @return
     */
    @ApiOperation(value = "修改是否另行安排发货", notes = "修改是否另行安排发货")
    @PostMapping("/updateIsShippedSeparately")
    public ResultUtil updateIsShippedSeparately(Long id, String value) {
        return service.updateIsShippedSeparately(id, value);
    }


}
