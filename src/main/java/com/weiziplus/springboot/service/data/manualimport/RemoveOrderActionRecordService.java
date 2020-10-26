package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.RemoveOrderActionRecordMapper;
import com.weiziplus.springboot.models.RemoveOrderActionRecord;
import com.weiziplus.springboot.utils.*;
//import com.weiziplus.springboot.utils.redis.RedisUtil;
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
 * @data 2019/7/18 15:59
 */
@Slf4j
@Service
public class RemoveOrderActionRecordService extends BaseService {

    @Autowired
    RemoveOrderActionRecordMapper mapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:RemoveOrderActionRecordService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Map map) {
        int pageNum = MapUtils.getInteger(map, "pageNum", 1);
        int pageSize = MapUtils.getInteger(map,"pageSize",20);
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
     /*   String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize;
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
     * @param removeOrderActionRecord
     * @return
     */
    public ResultUtil add(RemoveOrderActionRecord removeOrderActionRecord) {
        if (StringUtil.isBlank(removeOrderActionRecord.getOrderNumber())) {
            return ResultUtil.error("订单号不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getFnsku())) {
            return ResultUtil.error("fnsku不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getReceivingPosition())) {
            return ResultUtil.error("接收仓位不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getWarehouseCode())) {
            return ResultUtil.error("分仓代号不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getNewMsku())) {
            return ResultUtil.error("新smku不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getNewFnsku())) {
            return ResultUtil.error("新fnsku不能为空");
        }
        RemoveOrderActionRecord oneInfoByTrackingNumberAndFnSku = mapper.getOneInfoByTrackingNumberAndFnSku(removeOrderActionRecord.getTrackingNumber(), removeOrderActionRecord.getFnsku());
        if (null != oneInfoByTrackingNumberAndFnSku && null != oneInfoByTrackingNumberAndFnSku.getId()) {
            return ResultUtil.error("该数据存在");
        }
        baseInsert(removeOrderActionRecord);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param removeOrderActionRecord
     * @return
     */
    public ResultUtil update(RemoveOrderActionRecord removeOrderActionRecord) {
        if (StringUtil.isBlank(removeOrderActionRecord.getOrderNumber())) {
            return ResultUtil.error("订单号不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getFnsku())) {
            return ResultUtil.error("fnsku不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getReceivingPosition())) {
            return ResultUtil.error("接收仓位不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getWarehouseCode())) {
            return ResultUtil.error("分仓代号不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getNewMsku())) {
            return ResultUtil.error("新smku不能为空");
        }
        if (StringUtil.isBlank(removeOrderActionRecord.getNewFnsku())) {
            return ResultUtil.error("新fnsku不能为空");
        }
        RemoveOrderActionRecord oneInfoByTrackingNumberAndFnSku = mapper.getOneInfoByTrackingNumberAndFnSku(removeOrderActionRecord.getTrackingNumber(), removeOrderActionRecord.getFnsku());
        if (null != oneInfoByTrackingNumberAndFnSku && null != oneInfoByTrackingNumberAndFnSku.getId() && !oneInfoByTrackingNumberAndFnSku.getId().equals(removeOrderActionRecord.getId())) {
            return ResultUtil.error("该数据存在");
        }
        baseUpdate(removeOrderActionRecord);
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
        baseDeleteByClassAndIds(RemoveOrderActionRecord.class, ids);
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
            int excelTitleNum = 15;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为十五列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            RemoveOrderActionRecord removeOrderActionRecord = new RemoveOrderActionRecord();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                for (int k = 0; k < excelTitleNum; k++) {
                    Cell cell = row.getCell(k);
                    if (null == cell) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j+1 + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    cell.setCellType(CellType.STRING);
                    String stringCellValue = cell.getStringCellValue();
                    if (StringUtil.isBlank(stringCellValue)) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j+1+  "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    switch (k) {
                        case 0: {
                            removeOrderActionRecord.setShop(stringCellValue);
                        }
                        break;
                        case 1: {
                            removeOrderActionRecord.setArea(stringCellValue);

                        }
                        break;
                        case 2: {
                            removeOrderActionRecord.setOrderNumber(stringCellValue);

                        }
                        break;
                        case 3: {
                            removeOrderActionRecord.setTrackingNumber(stringCellValue);

                        }
                        break;
                        case 4: {
                            removeOrderActionRecord.setMsku(stringCellValue);

                        }
                        break;
                        case 5: {
                            removeOrderActionRecord.setFnsku(stringCellValue);
                        }
                        break;
                        case 6: {

                            if ("退仓".equals(stringCellValue)) {
                                removeOrderActionRecord.setOrderType(0);
                            } else if ("赠品".equals(stringCellValue)) {
                                removeOrderActionRecord.setOrderType(1);
                            } else {
                                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                                return ResultUtil.error("导入失败,第" + (j+1) + "行,第" + (k + 1) + "列格式错误，退仓\\赠品，请补充完整后重试");
                            }






                        }
                        break;
                        case 7: {
                            removeOrderActionRecord.setReceivingPosition(stringCellValue);

                        }
                        break;
                        case 8: {

                            if ("发走".equals(stringCellValue)) {
                                removeOrderActionRecord.setRefundType(0);
                            } else if ("存放".equals(stringCellValue)) {
                                removeOrderActionRecord.setRefundType(1);
                            } else {
                                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                                return ResultUtil.error("导入失败,第" + (j+1) + "行,第" + (k + 1) + "列格式错误，发走\\存放，请补充完整后重试");
                            }
                        }
                        break;
                        case 9: {
                            removeOrderActionRecord.setWarehouseCode(stringCellValue);
                        }
                        break;
                        case 10: {
                            if ("是".equals(stringCellValue)) {
                                removeOrderActionRecord.setIsChange(0);
                            } else if ("否".equals(stringCellValue)) {
                                removeOrderActionRecord.setIsChange(1);
                            } else {
                                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                                return ResultUtil.error("导入失败,第" + (j+1) + "行,第" + (k + 1) + "列格式错误，是\\否，请补充完整后重试");
                            }
                        }
                        break;
                        case 11: {
                            removeOrderActionRecord.setNewMsku(stringCellValue);
                        }
                        break;
                        case 12: {
                            removeOrderActionRecord.setNewFnsku(stringCellValue);
                        }
                        break;
                        case 13: {
                            removeOrderActionRecord.setReceiveShop(stringCellValue);
                        }
                        break;
                        case 14: {
                            removeOrderActionRecord.setReceiveArea(stringCellValue);
                        }
                        break;


                        default: {
                        }
                    }
                }
                RemoveOrderActionRecord oneInfoByTrackingNumberAndFnSku = mapper.getOneInfoByTrackingNumberAndFnSku(removeOrderActionRecord.getTrackingNumber(), removeOrderActionRecord.getFnsku());
                if (null != oneInfoByTrackingNumberAndFnSku && null != oneInfoByTrackingNumberAndFnSku.getId()) {
                    removeOrderActionRecord.setId(oneInfoByTrackingNumberAndFnSku.getId());
                    baseUpdate(removeOrderActionRecord);
                } else {
                    removeOrderActionRecord.setId(null);
                    baseInsert(removeOrderActionRecord);
                }
            }
        } catch (IOException e) {
            log.warn("移除订单操作登记出错" + e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
     //   RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/移除订单操作登记.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
