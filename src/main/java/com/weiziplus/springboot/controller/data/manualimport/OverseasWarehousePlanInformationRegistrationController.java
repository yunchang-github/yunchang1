package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.OverseasWarehousePlanInformationRegistration;
import com.weiziplus.springboot.service.data.manualimport.OverseasWarehousePlanInformationRegistrationService;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/18 16:18
 */
@Api(value="OverseasWarehousePlanInformationRegistrationController",tags={"海外仓计划信息登记（手动导入）"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/overseasWarehousePlanInformationRegistration")
public class OverseasWarehousePlanInformationRegistrationController {

    @Autowired
    OverseasWarehousePlanInformationRegistrationService service;

    /**
     * 获取分页列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询列表", notes = "查询列表")
    @GetMapping("/getPageList")
    public ResultUtil getPageList(
            @ApiParam(value = "当前页", required = true) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @ApiParam(value = "msku", required = false)@RequestParam(value = "msku",required = false) String msku,
            @ApiParam(value = "fnsku", required = false)@RequestParam(value = "fnsku",required = false) String fnsku,
            @ApiParam(value = "本地库存", required = false)@RequestParam(value = "localSku",required = false) String localSku,
            @ApiParam(value = "shipmentId", required = false)@RequestParam(value = "shipmentId",required = false) String shipmentId,
            @ApiParam(value = "箱号", required = false)@RequestParam(value = "placeBoxNumber",required = false) String placeBoxNumber,
            @ApiParam(value = "店铺", required = false) @RequestParam(value = "shop",required = false) String shop,
            @ApiParam(value = "区域", required = false)@RequestParam(value = "area",required = false) String area
    ) {
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("msku", msku);
        map.put("fnsku", fnsku);
        map.put("localSku", localSku);
        map.put("shipmentId", shipmentId);
        map.put("placeBoxNumber", placeBoxNumber);
        map.put("shop", shop);
        map.put("area", area);
        map.put("shopArray", StringUtil.parseReqParamToArrayUseTrim(shop));
        map.put("areaArray", StringUtil.parseReqParamToArrayUseTrim(area));
        return service.getPageList(map);
    }

    /**
     * 新增
     *
     * @param overseasWarehousePlanInformationRegistration
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "海外仓计划信息登记对象", required = true)OverseasWarehousePlanInformationRegistration overseasWarehousePlanInformationRegistration) {
        return service.add(overseasWarehousePlanInformationRegistration);
    }

    /**
     * 修改
     *
     * @param overseasWarehousePlanInformationRegistration
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "海外仓计划信息登记对象", required = true)OverseasWarehousePlanInformationRegistration overseasWarehousePlanInformationRegistration) {
        return service.update(overseasWarehousePlanInformationRegistration);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/delete")
    public ResultUtil delete(@ApiParam(value = "所选id,数组", required = true)Long[] ids) {
        return service.delete(ids);
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传", notes = "上传")
    @PostMapping("/upload")
    public ResultUtil upload(MultipartFile file) {
        return service.upload(file);
    }

    /**
     * 模板下载
     *
     * @param response
     * @return
     */
    @ApiOperation(value = "下载模板", notes = "下载模板")
    @PostMapping("/downTemplate")
    public void downTemplate(HttpServletResponse response) {
        service.downTemplate(response);
    }
}
