package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.PurchasePriceMapper;
import com.weiziplus.springboot.models.PurchasePrice;
import com.weiziplus.springboot.service.shop.AreaService;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/17 10:32
 */
@Slf4j
@Service
public class PurchasePriceService extends BaseService {

    @Autowired
    PurchasePriceMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:PurchasePriceService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Map map) {
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList",map.hashCode());
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(map));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param purchasePrice
     * @return
     */
    public ResultUtil add(PurchasePrice purchasePrice) {
        if (StringUtil.isBlank(purchasePrice.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getKcsku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getPurchasePrice())) {
            return ResultUtil.error("采购价不能为空");
        }
        if (0 != purchasePrice.getIsEstimate() && 1 != purchasePrice.getIsEstimate()) {
            return ResultUtil.error("是否为预估错误");
        }
        if (!areaService.getMwsCountryCodeList().contains(purchasePrice.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        PurchasePrice oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(purchasePrice.getShop(), purchasePrice.getArea(), purchasePrice.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
            return ResultUtil.error("当前记录已存在");
        }
        baseInsert(purchasePrice);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param purchasePrice
     * @return
     */
    public ResultUtil update(PurchasePrice purchasePrice) {
        if (StringUtil.isBlank(purchasePrice.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getKcsku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(purchasePrice.getPurchasePrice())) {
            return ResultUtil.error("采购价不能为空");
        }
        if (0 != purchasePrice.getIsEstimate() && 1 != purchasePrice.getIsEstimate()) {
            return ResultUtil.error("是否为预估错误");
        }
        if (!areaService.getMwsCountryCodeList().contains(purchasePrice.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        PurchasePrice oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(purchasePrice.getShop(), purchasePrice.getArea(), purchasePrice.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId() && !oneInfoByShopAndAreaAndMsku.getId().equals(purchasePrice.getId())) {
            return ResultUtil.error("当前记录已存在");
        }
        baseUpdate(purchasePrice);
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
        baseDeleteByClassAndIds(PurchasePrice.class, ids);

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
            int excelTitleNum = 6;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为六列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            PurchasePrice purchasePrice = new PurchasePrice();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell mskuCell = row.getCell(0);
                Cell shopCell = row.getCell(1);
                Cell areaCell = row.getCell(2);
                Cell kcSkuCell = row.getCell(3);
                Cell priceCell = row.getCell(4);
                Cell estimateCell = row.getCell(5);
                if (null == mskuCell || null == shopCell || null == areaCell || null == kcSkuCell || null == priceCell || null == estimateCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                mskuCell.setCellType(CellType.STRING);
                shopCell.setCellType(CellType.STRING);
                areaCell.setCellType(CellType.STRING);
                kcSkuCell.setCellType(CellType.STRING);
                priceCell.setCellType(CellType.STRING);
                estimateCell.setCellType(CellType.STRING);
                String mskuStr = mskuCell.getStringCellValue();
                String shopStr = shopCell.getStringCellValue();
                String areaStr = areaCell.getStringCellValue();
                String kcSkuStr = kcSkuCell.getStringCellValue();
                String priceStr = priceCell.getStringCellValue();
                String estimateStr = estimateCell.getStringCellValue();
                if (StringUtil.isBlank(mskuStr) || StringUtil.isBlank(shopStr) || StringUtil.isBlank(areaStr)
                        || StringUtil.isBlank(kcSkuStr) || StringUtil.isBlank(priceStr) || StringUtil.isBlank(estimateStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                if (!areaService.getMwsCountryCodeList().contains(areaStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行国家代码错误");
                }
                purchasePrice.setMsku(mskuStr);
                purchasePrice.setShop(shopStr);
                purchasePrice.setArea(areaStr);
                purchasePrice.setKcsku(kcSkuStr);
                purchasePrice.setPurchasePrice(priceStr);
                if ("是".equals(estimateStr)) {
                    purchasePrice.setIsEstimate(0);
                } else if ("否".equals(estimateStr)) {
                    purchasePrice.setIsEstimate(1);
                } else {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,预估为：是或否");
                }
                PurchasePrice oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(shopStr, areaStr, mskuStr);
                if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
                    purchasePrice.setId(oneInfoByShopAndAreaAndMsku.getId());
                    baseUpdate(purchasePrice);
                } else {
                    purchasePrice.setId(null);
                    baseInsert(purchasePrice);
                }
            }
        } catch (IOException e) {
            log.warn("采购价出错" + e.getMessage());
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
        String path = "public/template/采购价.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
