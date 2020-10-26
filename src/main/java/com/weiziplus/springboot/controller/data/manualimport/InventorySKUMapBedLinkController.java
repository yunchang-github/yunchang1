package com.weiziplus.springboot.controller.data.manualimport;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.weiziplus.springboot.models.InventorySkuMapBedLink;
import com.weiziplus.springboot.service.InventorySKUMapBedLink.InventorySKUMapBedLinkService;
import com.weiziplus.springboot.utils.ResultUtil;

@Api(value="InventorySKUMapBedLinkController",tags={"库存SKU图床链接"})
@RestController
@RequestMapping("/pc/data/manualImport/inventorySKUMapBedLink")
public class InventorySKUMapBedLinkController {

	@Autowired
	private InventorySKUMapBedLinkService service;

    @ApiOperation(value = "查询列表", notes = "查询列表")
	@GetMapping("/getPageList")
	 public ResultUtil getPageList(
                @ApiParam(value = "当前页", required = true) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                @ApiParam(value = "每页数", required = true)@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                @ApiParam(value = "库存", required = false)  @RequestParam(value = "inventorySku", required = false) String inventorySku,
                @ApiParam(value = "图片链接", required = false)  @RequestParam(value = "link", required = false) String link
	           ) {

		return service.getPageList(pageNum,pageSize,inventorySku);
	}

    @ApiOperation(value = "根据sku获取链接", notes = "根据sku获取链接")
    @GetMapping("/getlinkBySku")
    public ResultUtil getlinkBySku(
            @ApiParam(value = "sku", required = true)String sku
    ) {
        return service.getLink(sku);
    }


	   /**
     * 新增
     *
     * @param inventorySkuMapBedLink
     * @return
     */
	   @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultUtil add(@ApiParam(value = "库存SKU图床链接", required = true)InventorySkuMapBedLink inventorySkuMapBedLink) {
        return service.add(inventorySkuMapBedLink);
    }

    /**
     * 修改
     *
     * @param inventorySkuMapBedLink
     * @return
     */
    @PostMapping("/update")
    public ResultUtil update(@ApiParam(value = "库存SKU图床链接", required = true)InventorySkuMapBedLink inventorySkuMapBedLink) {
        return service.update(inventorySkuMapBedLink);
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
