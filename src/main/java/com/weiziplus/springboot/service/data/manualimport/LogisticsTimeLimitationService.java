package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.LogisticsTimeLimitationMapper;
import com.weiziplus.springboot.models.LogisticsTimeLimitation;
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
 * @data 2019/7/17 10:38
 */
@Slf4j
@Service
public class LogisticsTimeLimitationService extends BaseService {

    @Autowired
    LogisticsTimeLimitationMapper mapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:LogisticsTimeLimitationService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String code, String mode, String type) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", code, mode, type);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(code, mode, type));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param logisticsTimeLimitation
     * @return
     */
    public ResultUtil add(LogisticsTimeLimitation logisticsTimeLimitation) {
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsModeCode())) {
            return ResultUtil.error("物流方式代码不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsMode())) {
            return ResultUtil.error("物流方式不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsTimeLimitation())) {
            return ResultUtil.error("物流时效不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsTimeLimitation())) {
            return ResultUtil.error("入仓检出时间不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsType())) {
            return ResultUtil.error("物流类型不能为空");
        }
        LogisticsTimeLimitation oneInfoByMode = mapper.getOneInfoByMode(logisticsTimeLimitation.getLogisticsMode());
        if (null != oneInfoByMode && null != oneInfoByMode.getId()) {
            return ResultUtil.error("该记录已存在");
        }
        baseInsert(logisticsTimeLimitation);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param logisticsTimeLimitation
     * @return
     */
    public ResultUtil update(LogisticsTimeLimitation logisticsTimeLimitation) {
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsModeCode())) {
            return ResultUtil.error("物流方式代码不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsMode())) {
            return ResultUtil.error("物流方式不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsTimeLimitation())) {
            return ResultUtil.error("物流时效不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsTimeLimitation())) {
            return ResultUtil.error("入仓检出时间不能为空");
        }
        if (StringUtil.isBlank(logisticsTimeLimitation.getLogisticsType())) {
            return ResultUtil.error("物流类型不能为空");
        }
        LogisticsTimeLimitation oneInfoByMode = mapper.getOneInfoByMode(logisticsTimeLimitation.getLogisticsMode());
        if (null != oneInfoByMode && null != oneInfoByMode.getId() && !oneInfoByMode.getId().equals(logisticsTimeLimitation.getId())) {
            return ResultUtil.error("该记录已存在");
        }
        baseUpdate(logisticsTimeLimitation);
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
        baseDeleteByClassAndIds(LogisticsTimeLimitation.class, ids);
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
            int excelTitleNum = 5;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为五列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                LogisticsTimeLimitation logisticsTimeLimitation = new LogisticsTimeLimitation();
                Row row = sheet.getRow(j);
                Cell modeCodeCell = row.getCell(0);
                Cell modeCell = row.getCell(1);
                Cell timeCell = row.getCell(2);
                Cell checkTimeCell = row.getCell(3);
                Cell typeCell = row.getCell(4);
                if (null == modeCodeCell || null == modeCell || null == timeCell || null == checkTimeCell || null == typeCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                modeCodeCell.setCellType(CellType.STRING);
                modeCell.setCellType(CellType.STRING);
                timeCell.setCellType(CellType.STRING);
                checkTimeCell.setCellType(CellType.STRING);
                typeCell.setCellType(CellType.STRING);
                String modeCodeStr = modeCodeCell.getStringCellValue();
                String modeStr = modeCell.getStringCellValue();
                String timeStr = timeCell.getStringCellValue();
                String checkTimeStr = checkTimeCell.getStringCellValue();
                String typeStr = typeCell.getStringCellValue();
                if (StringUtil.isBlank(modeCodeStr) || StringUtil.isBlank(modeStr) || StringUtil.isBlank(timeStr)
                        || StringUtil.isBlank(checkTimeStr) || StringUtil.isBlank(typeStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                logisticsTimeLimitation.setLogisticsModeCode(modeCodeStr);
                logisticsTimeLimitation.setLogisticsMode(modeStr);
                logisticsTimeLimitation.setLogisticsTimeLimitation(timeStr);
                logisticsTimeLimitation.setCheckInTime(checkTimeStr);
                logisticsTimeLimitation.setLogisticsType(typeStr);
                LogisticsTimeLimitation oneInfoByMode = mapper.getOneInfoByMode(modeStr);
                if (null != oneInfoByMode && null != oneInfoByMode.getId()) {
                    logisticsTimeLimitation.setId(oneInfoByMode.getId());
                    baseUpdate(logisticsTimeLimitation);
                } else {
                    logisticsTimeLimitation.setId(null);
                    insertList.add(logisticsTimeLimitation);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("物流时效出错" + e.getMessage());
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
        String path = "public/template/物流时效.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
