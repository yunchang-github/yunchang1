package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.RebuildingShipment;
import com.weiziplus.springboot.service.data.manualimport.RebuildingShipmentService;
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
 * @data 2019/7/17 16:56
 */
@Api(value="RebuildingShipmentController",tags={"重建货件(手动导入)"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/rebuildingShipment")
public class RebuildingShipmentController {

    @Autowired
    RebuildingShipmentService service;

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
            @ApiParam(value = "当前页", required = true)@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @ApiParam(value = "新ShipmentID", required = false) @RequestParam(value = "newShipmentId", required = false) String newShipmentId,
            @ApiParam(value = "被替换的ShipmentID", required = false) @RequestParam(value = "replaceShipmentId", required = false ) String replaceShipmentId
                    ) {
        Map<String,Object> map =  new HashMap<>(4);

        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("newShipmentId",newShipmentId);
        map.put("replaceShipmentId",replaceShipmentId);
        return service.getPageList(map);
    }

    /**
     * 新增
     *
     * @param rebuildingShipment
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add( @ApiParam(value = "重建货件对象", required = true)RebuildingShipment rebuildingShipment) {
        return service.add(rebuildingShipment);
    }

    /**
     * 修改
     *
     * @param rebuildingShipment
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update( @ApiParam(value = "重建货件对象", required = true)RebuildingShipment rebuildingShipment) {
        return service.update(rebuildingShipment);
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
