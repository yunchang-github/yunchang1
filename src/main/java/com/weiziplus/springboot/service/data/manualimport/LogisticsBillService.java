package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.LogisticsBillMapper;
import com.weiziplus.springboot.models.LogisticsBill;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/7/17 17:08
 */
@Slf4j
@Service
public class LogisticsBillService extends BaseService {

    @Autowired
    LogisticsBillMapper mapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:LogisticsBillService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String number, String carrier) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", pageNum, pageSize);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(number, carrier));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param logisticsBill
     * @return
     */
    public ResultUtil add(LogisticsBill logisticsBill) {
        if (StringUtil.isBlank(logisticsBill.getBillingLogisticsNumber())) {
            return ResultUtil.error("单号不能为空");
        }
        if (StringUtil.isBlank(logisticsBill.getFreight())) {
            return ResultUtil.error("运费不能为空");
        }
        if (StringUtil.isBlank(logisticsBill.getCarrier())) {
            return ResultUtil.error("承运商不能为空");
        }
        LogisticsBill oneInfoByNumber = mapper.getOneInfoByNumber(logisticsBill.getBillingLogisticsNumber());
        if (null != oneInfoByNumber && null != oneInfoByNumber.getId()) {
            return ResultUtil.error("该单号数据已存在");
        }
        baseInsert(logisticsBill);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param logisticsBill
     * @return
     */
    public ResultUtil update(LogisticsBill logisticsBill) {
        if (StringUtil.isBlank(logisticsBill.getBillingLogisticsNumber())) {
            return ResultUtil.error("单号不能为空");
        }
        if (StringUtil.isBlank(logisticsBill.getFreight())) {
            return ResultUtil.error("运费不能为空");
        }
        if (StringUtil.isBlank(logisticsBill.getCarrier())) {
            return ResultUtil.error("承运商不能为空");
        }
        LogisticsBill oneInfoByNumber = mapper.getOneInfoByNumber(logisticsBill.getBillingLogisticsNumber());
        if (null != oneInfoByNumber && null != oneInfoByNumber.getId() && !oneInfoByNumber.getId().equals(logisticsBill.getId())) {
            return ResultUtil.error("该单号数据已存在");
        }
        baseUpdate(logisticsBill);
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
        baseDeleteByClassAndIds(LogisticsBill.class, ids);
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
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                LogisticsBill logisticsBill = new LogisticsBill();
                Row row = sheet.getRow(j);
                Cell numberCell = row.getCell(0);
                Cell freightCell = row.getCell(1);
                Cell carrierCell = row.getCell(2);
                if (null == numberCell || null == freightCell || null == carrierCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                numberCell.setCellType(CellType.STRING);
                freightCell.setCellType(CellType.STRING);
                carrierCell.setCellType(CellType.STRING);
                String numberValue = numberCell.getStringCellValue();
                String freightValue = freightCell.getStringCellValue();
                String carrierValue = carrierCell.getStringCellValue();
                if (StringUtil.isBlank(numberValue) || StringUtil.isBlank(freightValue) || StringUtil.isBlank(carrierValue)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                logisticsBill.setBillingLogisticsNumber(numberValue);
                logisticsBill.setFreight(freightValue);
                logisticsBill.setCarrier(carrierValue);
                LogisticsBill oneInfoByNumber = mapper.getOneInfoByNumber(numberValue);
                if (null != oneInfoByNumber && null != oneInfoByNumber.getId()) {
                    logisticsBill.setId(oneInfoByNumber.getId());
                    baseUpdate(logisticsBill);
                } else {
                    logisticsBill.setId(null);
                    insertList.add(logisticsBill);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("物流账单出错" + e.getMessage());
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
        String path = "public/template/物流账单.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
