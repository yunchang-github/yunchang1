package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.UsXicangGoodsIssuedRecordsMapper;
import com.weiziplus.springboot.models.UsXicangGoodsIssuedRecords;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/18 17:14
 */
@Slf4j
@Service
public class UsXicangGoodsIssuedRecordsService extends BaseService {

    @Autowired
    UsXicangGoodsIssuedRecordsMapper mapper;

    /**
     * 基准redis的key
     */
   // private final String BASE_REDIS_KEY = "pc:service:data:manualimport:UsXicangGoodsIssuedRecordsService";

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
  /*      String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize;
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }*/
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getPageList(map));
       // RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param usXicangGoodsIssuedRecords
     * @return
     */
    public ResultUtil add(UsXicangGoodsIssuedRecords usXicangGoodsIssuedRecords) {
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getFnsku())) {
            return ResultUtil.error("fnsku不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getInventorySku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getShipmentId())) {
            return ResultUtil.error("ShipmnetID不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getCaseNumber())) {
            return ResultUtil.error("箱号不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getNumber())) {
            return ResultUtil.error("数量不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getDeliveryTime())) {
            return ResultUtil.error("发货时间不能为空");
        }
        UsXicangGoodsIssuedRecords oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber = mapper.getOneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber(
                usXicangGoodsIssuedRecords.getMsku(), usXicangGoodsIssuedRecords.getFnsku(), usXicangGoodsIssuedRecords.getShipmentId(), usXicangGoodsIssuedRecords.getCaseNumber());
        if (null != oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber && null != oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber.getId()) {
            return ResultUtil.error("该记录已存在");
        }
        baseInsert(usXicangGoodsIssuedRecords);
    //    RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param usXicangGoodsIssuedRecords
     * @return
     */
    public ResultUtil update(UsXicangGoodsIssuedRecords usXicangGoodsIssuedRecords) {
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getFnsku())) {
            return ResultUtil.error("fnsku不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getInventorySku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getShipmentId())) {
            return ResultUtil.error("ShipmnetID不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getCaseNumber())) {
            return ResultUtil.error("箱号不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getNumber())) {
            return ResultUtil.error("数量不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(usXicangGoodsIssuedRecords.getDeliveryTime())) {
            return ResultUtil.error("发货时间不能为空");
        }
        UsXicangGoodsIssuedRecords oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber = mapper.getOneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber(
                usXicangGoodsIssuedRecords.getMsku(), usXicangGoodsIssuedRecords.getFnsku(), usXicangGoodsIssuedRecords.getShipmentId(), usXicangGoodsIssuedRecords.getCaseNumber());
        if (null != oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber && null != oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber.getId()
                && !oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber.getId().equals(usXicangGoodsIssuedRecords.getId())) {
            return ResultUtil.error("该记录已存在");
        }
        baseUpdate(usXicangGoodsIssuedRecords);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
        baseDeleteByClassAndIds(UsXicangGoodsIssuedRecords.class, ids);
      //  RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
            int excelTitleNum = 9;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为九列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            UsXicangGoodsIssuedRecords usXicangGoodsIssuedRecords = new UsXicangGoodsIssuedRecords();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                for (int k = 0; k < excelTitleNum; k++) {
                    Cell cell = row.getCell(k);
                    if (null == cell) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    String stringCellValue = null;
                    if (7 == k) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            stringCellValue = dateFormat.format(date);
                        } else {
                            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                            return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列格式错误，请补充完整后重试");
                        }
                    } else {
                        cell.setCellType(CellType.STRING);
                        stringCellValue = cell.getStringCellValue();
                    }
                    if (StringUtil.isBlank(stringCellValue)) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    switch (k) {
                        case 0: {
                            usXicangGoodsIssuedRecords.setMsku(stringCellValue);
                        }
                        break;
                        case 1: {
                            usXicangGoodsIssuedRecords.setFnsku(stringCellValue);
                        }
                        break;
                        case 2: {
                            usXicangGoodsIssuedRecords.setInventorySku(stringCellValue);
                        }
                        break;
                        case 3: {
                            usXicangGoodsIssuedRecords.setShipmentId(stringCellValue);
                        }
                        break;
                        case 4: {
                            usXicangGoodsIssuedRecords.setCaseNumber(stringCellValue);
                        }
                        break;
                        case 5: {
                            usXicangGoodsIssuedRecords.setNumber(stringCellValue);
                        }
                        break;
                        case 6: {
                            usXicangGoodsIssuedRecords.setShop(stringCellValue);
                        }
                        break;
                        case 7: {
                            usXicangGoodsIssuedRecords.setDeliveryTime(stringCellValue);
                        }
                        break;
                        case 8:{
                            usXicangGoodsIssuedRecords.setArea(stringCellValue);
                        }
                        default: {
                        }
                    }
                }
                UsXicangGoodsIssuedRecords oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber = mapper.getOneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber(
                        usXicangGoodsIssuedRecords.getMsku(), usXicangGoodsIssuedRecords.getFnsku(), usXicangGoodsIssuedRecords.getShipmentId()
                        , usXicangGoodsIssuedRecords.getCaseNumber());
                if (null != oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber && null != oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber.getId()) {
                    usXicangGoodsIssuedRecords.setId(oneInfoByMskuAndFuSkuAndShipmentIdAndCaseNumber.getId());
                    baseUpdate(usXicangGoodsIssuedRecords);
                } else {
                    usXicangGoodsIssuedRecords.setId(null);
                    baseInsert(usXicangGoodsIssuedRecords);
                }
            }
        } catch (IOException e) {
            log.warn("美西仓商品发出记录出错" + e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
      //  RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/美西仓商品发出记录.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
