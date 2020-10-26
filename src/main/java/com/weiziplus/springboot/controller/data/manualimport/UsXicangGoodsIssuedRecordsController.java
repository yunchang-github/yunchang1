package com.weiziplus.springboot.controller.data.manualimport;

import com.weiziplus.springboot.interceptor.AdminAuthToken;
import com.weiziplus.springboot.models.UsXicangGoodsIssuedRecords;
import com.weiziplus.springboot.service.data.manualimport.UsXicangGoodsIssuedRecordsService;
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
 * @data 2019/7/18 17:15
 */
@Api(value="UsXicangGoodsIssuedRecordsController",tags={"美西仓商品发出记录"})
@RestController
//@ApiIgnore
@AdminAuthToken
@RequestMapping("/pc/data/manualImport/usXicangGoodsIssuedRecords")
public class UsXicangGoodsIssuedRecordsController {

    @Autowired
    UsXicangGoodsIssuedRecordsService service;

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
            @ApiParam(value = "msku", required = false) @RequestParam(value = "msku",required = false) String msku,
            @ApiParam(value = "fnsku", required = false)  @RequestParam(value = "fnsku",required = false) String fnsku,
            @ApiParam(value = "库存sku", required = false) @RequestParam(value = "inventorySku",required = false) String inventorySku,
            @ApiParam(value = "shipmentId", required = false) @RequestParam(value = "shipmentId",required = false) String shipmentId,
            @ApiParam(value = "箱号", required = false) @RequestParam(value = "caseNumber",required = false) String caseNumber,
            @ApiParam(value = "店铺", required = false) @RequestParam(value = "shop",required = false) String shop,
            @ApiParam(value = "区域", required = false) @RequestParam(value = "area",required = false) String area
    ) {
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("msku", msku);
        map.put("fnsku", fnsku);
        map.put("inventorySku", inventorySku);
        map.put("shipmentId", shipmentId);
        map.put("caseNumber", caseNumber);
        map.put("shop", shop);
        map.put("area", area);
        map.put("shopArray", StringUtil.parseReqParamToArrayUseTrim(shop));
        map.put("areaArray", StringUtil.parseReqParamToArrayUseTrim(area));
        return service.getPageList(map);
    }

    /**
     * 新增
     *
     * @param usXicangGoodsIssuedRecords
     * @return
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "美西仓商品发出记录对象", required = true)UsXicangGoodsIssuedRecords usXicangGoodsIssuedRecords) {
        return service.add(usXicangGoodsIssuedRecords);
    }

    /**
     * 修改
     *
     * @param usXicangGoodsIssuedRecords
     * @return
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "美西仓商品发出记录对象", required = true)UsXicangGoodsIssuedRecords usXicangGoodsIssuedRecords) {
        return service.update(usXicangGoodsIssuedRecords);
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
