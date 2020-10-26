package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.ProductsInfoMapper;
import com.weiziplus.springboot.models.ProductsInfo;
import com.weiziplus.springboot.service.shop.AreaService;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wanglongwei
 * @data 2019/7/19 8:52
 */
@Slf4j
@Service
public class ProductsInfoService extends BaseService {

    @Autowired
    ProductsInfoMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:ProductsInfoService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String shop, String area, String msku) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", shop, area, msku);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(shop, area, msku));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param productsInfo
     * @return
     */
    public ResultUtil add(ProductsInfo productsInfo) {
        if (StringUtil.isBlank(productsInfo.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getInventorySku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getCategory())) {
            return ResultUtil.error("品类不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getSize())) {
            return ResultUtil.error("尺寸不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getStyle())) {
            return ResultUtil.error("款式不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(productsInfo.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        ProductsInfo oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(productsInfo.getShop(), productsInfo.getArea(), productsInfo.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
            return ResultUtil.error("该数据已存在");
        }
        baseInsert(productsInfo);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param productsInfo
     * @return
     */
    public ResultUtil update(ProductsInfo productsInfo) {
        if (StringUtil.isBlank(productsInfo.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getInventorySku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getCategory())) {
            return ResultUtil.error("品类不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getSize())) {
            return ResultUtil.error("尺寸不能为空");
        }
        if (StringUtil.isBlank(productsInfo.getStyle())) {
            return ResultUtil.error("款式不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(productsInfo.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        ProductsInfo oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(productsInfo.getShop(), productsInfo.getArea(), productsInfo.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId() && !oneInfoByShopAndAreaAndMsku.getId().equals(productsInfo.getId())) {
            return ResultUtil.error("该数据已存在");
        }
        baseUpdate(productsInfo);
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
        baseDeleteByClassAndIds(ProductsInfo.class, ids);
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
            int excelTitleNum = 7;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为七列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            ProductsInfo productsInfo = new ProductsInfo();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell shopCell = row.getCell(0);
                Cell areaCell = row.getCell(1);
                Cell mskuCell = row.getCell(2);
                Cell kcSkuCell = row.getCell(3);
                Cell categoryCell = row.getCell(4);
                Cell sizeCell = row.getCell(5);
                Cell styleCell = row.getCell(6);
                if (null == shopCell || null == areaCell || null == mskuCell || null == kcSkuCell || null == categoryCell
                        || null == sizeCell || null == styleCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                shopCell.setCellType(CellType.STRING);
                areaCell.setCellType(CellType.STRING);
                mskuCell.setCellType(CellType.STRING);
                kcSkuCell.setCellType(CellType.STRING);
                categoryCell.setCellType(CellType.STRING);
                sizeCell.setCellType(CellType.STRING);
                styleCell.setCellType(CellType.STRING);
                String shopStr = shopCell.getStringCellValue();
                String areaStr = areaCell.getStringCellValue();
                String mskuStr = mskuCell.getStringCellValue();
                String kcSkuStr = kcSkuCell.getStringCellValue();
                String categoryStr = categoryCell.getStringCellValue();
                String sizeStr = sizeCell.getStringCellValue();
                String styleStr = styleCell.getStringCellValue();
                if (StringUtil.isBlank(shopStr) || StringUtil.isBlank(areaStr) || StringUtil.isBlank(mskuStr) || StringUtil.isBlank(kcSkuStr)
                        || StringUtil.isBlank(categoryStr) || StringUtil.isBlank(sizeStr) || StringUtil.isBlank(styleStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                if (!areaService.getMwsCountryCodeList().contains(areaStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行国家代码错误");
                }
                productsInfo.setShop(shopStr);
                productsInfo.setArea(areaStr);
                productsInfo.setMsku(mskuStr);
                productsInfo.setInventorySku(kcSkuStr);
                productsInfo.setCategory(categoryStr);
                productsInfo.setSize(sizeStr);
                productsInfo.setStyle(styleStr);
                ProductsInfo oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(shopStr, areaStr, mskuStr);
                if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
                    productsInfo.setId(oneInfoByShopAndAreaAndMsku.getId());
                    baseUpdate(productsInfo);
                } else {
                    productsInfo.setId(null);
                    baseInsert(productsInfo);
                }
            }
        } catch (IOException e) {
            log.warn("商品信息表出错" + e.getMessage());
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
        String path = "public/template/商品信息表.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
