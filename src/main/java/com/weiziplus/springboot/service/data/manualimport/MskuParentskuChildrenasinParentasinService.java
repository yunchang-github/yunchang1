package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.MskuParentskuChildrenasinParentasinMapper;
import com.weiziplus.springboot.models.MskuParentskuChildrenasinParentasin;
import com.weiziplus.springboot.service.shop.AreaService;
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
 * @data 2019/7/11 8:45
 */
@Slf4j
@Service
public class MskuParentskuChildrenasinParentasinService extends BaseService {

    @Autowired
    MskuParentskuChildrenasinParentasinMapper mapper;

    @Autowired
    AreaService areaService;

    /**
     * 基准redis的key
     */
    private final String BASE_REDIS_KEY = "pc:service:data:manualimport:MskuParentskuChildrenasinParentasinService";

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
    /*    String key = createRedisKey(BASE_REDIS_KEY + "getPageList", shop, area, msku);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }*/
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(map));
      //  RedisUtil.set(key, pageUtil);
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param mskuParentskuChildrenasinParentasin
     * @return
     */
    public ResultUtil add(MskuParentskuChildrenasinParentasin mskuParentskuChildrenasinParentasin) {
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getParentSku())) {
            return ResultUtil.error("父sku不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(mskuParentskuChildrenasinParentasin.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        MskuParentskuChildrenasinParentasin oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(mskuParentskuChildrenasinParentasin.getShop()
                , mskuParentskuChildrenasinParentasin.getArea(), mskuParentskuChildrenasinParentasin.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
            return ResultUtil.error("当前记录已存在");
        }
        baseInsert(mskuParentskuChildrenasinParentasin);
        //RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param mskuParentskuChildrenasinParentasin
     * @return
     */
    public ResultUtil update(MskuParentskuChildrenasinParentasin mskuParentskuChildrenasinParentasin) {
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getMsku())) {
            return ResultUtil.error("msku不能为空");
        }
        if (StringUtil.isBlank(mskuParentskuChildrenasinParentasin.getParentSku())) {
            return ResultUtil.error("父sku不能为空");
        }
        if (!areaService.getMwsCountryCodeList().contains(mskuParentskuChildrenasinParentasin.getArea())) {
            return ResultUtil.error("国家代码错误");
        }
        MskuParentskuChildrenasinParentasin oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(mskuParentskuChildrenasinParentasin.getShop()
                , mskuParentskuChildrenasinParentasin.getArea(), mskuParentskuChildrenasinParentasin.getMsku());
        if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId() && !oneInfoByShopAndAreaAndMsku.getId().equals(mskuParentskuChildrenasinParentasin.getId())) {
            return ResultUtil.error("当前记录已存在");
        }
        baseUpdate(mskuParentskuChildrenasinParentasin);
        //RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
        baseDeleteByClassAndIds(MskuParentskuChildrenasinParentasin.class, ids);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
            MskuParentskuChildrenasinParentasin mskuParentskuChildrenasinParentasin = new MskuParentskuChildrenasinParentasin();
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell shopCell = row.getCell(0);
                Cell areaCell = row.getCell(1);
                Cell mskuCell = row.getCell(2);
                Cell parentSkuCell = row.getCell(3);
                if (null == shopCell || null == areaCell || null == mskuCell || null == parentSkuCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                shopCell.setCellType(CellType.STRING);
                areaCell.setCellType(CellType.STRING);
                mskuCell.setCellType(CellType.STRING);
                parentSkuCell.setCellType(CellType.STRING);
                String shopValue = shopCell.getStringCellValue();
                String areaValue = areaCell.getStringCellValue();
                String mskuValue = mskuCell.getStringCellValue();
                String parentSkuValue = parentSkuCell.getStringCellValue();
                if (StringUtil.isBlank(shopValue) || StringUtil.isBlank(areaValue) || StringUtil.isBlank(mskuValue) || StringUtil.isBlank(parentSkuValue)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                if (!areaService.getMwsCountryCodeList().contains(areaValue)) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行国家代码错误");
                }
                mskuParentskuChildrenasinParentasin.setShop(shopValue);
                mskuParentskuChildrenasinParentasin.setArea(areaValue);
                mskuParentskuChildrenasinParentasin.setMsku(mskuValue);
                mskuParentskuChildrenasinParentasin.setParentSku(parentSkuValue);
                MskuParentskuChildrenasinParentasin oneInfoByShopAndAreaAndMsku = mapper.getOneInfoByShopAndAreaAndMsku(shopValue, areaValue, mskuValue);
                if (null != oneInfoByShopAndAreaAndMsku && null != oneInfoByShopAndAreaAndMsku.getId()) {
                    mskuParentskuChildrenasinParentasin.setId(oneInfoByShopAndAreaAndMsku.getId());
                    baseUpdate(mskuParentskuChildrenasinParentasin);
                } else {
                    mskuParentskuChildrenasinParentasin.setId(null);
                    baseInsert(mskuParentskuChildrenasinParentasin);
                }
            }
        } catch (IOException e) {
            log.warn("MSKU子父体对应关系出错" + e.getMessage());
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
        String path = "public/template/MSKU子父体对应关系.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
