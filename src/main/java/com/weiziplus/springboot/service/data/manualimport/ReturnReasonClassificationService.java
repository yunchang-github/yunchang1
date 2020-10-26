package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.ReturnReasonClassificationMapper;
import com.weiziplus.springboot.models.ReturnReasonClassification;
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
 * @data 2019/7/17 10:35
 */
@Slf4j
@Service
public class ReturnReasonClassificationService extends BaseService {

    @Autowired
    ReturnReasonClassificationMapper mapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:ReturnReasonClassificationService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize;
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(baseFindAllByClass(ReturnReasonClassification.class));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param returnReasonClassification
     * @return
     */
    public ResultUtil add(ReturnReasonClassification returnReasonClassification) {
        if (StringUtil.isBlank(returnReasonClassification.getClassification())) {
            return ResultUtil.error("分类不能为空");
        }
        if (StringUtil.isBlank(returnReasonClassification.getCnReason())) {
            return ResultUtil.error("中文原因不能为空");
        }
        if (StringUtil.isBlank(returnReasonClassification.getEgReason())) {
            return ResultUtil.error("英文原因不能为空");
        }
        ReturnReasonClassification oneInfoByZhReasonAndEgReason = mapper.getOneInfoByZhReasonAndEgReason(returnReasonClassification.getCnReason(), returnReasonClassification.getEgReason());
        if (null != oneInfoByZhReasonAndEgReason && null != oneInfoByZhReasonAndEgReason.getId()) {
            return ResultUtil.error("该记录已存在");
        }
        baseInsert(returnReasonClassification);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param returnReasonClassification
     * @return
     */
    public ResultUtil update(ReturnReasonClassification returnReasonClassification) {
        if (StringUtil.isBlank(returnReasonClassification.getClassification())) {
            return ResultUtil.error("分类不能为空");
        }
        if (StringUtil.isBlank(returnReasonClassification.getCnReason())) {
            return ResultUtil.error("中文原因不能为空");
        }
        if (StringUtil.isBlank(returnReasonClassification.getEgReason())) {
            return ResultUtil.error("英文原因不能为空");
        }
        ReturnReasonClassification oneInfoByZhReasonAndEgReason = mapper.getOneInfoByZhReasonAndEgReason(returnReasonClassification.getCnReason(), returnReasonClassification.getEgReason());
        if (null != oneInfoByZhReasonAndEgReason && null != oneInfoByZhReasonAndEgReason.getId()
                && !oneInfoByZhReasonAndEgReason.getId().equals(returnReasonClassification.getId())) {
            return ResultUtil.error("该记录已存在");
        }
        baseUpdate(returnReasonClassification);
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
        baseDeleteByClassAndIds(ReturnReasonClassification.class, ids);
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
            int excelTitleNum = 3;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为三列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            ReturnReasonClassification returnReasonClassification = new ReturnReasonClassification();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell typeCell = row.getCell(0);
                Cell zhCell = row.getCell(1);
                Cell usCell = row.getCell(2);
                if (null == typeCell || null == zhCell || null == usCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                typeCell.setCellType(CellType.STRING);
                zhCell.setCellType(CellType.STRING);
                usCell.setCellType(CellType.STRING);
                String typeStr = typeCell.getStringCellValue();
                String zhStr = zhCell.getStringCellValue();
                String usStr = usCell.getStringCellValue();
                if (StringUtil.isBlank(typeStr) || StringUtil.isBlank(zhStr) || StringUtil.isBlank(usStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                returnReasonClassification.setClassification(typeStr);
                returnReasonClassification.setCnReason(zhStr);
                returnReasonClassification.setEgReason(usStr);
                ReturnReasonClassification oneInfoByZhReasonAndEgReason = mapper.getOneInfoByZhReasonAndEgReason(zhStr, usStr);
                if (null != oneInfoByZhReasonAndEgReason && null != oneInfoByZhReasonAndEgReason.getId()) {
                    returnReasonClassification.setId(oneInfoByZhReasonAndEgReason.getId());
                    baseUpdate(returnReasonClassification);
                } else {
                    returnReasonClassification.setId(null);
                    baseInsert(returnReasonClassification);
                }
            }
        } catch (IOException e) {
            log.warn("退货原因分类出错" + e.getMessage());
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
        String path = "public/template/退货原因分类.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
