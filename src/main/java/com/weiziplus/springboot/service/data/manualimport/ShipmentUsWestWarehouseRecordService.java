package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.ShipmentUsWestWarehouseRecordMapper;
import com.weiziplus.springboot.models.ShipmentUsWestWarehouseRecord;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/18 16:51
 */
@Slf4j
@Service
public class ShipmentUsWestWarehouseRecordService extends BaseService {

    @Autowired
    ShipmentUsWestWarehouseRecordMapper mapper;

    /**
     * 基准redis的key
     */
    //private final String BASE_REDIS_KEY = "pc:service:data:manualimport:ShipmentUsWestWarehouseRecordService";

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
        //RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param shipmentUsWestWarehouseRecord
     * @return
     */
    public ResultUtil add(ShipmentUsWestWarehouseRecord shipmentUsWestWarehouseRecord) {
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getFnsku())) {
            return ResultUtil.error("fnsku不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getInstockSku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getNumber())) {
            return ResultUtil.error("数量不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getCaseNumber())) {
            return ResultUtil.error("箱号不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getOutboundTime())) {
            return ResultUtil.error("出库时间不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getExpectedReceiveTime())) {
            return ResultUtil.error("海外仓预计接收时间不能为空");
        }
        ShipmentUsWestWarehouseRecord oneInfoByMskuAndFnSkuAndCaseNumber = mapper.getOneInfoByMskuAndFnSkuAndCaseNumber(
                shipmentUsWestWarehouseRecord.getMsku(), shipmentUsWestWarehouseRecord.getFnsku(), shipmentUsWestWarehouseRecord.getCaseNumber());
        if (null != oneInfoByMskuAndFnSkuAndCaseNumber && null != oneInfoByMskuAndFnSkuAndCaseNumber.getId()) {
            return ResultUtil.error("该数据已存在");
        }
        baseInsert(shipmentUsWestWarehouseRecord);
      //  RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param shipmentUsWestWarehouseRecord
     * @return
     */
    public ResultUtil update(ShipmentUsWestWarehouseRecord shipmentUsWestWarehouseRecord) {
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getFnsku())) {
            return ResultUtil.error("fnsku不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getInstockSku())) {
            return ResultUtil.error("库存sku不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getNumber())) {
            return ResultUtil.error("数量不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getCaseNumber())) {
            return ResultUtil.error("箱号不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getOutboundTime())) {
            return ResultUtil.error("出库时间不能为空");
        }
        if (StringUtil.isBlank(shipmentUsWestWarehouseRecord.getExpectedReceiveTime())) {
            return ResultUtil.error("海外仓预计接收时间不能为空");
        }
        ShipmentUsWestWarehouseRecord oneInfoByMskuAndFnSkuAndCaseNumber = mapper.getOneInfoByMskuAndFnSkuAndCaseNumber(
                shipmentUsWestWarehouseRecord.getMsku(), shipmentUsWestWarehouseRecord.getFnsku(), shipmentUsWestWarehouseRecord.getCaseNumber());
        if (null != oneInfoByMskuAndFnSkuAndCaseNumber && null != oneInfoByMskuAndFnSkuAndCaseNumber.getId() && !oneInfoByMskuAndFnSkuAndCaseNumber.getId().equals(shipmentUsWestWarehouseRecord.getId())) {
            return ResultUtil.error("该数据已存在");
        }
        baseUpdate(shipmentUsWestWarehouseRecord);
      //  RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
        baseDeleteByClassAndIds(ShipmentUsWestWarehouseRecord.class, ids);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
            int excelTitleNum = 12;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为十二列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            ShipmentUsWestWarehouseRecord shipmentUsWestWarehouseRecord = new ShipmentUsWestWarehouseRecord();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                    Cell cell = row.getCell(k);
                    if (null == cell) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    String stringCellValue = null;
                    if (5 == k || 6 == k) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            stringCellValue = dateFormat.format(date);
                        } else {
                            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                            return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列格式错误，请补充完整后重试");
                        }
                    }else if(11==k){
                            if(ObjectUtils.isEmpty(cell)){
                                cell.setCellType(CellType.STRING);
                            }else{
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    stringCellValue = dateFormat.format(date);
                                } else {
                                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                                    return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列格式错误，请补充完整后重试");
                                }
                            }
                    }else {
                        cell.setCellType(CellType.STRING);
                        stringCellValue = cell.getStringCellValue();
                    }
                    if (StringUtil.isBlank(stringCellValue)) {

                        if(k==10||k==11){

                        }else{
                            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                            return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                        }

                    }
                    switch (k) {
                        case 0: {
                            shipmentUsWestWarehouseRecord.setMsku(stringCellValue);
                        }
                        break;
                        case 1: {
                            shipmentUsWestWarehouseRecord.setFnsku(stringCellValue);
                        }
                        break;
                        case 2: {
                            shipmentUsWestWarehouseRecord.setInstockSku(stringCellValue);
                        }
                        break;
                        case 3: {
                            shipmentUsWestWarehouseRecord.setNumber(stringCellValue);
                        }
                        break;
                        case 4: {
                            shipmentUsWestWarehouseRecord.setCaseNumber(stringCellValue);
                        }
                        break;
                        case 5: {
                            shipmentUsWestWarehouseRecord.setOutboundTime(stringCellValue);
                        }
                        break;
                        case 6: {
                            shipmentUsWestWarehouseRecord.setExpectedReceiveTime(stringCellValue);
                        }
                        break;
                        case 7:{
                            shipmentUsWestWarehouseRecord.setShipmentId(stringCellValue);
                        }
                        break;
                        case 8:{
                            shipmentUsWestWarehouseRecord.setShop(stringCellValue);
                        }
                        break;
                        case 9:{
                            shipmentUsWestWarehouseRecord.setArea(stringCellValue);
                        }
                        break;
                        case 10:{
                            shipmentUsWestWarehouseRecord.setReceiveNumber(StringUtils.isNotBlank(stringCellValue)?Integer.valueOf(stringCellValue):null);
                        }
                        break;
                        case 11:{
                            shipmentUsWestWarehouseRecord.setReceiveTime(stringCellValue);
                        }
                        break;
                        default: {
                        }
                    }
                }
                ShipmentUsWestWarehouseRecord oneInfoByMskuAndFnSkuAndCaseNumber = mapper.getOneInfoByCondition(
                        shipmentUsWestWarehouseRecord.getMsku(), shipmentUsWestWarehouseRecord.getFnsku(), shipmentUsWestWarehouseRecord.getCaseNumber(),shipmentUsWestWarehouseRecord.getShipmentId(),shipmentUsWestWarehouseRecord.getShop(),shipmentUsWestWarehouseRecord.getArea());
                if (null != oneInfoByMskuAndFnSkuAndCaseNumber && null != oneInfoByMskuAndFnSkuAndCaseNumber.getId()) {
                    shipmentUsWestWarehouseRecord.setId(oneInfoByMskuAndFnSkuAndCaseNumber.getId());
                    baseUpdate(shipmentUsWestWarehouseRecord);
                } else {
                    shipmentUsWestWarehouseRecord.setId(null);
                    baseInsert(shipmentUsWestWarehouseRecord);
                }
            }
        } catch (IOException e) {
            log.warn("发货至美西仓记录出错" + e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/发货至美西仓记录.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
