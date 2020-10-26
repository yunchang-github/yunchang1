package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.OverseasWarehousePlanInformationRegistrationMapper;
import com.weiziplus.springboot.models.OverseasWarehousePlanInformationRegistration;
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
 * @data 2019/7/18 16:18
 */
@Slf4j
@Service
public class OverseasWarehousePlanInformationRegistrationService extends BaseService {

    @Autowired
    OverseasWarehousePlanInformationRegistrationMapper mapper;

    /**
     * 基准redis的key
     */
  //  private final String BASE_REDIS_KEY = "pc:service:data:manualimport:OverseasWarehousePlanInformationRegistrationService";

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
       /* String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize;
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }*/
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getPageList(map));
      //  RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param overseasWarehousePlanInformationRegistration
     * @return
     */
    public ResultUtil add(OverseasWarehousePlanInformationRegistration overseasWarehousePlanInformationRegistration) {
        if (StringUtil.isBlank(overseasWarehousePlanInformationRegistration.getPlaceBoxNumber())) {
            return ResultUtil.error("放置箱号不能为空");
        }
        if (StringUtil.isBlank(overseasWarehousePlanInformationRegistration.getBoxStatus())) {
            return ResultUtil.error("箱子状态不能为空");
        }
        if (StringUtil.isBlank(overseasWarehousePlanInformationRegistration.getShipmentId())) {
            return ResultUtil.error("shipmentId不能为空");
        }
        OverseasWarehousePlanInformationRegistration oneInfoByBoxNumber = mapper.getOneInfoByBoxNumber(overseasWarehousePlanInformationRegistration.getPlaceBoxNumber());
        if (null != oneInfoByBoxNumber && null != oneInfoByBoxNumber.getId()) {
            return ResultUtil.error("改数据已存在");
        }
        baseInsert(overseasWarehousePlanInformationRegistration);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param overseasWarehousePlanInformationRegistration
     * @return
     */
    public ResultUtil update(OverseasWarehousePlanInformationRegistration overseasWarehousePlanInformationRegistration) {
        if (StringUtil.isBlank(overseasWarehousePlanInformationRegistration.getPlaceBoxNumber())) {
            return ResultUtil.error("放置箱号不能为空");
        }
        if (StringUtil.isBlank(overseasWarehousePlanInformationRegistration.getBoxStatus())) {
            return ResultUtil.error("箱子状态不能为空");
        }
        if (StringUtil.isBlank(overseasWarehousePlanInformationRegistration.getShipmentId())) {
            return ResultUtil.error("shipmentId不能为空");
        }
        OverseasWarehousePlanInformationRegistration oneInfoByBoxNumber = mapper.getOneInfoByBoxNumber(overseasWarehousePlanInformationRegistration.getPlaceBoxNumber());
        if (null != oneInfoByBoxNumber && null != oneInfoByBoxNumber.getId() && !oneInfoByBoxNumber.getId().equals(overseasWarehousePlanInformationRegistration.getId())) {
            return ResultUtil.error("改数据已存在");
        }
        baseUpdate(overseasWarehousePlanInformationRegistration);
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
        baseDeleteByClassAndIds(OverseasWarehousePlanInformationRegistration.class, ids);
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
            int excelTitleNum = 10;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为十列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            OverseasWarehousePlanInformationRegistration overseasWarehousePlanInformationRegistration = new OverseasWarehousePlanInformationRegistration();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                for (int k = 0; k < excelTitleNum; k++) {
                    Cell cell = row.getCell(k);
                    if (null == cell) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    String stringCellValue = null;
                    if (5 == k) {

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
                            overseasWarehousePlanInformationRegistration.setPlaceBoxNumber(stringCellValue);
                        }
                        break;
                        case 1: {
                            overseasWarehousePlanInformationRegistration.setBoxStatus(stringCellValue);
                        }
                        break;
                        case 2: {
                            overseasWarehousePlanInformationRegistration.setShipmentId(stringCellValue);
                        }
                        break;
                        case 3:{
                            overseasWarehousePlanInformationRegistration.setShop(stringCellValue);
                        }
                        break;
                        case 4:{
                            overseasWarehousePlanInformationRegistration.setArea(stringCellValue);
                        }
                        break;
                        case 5:{
                            overseasWarehousePlanInformationRegistration.setFhTime(stringCellValue);
                        }
                        break;
                        case 6:{
                            overseasWarehousePlanInformationRegistration.setNumber(Integer.parseInt(stringCellValue));

                        }
                        break;
                        case 7:{
                            overseasWarehousePlanInformationRegistration.setMsku(stringCellValue);

                        }
                        break;
                        case 8:{
                            overseasWarehousePlanInformationRegistration.setFnsku(stringCellValue);

                        }
                        break;
                        case 9:{
                            overseasWarehousePlanInformationRegistration.setLocalSku(stringCellValue);

                        }
                        break;

                        default: {
                        }
                    }
                }
                OverseasWarehousePlanInformationRegistration oneInfoByBoxNumber = mapper.getOneInfoByBoxNumber(overseasWarehousePlanInformationRegistration.getPlaceBoxNumber());
                if (null != oneInfoByBoxNumber && null != oneInfoByBoxNumber.getId()) {
                    overseasWarehousePlanInformationRegistration.setId(oneInfoByBoxNumber.getId());
                    baseUpdate(overseasWarehousePlanInformationRegistration);
                } else {
                    overseasWarehousePlanInformationRegistration.setId(null);
                    baseInsert(overseasWarehousePlanInformationRegistration);
                }
            }
        } catch (IOException e) {
            log.warn("海外仓计划信息登记出错" + e.getMessage());
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
        String path = "public/template/海外仓计划信息登记.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
