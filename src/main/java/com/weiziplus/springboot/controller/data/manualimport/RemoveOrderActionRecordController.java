package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.RemoveOrderActionRecord;
import com.weiziplus.springboot.service.data.manualimport.RemoveOrderActionRecordService;
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
 * @data 2019/7/18 15:59
 */

@Api(value="RemoveOrderActionRecordController",tags={"移除订单操作登记(手动导入)"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/removeOrderActionRecord")
public class RemoveOrderActionRecordController {

    @Autowired
    RemoveOrderActionRecordService service;

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
            @ApiParam(value = "订单号", required = false) @RequestParam(value = "code", required = false) String code,
            @ApiParam(value = "fnsku", required = false) @RequestParam(value = "fmsku", required = false ) String fmsku,
            @ApiParam(value = "运单号", required = false) @RequestParam(value = "ydcode", required = false ) String ydcode,
            @ApiParam(value = "新fnsku", required = false) @RequestParam(value = "newfmsku", required = false ) String newfmsku,
            @ApiParam(value = "新msku", required = false) @RequestParam(value = "newmsku", required = false ) String newmsku,
            @ApiParam(value = "店铺", required = false) @RequestParam(value = "shop", required = false ) String shop,
            @ApiParam(value = "区域", required = false) @RequestParam(value = "area", required = false ) String area,
            @ApiParam(value = "退仓类型(0:发走,!:存放)", required = false) @RequestParam(value = "refundType", required = false ) Integer refundType,
            @ApiParam(value = "订单类型(0:退仓,1:赠品)", required = false) @RequestParam(value = "orderType", required = false ) Integer orderType
    ) {
        Map<String,Object> map =  new HashMap<>();
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("code",code);
        map.put("fmsku",fmsku);
        map.put("ydcode",ydcode);
        map.put("newfmsku",newfmsku);
        map.put("newmsku",newmsku);
        map.put("orderType",orderType);
        map.put("refundType",refundType);
        map.put("shop", shop);
        map.put("shopArray", StringUtil.parseReqParamToArrayUseTrim(shop));
        map.put("area", area);
        map.put("areaArray",StringUtil.parseReqParamToArrayUseTrim(area));
        return service.getPageList(map);

    }

    /**
     * 新增
     *
     * @param removeOrderActionRecord
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "移除订单操作登记对象", required = true)RemoveOrderActionRecord removeOrderActionRecord) {
        return service.add(removeOrderActionRecord);
    }

    /**
     * 修改
     *
     * @param removeOrderActionRecord
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "移除订单操作登记对象", required = true)RemoveOrderActionRecord removeOrderActionRecord) {
        return service.update(removeOrderActionRecord);
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
