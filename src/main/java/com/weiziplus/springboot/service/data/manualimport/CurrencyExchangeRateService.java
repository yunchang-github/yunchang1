package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.CurrencyExchangeRateMapper;
import com.weiziplus.springboot.models.CurrencyExchangeRate;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author wanglongwei
 * @data 2019/7/19 9:34
 */
@Slf4j
@Service
public class CurrencyExchangeRateService extends BaseService {

    @Autowired
    CurrencyExchangeRateMapper mapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:CurrencyExchangeRateService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", pageNum, pageSize);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(baseFindAllByClass(CurrencyExchangeRate.class));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param currencyExchangeRate
     * @return
     */
    public ResultUtil add(CurrencyExchangeRate currencyExchangeRate) {
        if (StringUtil.isBlank(currencyExchangeRate.getCurrency())) {
            return ResultUtil.error("币种为空");
        }
        if (StringUtil.isBlank(currencyExchangeRate.getExchangeRate())) {
            return ResultUtil.error("汇率为空");
        }
        if (StringUtil.isBlank(currencyExchangeRate.getExchangeRateStartTime())) {
            return ResultUtil.error("启用时间为空");
        }
        String regex = "^(19|20)(\\d{2})-[01][0-9]-[0123][0-9]$";
        if (!Pattern.matches(regex, currencyExchangeRate.getExchangeRateStartTime())) {
            return ResultUtil.error("启用时间格式错误");
        }
        CurrencyExchangeRate oneInfoByCurrency = mapper.getOneInfoByCurrency(currencyExchangeRate.getCurrency());
        if (null != oneInfoByCurrency && null != oneInfoByCurrency.getId()) {
            return ResultUtil.error("该币种已存在");
        }
        baseInsert(currencyExchangeRate);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param currencyExchangeRate
     * @return
     */
    public ResultUtil update(CurrencyExchangeRate currencyExchangeRate) {
        if (StringUtil.isBlank(currencyExchangeRate.getCurrency())) {
            return ResultUtil.error("币种为空");
        }
        if (StringUtil.isBlank(currencyExchangeRate.getExchangeRate())) {
            return ResultUtil.error("汇率为空");
        }
        if (StringUtil.isBlank(currencyExchangeRate.getExchangeRateStartTime())) {
            return ResultUtil.error("启用时间为空");
        }
        String regex = "^(19|20)(\\d{2})-[01][0-9]-[0123][0-9]$";
        if (!Pattern.matches(regex, currencyExchangeRate.getExchangeRateStartTime())) {
            return ResultUtil.error("启用时间格式错误");
        }
        CurrencyExchangeRate oneInfoByCurrency = mapper.getOneInfoByCurrency(currencyExchangeRate.getCurrency());
        if (null != oneInfoByCurrency && null != oneInfoByCurrency.getId() && !oneInfoByCurrency.getId().equals(currencyExchangeRate.getId())) {
            return ResultUtil.error("该币种已存在");
        }
        baseUpdate(currencyExchangeRate);
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
        baseDeleteByClassAndIds(CurrencyExchangeRate.class, ids);
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
                CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
                Row row = sheet.getRow(j);
                Cell currencyCell = row.getCell(0);
                Cell rateCell = row.getCell(1);
                Cell timeCell = row.getCell(2);
                if (null == currencyCell || null == rateCell || null == timeCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                currencyCell.setCellType(CellType.STRING);
                rateCell.setCellType(CellType.STRING);
                String currencyStr = currencyCell.getStringCellValue();
                String rateStr = rateCell.getStringCellValue();
                String timeStr = null;
                if (DateUtil.isCellDateFormatted(timeCell)) {
                    Date date = timeCell.getDateCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    timeStr = dateFormat.format(date);
                }
                if (StringUtil.isBlank(currencyStr) || StringUtil.isBlank(rateStr) || StringUtil.isBlank(timeStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                currencyExchangeRate.setCurrency(currencyStr);
                currencyExchangeRate.setExchangeRate(rateStr);
                currencyExchangeRate.setExchangeRateStartTime(timeStr);
                CurrencyExchangeRate oneInfoByCurrency = mapper.getOneInfoByCurrency(currencyStr);
                if (null != oneInfoByCurrency && null != oneInfoByCurrency.getId()) {
                    currencyExchangeRate.setId(oneInfoByCurrency.getId());
                    baseUpdate(currencyExchangeRate);
                } else {
                    currencyExchangeRate.setId(null);
                    insertList.add(currencyExchangeRate);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("货币汇率出错" + e.getMessage());
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
        String path = "public/template/货币汇率.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
