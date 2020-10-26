package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.MergeAsinMapper;
import com.weiziplus.springboot.models.MergeAsin;
import com.weiziplus.springboot.service.shop.AreaService;
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
 * @data 2019/7/19 8:38
 */
@Slf4j
@Service
public class MergeAsinService extends BaseService {

    @Autowired
    MergeAsinMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:MergeAsinService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String shop, String area, String asin) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", shop, area, asin);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(baseFindAllByClass(MergeAsin.class));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param mergeAsin
     * @return
     */
    public ResultUtil add(MergeAsin mergeAsin) {
        if (StringUtil.isBlank(mergeAsin.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(mergeAsin.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(mergeAsin.getAsin())) {
            return ResultUtil.error("asin不能为空");
        }
        if (StringUtil.isBlank(mergeAsin.getMergeAsin())) {
            return ResultUtil.error("被合并的asin不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(mergeAsin.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        MergeAsin oneInfoByShopAndAreaAndAsin = mapper.getOneInfoByShopAndAreaAndAsin(mergeAsin.getShop(), mergeAsin.getArea(), mergeAsin.getAsin());
        if (null != oneInfoByShopAndAreaAndAsin && null != oneInfoByShopAndAreaAndAsin.getId()) {
            return ResultUtil.error("该记录存在");
        }
        baseInsert(mergeAsin);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param mergeAsin
     * @return
     */
    public ResultUtil update(MergeAsin mergeAsin) {
        if (StringUtil.isBlank(mergeAsin.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(mergeAsin.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(mergeAsin.getAsin())) {
            return ResultUtil.error("asin不能为空");
        }
        if (StringUtil.isBlank(mergeAsin.getMergeAsin())) {
            return ResultUtil.error("被合并的asin不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(mergeAsin.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        MergeAsin oneInfoByShopAndAreaAndAsin = mapper.getOneInfoByShopAndAreaAndAsin(mergeAsin.getShop(), mergeAsin.getArea(), mergeAsin.getAsin());
        if (null != oneInfoByShopAndAreaAndAsin && null != oneInfoByShopAndAreaAndAsin.getId() && !oneInfoByShopAndAreaAndAsin.getId().equals(mergeAsin.getId())) {
            return ResultUtil.error("该记录存在");
        }
        baseUpdate(mergeAsin);
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
        baseDeleteByClassAndIds(MergeAsin.class, ids);
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
            int excelTitleNum = 4;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为四列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                MergeAsin mergeAsin = new MergeAsin();
                Row row = sheet.getRow(j);
                Cell shopCell = row.getCell(0);
                Cell areaCell = row.getCell(1);
                Cell asinCell = row.getCell(2);
                Cell mergeAsinCell = row.getCell(3);
                if (null == shopCell || null == areaCell || null == asinCell || null == mergeAsinCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                shopCell.setCellType(CellType.STRING);
                areaCell.setCellType(CellType.STRING);
                asinCell.setCellType(CellType.STRING);
                mergeAsinCell.setCellType(CellType.STRING);
                String shopStr = shopCell.getStringCellValue();
                String areaStr = areaCell.getStringCellValue();
                String asinStr = asinCell.getStringCellValue();
                String mergeAsinStr = mergeAsinCell.getStringCellValue();
                if (StringUtil.isBlank(shopStr) || StringUtil.isBlank(areaStr) || StringUtil.isBlank(asinStr) || StringUtil.isBlank(mergeAsinStr)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                if (!areaService.getMwsCountryCodeList().contains(areaStr)) {
                    return ResultUtil.error("国家代码错误");
                }
                mergeAsin.setShop(shopStr);
                mergeAsin.setArea(areaStr);
                mergeAsin.setAsin(asinStr);
                mergeAsin.setMergeAsin(mergeAsinStr);
                MergeAsin oneInfoByShopAndAreaAndAsin = mapper.getOneInfoByShopAndAreaAndAsin(shopStr, areaStr, asinStr);
                if (null != oneInfoByShopAndAreaAndAsin && null != oneInfoByShopAndAreaAndAsin.getId()) {
                    mergeAsin.setId(oneInfoByShopAndAreaAndAsin.getId());
                    baseUpdate(mergeAsin);
                } else {
                    mergeAsin.setId(null);
                    insertList.add(mergeAsin);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("ASIN合并信息出错" + e.getMessage());
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
        String path = "public/template/ASIN合并信息.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}