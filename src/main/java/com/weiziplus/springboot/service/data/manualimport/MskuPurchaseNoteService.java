package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.MskuPurchaseNoteMapper;
import com.weiziplus.springboot.models.MskuParentskuChildrenasinParentasin;
import com.weiziplus.springboot.models.MskuPurchaseNote;
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

/**
 * @author wanglongwei
 * @data 2019/7/29 8:35
 */
@Slf4j
@Service
public class MskuPurchaseNoteService extends BaseService {

    @Autowired
    MskuPurchaseNoteMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:MskuPurchaseNoteService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String shop, String area, String msku) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        String key = createRedisKey(BASE_REDIS_KEY + "getPageList", shop, area, msku);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(shop, area, msku));
        RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param mskuPurchaseNote
     * @return
     */
    public ResultUtil add(MskuPurchaseNote mskuPurchaseNote) {
        if (StringUtil.isBlank(mskuPurchaseNote.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(mskuPurchaseNote.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(mskuPurchaseNote.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(mskuPurchaseNote.getNote())) {
            return ResultUtil.error("注意事项不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(mskuPurchaseNote.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        MskuPurchaseNote oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(mskuPurchaseNote.getShop()
                , mskuPurchaseNote.getArea(), mskuPurchaseNote.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
            return ResultUtil.error("当前记录已存在");
        }
        baseInsert(mskuPurchaseNote);
        RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param mskuPurchaseNote
     * @return
     */
    public ResultUtil update(MskuPurchaseNote mskuPurchaseNote) {
        if (StringUtil.isBlank(mskuPurchaseNote.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(mskuPurchaseNote.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(mskuPurchaseNote.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(mskuPurchaseNote.getNote())) {
            return ResultUtil.error("注意事项不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(mskuPurchaseNote.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        MskuPurchaseNote oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(mskuPurchaseNote.getShop()
                , mskuPurchaseNote.getArea(), mskuPurchaseNote.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId() && !oneInfoByShopAndAreaAndMsku.getId().equals(mskuPurchaseNote.getId())) {
            return ResultUtil.error("当前记录已存在");
        }
        baseUpdate(mskuPurchaseNote);
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
        baseDeleteByClassAndIds(MskuPurchaseNote.class, ids);
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
            MskuPurchaseNote mskuPurchaseNote = new MskuPurchaseNote();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell mskuCell = row.getCell(0);
                Cell shopCell = row.getCell(1);
                Cell areaCell = row.getCell(2);
                Cell noteCell = row.getCell(3);
                if (null == mskuCell || null == shopCell || null == areaCell || null == noteCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                mskuCell.setCellType(CellType.STRING);
                shopCell.setCellType(CellType.STRING);
                areaCell.setCellType(CellType.STRING);
                noteCell.setCellType(CellType.STRING);
                String mskuValue = mskuCell.getStringCellValue();
                String shopValue = shopCell.getStringCellValue();
                String areaValue = areaCell.getStringCellValue();
                String noteValue = noteCell.getStringCellValue();
                if (StringUtil.isBlank(mskuValue) || StringUtil.isBlank(shopValue) || StringUtil.isBlank(areaValue) || StringUtil.isBlank(noteValue)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                if (!areaService.getMwsCountryCodeList().contains(areaValue)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行国家代码错误");
                }
                mskuPurchaseNote.setMsku(mskuValue);
                mskuPurchaseNote.setShop(shopValue);
                mskuPurchaseNote.setArea(areaValue);
                mskuPurchaseNote.setNote(noteValue);
                MskuPurchaseNote oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(shopValue, areaValue, mskuValue);
                if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
                    mskuPurchaseNote.setId(oneInfoByShopAndAreaAndMsku.getId());
                    baseUpdate(mskuPurchaseNote);
                } else {
                    mskuPurchaseNote.setId(null);
                    baseInsert(mskuPurchaseNote);
                }
            }
        } catch (IOException e) {
            log.warn("MSKU采购注意事项出错" + e.getMessage());
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
        String path = "public/template/MSKU采购注意事项.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
