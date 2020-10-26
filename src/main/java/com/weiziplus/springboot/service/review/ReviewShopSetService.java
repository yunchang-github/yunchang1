package com.weiziplus.springboot.service.review;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.review.CrawlShopMapper;
import com.weiziplus.springboot.models.CrawlShop;
import com.weiziplus.springboot.models.HeadFreight;
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
 * @data 2019/8/27 16:36
 */
@Slf4j
@Service
public class ReviewShopSetService extends BaseService {

    @Autowired
    CrawlShopMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 获取分页数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(baseFindAllByClass(CrawlShop.class));
        return ResultUtil.success(pageUtil);
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/爬取店铺设置.xlsx";
        FileUtil.downResourceFile(response, path);
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
            List<Object> insertList = new ArrayList<>();
            //默认第一行为标题跳过
            CrawlShop crawlShop = new CrawlShop();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                for (int k = 0; k < excelTitleNum; k++) {
                    Cell cell = row.getCell(k);
                    if (null == cell) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    cell.setCellType(CellType.STRING);
                    String stringCellValue = cell.getStringCellValue();
                    if (StringUtil.isBlank(stringCellValue)) {
                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                        return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列文件有空数据，请补充完整后重试");
                    }
                    switch (k) {
                        case 0: {
                            crawlShop.setShop(stringCellValue);
                        }
                        break;
                        case 1: {
                            if (!areaService.getMwsCountryCodeList().contains(stringCellValue)) {
                                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                                return ResultUtil.error("导入失败,第" + j + "行,第" + (k + 1) + "列国家代码错误");
                            }
                            crawlShop.setArea(stringCellValue);
                        }
                        break;
                        case 2: {
                            crawlShop.setAsin(stringCellValue);
                        }
                        break;
                        default: {
                        }
                    }
                }
                CrawlShop oneInfoByShopAndAreaAndAsin = mapper.getOneInfoByShopAndAreaAndAsin(crawlShop.getShop()
                        , crawlShop.getArea(), crawlShop.getAsin());
                if (null != oneInfoByShopAndAreaAndAsin && null != oneInfoByShopAndAreaAndAsin.getId()) {
                    crawlShop.setId(oneInfoByShopAndAreaAndAsin.getId());
                    baseUpdate(crawlShop);
                } else {
                    crawlShop.setId(null);
                    insertList.add(crawlShop);
                }
            }
            baseInsertList(insertList);
            return ResultUtil.success();
        } catch (IOException e) {
            log.warn("店铺设置导入excel出错,出错详情:" + e);
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
    }

    /**
     * 新增
     *
     * @param crawlShop
     * @return
     */
    public ResultUtil add(CrawlShop crawlShop) {
        if (StringUtil.isBlank(crawlShop.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(crawlShop.getArea())) {
            return ResultUtil.error("国家代码不能为空");
        }
        if (StringUtil.isBlank(crawlShop.getAsin())) {
            return ResultUtil.error("asin不能为空");
        }
        CrawlShop oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndAsin(crawlShop.getShop()
                , crawlShop.getArea(), crawlShop.getAsin());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
            return ResultUtil.error("当前记录已存在");
        }
        baseInsert(crawlShop);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param crawlShop
     * @return
     */
    public ResultUtil update(CrawlShop crawlShop) {
        if (StringUtil.isBlank(crawlShop.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(crawlShop.getArea())) {
            return ResultUtil.error("国家代码不能为空");
        }
        if (StringUtil.isBlank(crawlShop.getAsin())) {
            return ResultUtil.error("asin不能为空");
        }
        CrawlShop oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndAsin(crawlShop.getShop()
                , crawlShop.getArea(), crawlShop.getAsin());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()
                && !oneInfoByShopAndAreaAndMsku.getId().equals(crawlShop.getId())) {
            return ResultUtil.error("当前记录已存在");
        }
        baseUpdate(crawlShop);
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
        baseDeleteByClassAndIds(CrawlShop.class, ids);
        return ResultUtil.success();
    }

}
