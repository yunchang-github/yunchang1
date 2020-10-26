package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.HeadFreightMapper;
import com.weiziplus.springboot.models.HeadFreight;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/17 10:29
 */
@Slf4j
@Service
public class HeadFreightService extends BaseService {

    @Autowired
    HeadFreightMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 基准redis的key
     */
   // private final String BASE_REDIS_KEY = "pc:service:data:manualimport:HeadFreightService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Map map) {
        int pageNum = MapUtils.getInteger(map, "pageNum");
        int pageSize = MapUtils.getInteger(map, "pageSize");
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
       /* String key = createRedisKey(BASE_REDIS_KEY + "getPageList", pageNum, pageSize);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }*/
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(
                mapper.getList(map));
//        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param headFreight
     * @return
     */
    public ResultUtil add(HeadFreight headFreight) {
        if (StringUtil.isBlank(headFreight.getShop())) {
            return ResultUtil.error("网点不能为空");
        }
        if (StringUtil.isBlank(headFreight.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(headFreight.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(headFreight.getHeadFreight())) {
            return ResultUtil.error("运费不能为空");
        }
        if (0 != headFreight.getIsEstimate() && 1 != headFreight.getIsEstimate()) {
            return ResultUtil.error("是否为预估错误");
        }
        if (!areaService.getMwsCountryCodeList().contains(headFreight.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        HeadFreight oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(headFreight.getShop(), headFreight.getArea(), headFreight.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
            return ResultUtil.error("当前记录已存在");
        }
        baseInsert(headFreight);
//        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param headFreight
     * @return
     */
    public ResultUtil update(HeadFreight headFreight) {
        if (StringUtil.isBlank(headFreight.getShop())) {
            return ResultUtil.error("网点不能为空");
        }
        if (StringUtil.isBlank(headFreight.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(headFreight.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(headFreight.getHeadFreight())) {
            return ResultUtil.error("运费不能为空");
        }
        if (0 != headFreight.getIsEstimate() && 1 != headFreight.getIsEstimate()) {
            return ResultUtil.error("是否为预估错误");
        }
        if (!areaService.getMwsCountryCodeList().contains(headFreight.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        HeadFreight oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(headFreight.getShop(), headFreight.getArea(), headFreight.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId() && !oneInfoByShopAndAreaAndMsku.getId().equals(headFreight.getId())) {
            return ResultUtil.error("当前记录已存在");
        }
        baseUpdate(headFreight);
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
        baseDeleteByClassAndIds(HeadFreight.class, ids);
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
            int excelTitleNum = 5;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为五列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                HeadFreight headFreight = new HeadFreight();
                Row row = sheet.getRow(j);
                Cell mskuCell = row.getCell(0);
                Cell shopCell = row.getCell(1);
                Cell areaCell = row.getCell(2);
                Cell freightCell = row.getCell(3);
                Cell estimateCell = row.getCell(4);
                if (null == mskuCell || null == shopCell || null == areaCell || null == freightCell || null == estimateCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                mskuCell.setCellType(CellType.STRING);
                shopCell.setCellType(CellType.STRING);
                areaCell.setCellType(CellType.STRING);
                freightCell.setCellType(CellType.STRING);
                estimateCell.setCellType(CellType.STRING);
                String mskuStr = mskuCell.getStringCellValue();
                String shopStr = shopCell.getStringCellValue();
                String areaStr = areaCell.getStringCellValue();
                String freightStr = freightCell.getStringCellValue();
                String estimateStr = estimateCell.getStringCellValue();
                if (StringUtil.isBlank(mskuStr) || StringUtil.isBlank(shopStr) || StringUtil.isBlank(areaStr)
                        || StringUtil.isBlank(freightStr) || StringUtil.isBlank(estimateStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                if (!areaService.getMwsCountryCodeList().contains(areaStr)) {
                    return ResultUtil.error("国家代码错误");
                }
                headFreight.setMsku(mskuStr);
                headFreight.setShop(shopStr);
                headFreight.setArea(areaStr);
                headFreight.setHeadFreight(freightStr);
                if ("是".equals(estimateStr)) {
                    headFreight.setIsEstimate(0);
                } else if ("否".equals(estimateStr)) {
                    headFreight.setIsEstimate(1);
                } else {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,预估为：是或否");
                }
                HeadFreight oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(shopStr, areaStr, mskuStr);
                if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
                    headFreight.setId(oneInfoByShopAndAreaAndMsku.getId());
                    baseUpdate(headFreight);
                } else {
                    headFreight.setId(null);
                    insertList.add(headFreight);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("头程运费出错" + e.getMessage());
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
        String path = "public/template/头程运费.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
