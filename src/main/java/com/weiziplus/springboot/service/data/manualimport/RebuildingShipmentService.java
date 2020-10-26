package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.RebuildingShipmentMapper;
import com.weiziplus.springboot.models.RebuildingShipment;
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
 * @data 2019/7/17 16:57
 */
@Slf4j
@Service
public class RebuildingShipmentService extends BaseService {

    @Autowired
    RebuildingShipmentMapper mapper;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:RebuildingShipmentService";

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
       /*  String key = BASE_REDIS_KEY + "getPageList:pageNum_" + pageNum + "pageSize_" + pageSize;
       Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }*/
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getPageList(map));
//        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param rebuildingShipment
     * @return
     */
    public ResultUtil add(RebuildingShipment rebuildingShipment) {
        if (StringUtil.isBlank(rebuildingShipment.getNewShipmentId())) {
            return ResultUtil.error("新的id不能为空");
        }
        if (StringUtil.isBlank(rebuildingShipment.getNewShipmentId())) {
            return ResultUtil.error("被替换的的id不能为空");
        }
        RebuildingShipment oneInfoNewShipmentId = mapper.getOneInfoNewShipmentId(rebuildingShipment.getNewShipmentId());
        if (null != oneInfoNewShipmentId && null != oneInfoNewShipmentId.getId()) {
            return ResultUtil.error("该记录存在");
        }
        baseInsert(rebuildingShipment);
//        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param rebuildingShipment
     * @return
     */
    public ResultUtil update(RebuildingShipment rebuildingShipment) {
        if (StringUtil.isBlank(rebuildingShipment.getNewShipmentId())) {
            return ResultUtil.error("新的id不能为空");
        }
        if (StringUtil.isBlank(rebuildingShipment.getNewShipmentId())) {
            return ResultUtil.error("被替换的的id不能为空");
        }
        RebuildingShipment oneInfoNewShipmentId = mapper.getOneInfoNewShipmentId(rebuildingShipment.getNewShipmentId());
        if (null != oneInfoNewShipmentId && null != oneInfoNewShipmentId.getId() && !oneInfoNewShipmentId.getId().equals(rebuildingShipment.getId())) {
            return ResultUtil.error("该记录存在");
        }
        baseUpdate(rebuildingShipment);
//        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
        baseDeleteByClassAndIds(RebuildingShipment.class, ids);
//        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
            int excelTitleNum = 2;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为两列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            RebuildingShipment rebuildingShipment = new RebuildingShipment();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell newCell = row.getCell(0);
                Cell oldCell = row.getCell(1);
                if (null == newCell || null == oldCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                newCell.setCellType(CellType.STRING);
                oldCell.setCellType(CellType.STRING);
                String newStr = newCell.getStringCellValue();
                String oldStr = oldCell.getStringCellValue();
                if (StringUtil.isBlank(newStr) || StringUtil.isBlank(oldStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                rebuildingShipment.setNewShipmentId(newStr);
                rebuildingShipment.setReplaceShipmentId(oldStr);
                RebuildingShipment oneInfoNewShipmentId = mapper.getOneInfoNewShipmentId(newStr);
                if (null != oneInfoNewShipmentId && null != oneInfoNewShipmentId.getId()) {
                    rebuildingShipment.setId(oneInfoNewShipmentId.getId());
                    baseUpdate(rebuildingShipment);
                } else {
                    rebuildingShipment.setId(null);
                    baseInsert(rebuildingShipment);
                }
            }
        } catch (IOException e) {
            log.warn("重建货件出错" + e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
//        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/重建货件.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
