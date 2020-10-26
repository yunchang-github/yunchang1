package com.weiziplus.springboot.service.InventorySKUMapBedLink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.InventorySKUMapBedLinkMapper;
import com.weiziplus.springboot.models.HeadFreight;
import com.weiziplus.springboot.models.InventorySkuMapBedLink;
import com.weiziplus.springboot.utils.FileUtil;
import com.weiziplus.springboot.utils.PageUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.ValidateUtil;
import com.weiziplus.springboot.utils.redis.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventorySKUMapBedLinkService extends  BaseService{

	@Autowired
	private InventorySKUMapBedLinkMapper mapper;


	  /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:InventorySKUMapBedLinkService";

	public ResultUtil getPageList(Integer pageNum, Integer pageSize, String inventorySku) {
		if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", pageNum, pageSize);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(
                mapper.getList(inventorySku));

        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
	}


	   /**
     * 新增
     *
     * @param inventorySkuMapBedLink
     * @return
     */
    public ResultUtil add(InventorySkuMapBedLink inventorySkuMapBedLink) {

        if (StringUtil.isBlank(inventorySkuMapBedLink.getInventorySku())) {
            return ResultUtil.error("库存SKU不能为空");
        }
        if (StringUtil.isBlank(inventorySkuMapBedLink.getLink())) {
            return ResultUtil.error("图床链接不能为空");
        }

        InventorySkuMapBedLink oneInfoByInventorySkuMapBedLink = mapper.getOneInfoByCondition(inventorySkuMapBedLink.getInventorySku());
        if (null != oneInfoByInventorySkuMapBedLink && null != oneInfoByInventorySkuMapBedLink.getId()) {
            return ResultUtil.error("当前记录已存在");
        }
        baseInsert(inventorySkuMapBedLink);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param inventorySkuMapBedLink
     * @return
     */
    public ResultUtil update(InventorySkuMapBedLink inventorySkuMapBedLink) {
    	 if (StringUtil.isBlank(inventorySkuMapBedLink.getInventorySku())) {
             return ResultUtil.error("库存SKU不能为空");
         }

         if (StringUtil.isBlank(inventorySkuMapBedLink.getLink())) {
             return ResultUtil.error("图床链接不能为空");
         }

         InventorySkuMapBedLink oneInfoByInventorySkuMapBedLink = mapper.getOneInfoByCondition(inventorySkuMapBedLink.getInventorySku());
        if (null != oneInfoByInventorySkuMapBedLink && null != oneInfoByInventorySkuMapBedLink.getId() && !oneInfoByInventorySkuMapBedLink.getId().equals(inventorySkuMapBedLink.getId())) {
            return ResultUtil.error("当前记录已存在");
        }



        baseUpdate(inventorySkuMapBedLink);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    public ResultUtil delete(Long[] ids) {
        if (null == ids) {
            return ResultUtil.error("ids为空");
        }
        baseDeleteByClassAndIds(InventorySkuMapBedLink.class, ids);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil upload(MultipartFile file) {
        //判断文件是否为空
        if (null == file || 0 >= file.getSize()) {
            return ResultUtil.error("文件不存在");
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        if (ValidateUtil.notExcel(fileName)) {
            return ResultUtil.error("文件格式错误");
        }
        Workbook workbook;
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            if (ValidateUtil.isExcel2007(fileName)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                workbook = new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
            }
            //默认只有一个sheet
            Sheet sheet = workbook.getSheetAt(0);
            if (null == sheet || null == sheet.getRow(0)) {
                return ResultUtil.error("文件错误，请检查文件");
            }
            //excel标题字段数
            int excelTitleNum = 2;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为二列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
            	InventorySkuMapBedLink inventorySkuMapBedLink = new InventorySkuMapBedLink();
                Row row = sheet.getRow(j);
                Cell inventorySkuCell = row.getCell(0);
                Cell linkCell = row.getCell(1);

                if (null == inventorySkuCell || null == linkCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                inventorySkuCell.setCellType(CellType.STRING);
                linkCell.setCellType(CellType.STRING);
                String inventorySkuStr = inventorySkuCell.getStringCellValue();
                String linkStr = linkCell.getStringCellValue();
                if (StringUtil.isBlank(inventorySkuStr) || StringUtil.isBlank(linkStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }

                inventorySkuMapBedLink.setInventorySku(inventorySkuStr);
                inventorySkuMapBedLink.setLink(linkStr);


                InventorySkuMapBedLink oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByCondition(inventorySkuStr);
                if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
                    inventorySkuMapBedLink.setId(oneInfoByShopAndAreaAndMsku.getId());
                    baseUpdate(inventorySkuMapBedLink);
                } else {
                	oneInfoByShopAndAreaAndMsku.setId(null);
                    insertList.add(oneInfoByShopAndAreaAndMsku);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("库存SKU图床链接" + e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/库存SKU图床链接.xlsx";
        FileUtil.downResourceFile(response, path);
    }


    public ResultUtil getLink(String sku) {
        String link = "";
        if(!StringUtils.isBlank(sku)){
            link =  mapper.getLinkBySku(sku);
        }

        return ResultUtil.success("获取成功",link);

    }
}
