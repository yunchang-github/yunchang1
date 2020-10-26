package com.weiziplus.springboot.service.data.record;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.config.GlobalConfig;
import com.weiziplus.springboot.mapper.caravan.MskuMapper;
import com.weiziplus.springboot.mapper.caravan.PurchaseMapper;
import com.weiziplus.springboot.mapper.caravan.StockWarehouseDataMapper;
import com.weiziplus.springboot.mapper.data.record.DataGetErrorRecordMapper;
import com.weiziplus.springboot.mapper.review.CrawlGoodsReviewMapper;
import com.weiziplus.springboot.mapper.shop.ShopMapper;
import com.weiziplus.springboot.mapper.sspa.*;
import com.weiziplus.springboot.models.*;
import com.weiziplus.springboot.scheduled.AmazonAdvertApiSchedule;
import com.weiziplus.springboot.scheduled.AmazonGoodReviewSchedule;
import com.weiziplus.springboot.scheduled.caravanapi.CaravanApiSchedule;
import com.weiziplus.springboot.service.shop.AreaService;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.*;
import com.weiziplus.springboot.utils.amazon.AmazonAdvertApiUtil;
import com.weiziplus.springboot.utils.amazon.AmazonGoodReviewUtil;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wanglongwei
 * @data 2019/8/21 15:46
 */
@Slf4j
@Service
public class DataGetErrorRecordService extends BaseService {

    @Autowired
    DataGetErrorRecordMapper mapper;

    @Autowired
    SalesAndTrafficMapper salesAndTrafficMapper;

    @Autowired
    DetailPageSalesAndTrafficByParentItemMapper detailPageSalesAndTrafficByParentItemMapper;

    @Autowired
    DetailPageSalesAndTrafficByChildItemMapper detailPageSalesAndTrafficByChildItemMapper;

    @Autowired
    AmazonAdvertApiSchedule amazonAdvertApiSchedule;

    @Autowired
    SponsoredProductsAdvertisedProductReportMapper sponsoredProductsAdvertisedProductReportMapper;

    @Autowired
    SponsoredProductsPlacementReportMapper sponsoredProductsPlacementReportMapper;

    @Autowired
    SponsoredProductsSearchTermReportMapper sponsoredProductsSearchTermReportMapper;

    @Autowired
    CrawlGoodsReviewMapper crawlGoodsReviewMapper;

    @Autowired
    AreaService areaService;

    @Autowired
    CaravanApiSchedule caravanApiSchedule;

    @Autowired
    StockWarehouseDataMapper stockWarehouseDataMapper;

    @Autowired
    PurchaseMapper purchaseMapper;

    @Autowired
    MskuMapper mskuMapper;

    @Autowired
    ShopMapper shopMapper;

    /**
     * 当前网店名字
     */
    private final String SHOP_NAME = GlobalConfig.SHOP_NAME;

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Integer pageNum, Integer pageSize, String shop, String area, String date, Integer type
            , Integer isHandle, String createTime) {
        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(mapper.getList(shop, area, date, type, isHandle, createTime));
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增错误记录
     *
     * @param date
     * @param shop
     * @param area
     * @param remark
     * @param type   那种类型的定时任务，1:浏览器亚马逊后台定时任务，4;秒杀定时任务
     * @return
     */
    public ResultUtil chromeAddRecord(String date, String shop, String area, String name, String remark, Integer type) {
        if (StringUtil.isBlank(date)) {
            return ResultUtil.error("时间不能为空");
        }
        if (StringUtil.isBlank(shop)) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(area)) {
            return ResultUtil.error("国家代码不能为空");
        }
        if (StringUtil.isBlank(name)) {
            return ResultUtil.error("任务名称不能为空");
        }
        //1:浏览器亚马逊后台定时任务，4;秒杀定时任务
        if (1 != type && 4 != type) {
            return ResultUtil.error("类型错误");
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setDate(date);
        //浏览器亚马逊后台定时任务
        record.setType(type);
        record.setIsHandle(0);
        record.setShop(shop);
        record.setArea(area);
        record.setName(name);
        record.setRemark(remark);
        record.setCreateTime(DateUtil.getFutureDateTime(0));
        return ResultUtil.success(baseInsert(record));
    }

    /**
     * 修复销售与访问量（Sales and Traffic）
     *
     * @param salesAndTraffic
     * @param dataGetErrorRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataSalesAndTraffic(SalesAndTraffic salesAndTraffic, DataGetErrorRecord dataGetErrorRecord) {
        if (null == salesAndTraffic || null == dataGetErrorRecord) {
            return ResultUtil.error("数据错误，请补充完整");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getArea())) {
            return ResultUtil.error("国家代码不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (null == salesAndTraffic.getOrderedProductSales()) {
            return ResultUtil.error("已订购商品销售额不能为空");
        }
        if (null == salesAndTraffic.getOrderedProductSalesB2b()) {
            return ResultUtil.error("已订购商品的销售额B2B不能为空");
        }
        if (null == salesAndTraffic.getUnitsOrdered()) {
            return ResultUtil.error("已订购商品数量不能为空");
        }
        if (null == salesAndTraffic.getUnitsOrderedB2b()) {
            return ResultUtil.error("已订购商品数量B2B不能为空");
        }
        if (null == salesAndTraffic.getTotalOrderItems()) {
            return ResultUtil.error("订单商品种类数不能为空");
        }
        if (null == salesAndTraffic.getTotalOrderItemsB2b()) {
            return ResultUtil.error("订单商品种类数B2B不能为空");
        }
        if (null == salesAndTraffic.getAverageSalesPerOrderItem()) {
            return ResultUtil.error("每种订单商品的平均销售额不能为空");
        }
        if (null == salesAndTraffic.getAverageSalesPerOrderItemB2b()) {
            return ResultUtil.error("每种订单商品的平均销售额b2b不能为空");
        }
        if (null == salesAndTraffic.getAverageUnitsPerOrderItem()) {
            return ResultUtil.error("每种订单商品的平均数量不能为空");
        }
        if (null == salesAndTraffic.getAverageUnitsPerOrderItemB2b()) {
            return ResultUtil.error("每种订单商品的平均数量B2B不能为空");
        }
        if (null == salesAndTraffic.getAverageSellingPrice()) {
            return ResultUtil.error("平均销售价格不能为空");
        }
        if (null == salesAndTraffic.getAverageSellingPriceB2b()) {
            return ResultUtil.error("平均销售价格B2B不能为空");
        }
        if (null == salesAndTraffic.getSessions()) {
            return ResultUtil.error("买家访问次数不能为空");
        }
        if (null == salesAndTraffic.getOrderItemSessionPercentage()) {
            return ResultUtil.error("订单商品种类数转化率不能为空");
        }
        if (null == salesAndTraffic.getOrderItemSessionPercentageB2b()) {
            return ResultUtil.error("订单商品种类数转化率B2B不能为空");
        }
        if (null == salesAndTraffic.getAverageOfferCount()) {
            return ResultUtil.error("平均在售商品数量不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("销售与访问量修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> map = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(map.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        String date = salesAndTraffic.getDate().substring(0, 10);
        SalesAndTraffic oneInfoByShopAndAreaAndDate = salesAndTrafficMapper.getOneInfoByShopAndAreaAndDate(salesAndTraffic.getShop(), salesAndTraffic.getArea()
                , date);
        if (null != oneInfoByShopAndAreaAndDate) {
            return ResultUtil.error("当前店铺、国家代码、日期，存在数据");
        }
        //因为参数是实体类接受，id两个实体类都有
        salesAndTraffic.setId(null);
        salesAndTraffic.setShop(dataGetErrorRecord.getShop());
        salesAndTraffic.setArea(dataGetErrorRecord.getArea());
        salesAndTraffic.setDate(date);
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(dataGetErrorRecord.getId());
        //将该数据修改为已处理
        record.setIsHandle(1);
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            baseInsert(salesAndTraffic);
            baseUpdate(record);
            return ResultUtil.success();
        } catch (Exception e) {
            log.warn("销售与访问量修复错误，错误详情:" + e);
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
    }

    /**
     * 修复父商品详情页面上的销售量与访问量
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataDetailSalesTrafficByParentItem(MultipartFile file, DataGetErrorRecord dataGetErrorRecord) {
        //判断文件是否为空
        if (null == file || 0 >= file.getSize()) {
            return ResultUtil.error("文件不存在");
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        if (ValidateUtil.notExcel(fileName)) {
            return ResultUtil.error("文件格式错误,请手动将csv格式文件另存为xlsx格式，然后上传");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("父商品详情页面上的销售量与访问量修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> map = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(map.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        String date = dataGetErrorRecord.getDate().substring(0, 10);
        DetailPageSalesAndTrafficByParentItem oneInfoByShopAndAreaAndDate = detailPageSalesAndTrafficByParentItemMapper.getOneInfoByShopAndAreaAndDate(dataGetErrorRecord.getShop()
                , dataGetErrorRecord.getArea(), date);
        if (null != oneInfoByShopAndAreaAndDate) {
            return ResultUtil.error("当前店铺、国家代码、日期，存在数据");
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
            int excelTitleNum = 15;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为十五列，请检查文件后重试");
            }
            DetailPageSalesAndTrafficByParentItem item = new DetailPageSalesAndTrafficByParentItem();
            item.setShop(dataGetErrorRecord.getShop());
            item.setArea(dataGetErrorRecord.getArea());
            item.setDate(dataGetErrorRecord.getDate().substring(0, 10));
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
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
                    //excel表中%的列
                    List<Integer> percentageColumn = Arrays.asList(3, 5, 6, 9, 10);
                    if (percentageColumn.contains(k) && !StringUtil.isBlank(stringCellValue)) {
                        stringCellValue = stringCellValue.replace("E-2", "");
                    }
                    switch (k) {
                        case 0: {
                            item.setParentAsin(stringCellValue);
                        }
                        break;
                        case 1: {
                            item.setTitle(stringCellValue);
                        }
                        break;
                        case 2: {
                            item.setSessions(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 3: {
                            item.setSessionPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 4: {
                            item.setPageViews(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 5: {
                            item.setPageViewsPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 6: {
                            item.setBuyBoxPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 7: {
                            item.setUnitsOrdered(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 9: {
                            item.setUnitSessionPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 11: {
                            item.setOrderedProductSales(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 12: {
                            item.setTotalOrderItems(Integer.valueOf(stringCellValue));
                        }
                        break;
                        default: {

                        }
                    }
                }
                insertList.add(item);
            }
            baseInsertList(insertList);
            DataGetErrorRecord record = new DataGetErrorRecord();
            record.setId(dataGetErrorRecord.getId());
            //将该数据修改为已处理
            record.setIsHandle(1);
            baseUpdate(record);
            return ResultUtil.success();
        } catch (Exception e) {
            log.warn("父商品详情页面上的销售量与访问量修复错误，错误详情:" + e);
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
    }

    /**
     * 修复子商品详情页面上的销售量与访问量
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataDetailSalesTrafficByChildItem(MultipartFile file, DataGetErrorRecord dataGetErrorRecord) {
        //判断文件是否为空
        if (null == file || 0 >= file.getSize()) {
            return ResultUtil.error("文件不存在");
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        if (ValidateUtil.notExcel(fileName)) {
            return ResultUtil.error("文件格式错误,请手动将csv格式文件另存为xlsx格式，然后上传");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getShop())) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getArea())) {
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("子商品详情页面上的销售量与访问量修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> map = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(map.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        String date = dataGetErrorRecord.getDate().substring(0, 10);
        DetailPageSalesAndTrafficByChildItem oneInfoByShopAndAreaAndDate = detailPageSalesAndTrafficByChildItemMapper.getOneInfoByShopAndAreaAndDate(dataGetErrorRecord.getShop()
                , dataGetErrorRecord.getArea(), date);
        if (null != oneInfoByShopAndAreaAndDate) {
            return ResultUtil.error("当前店铺、国家代码、日期，存在数据");
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
            int excelTitleNum = 16;
            if (excelTitleNum != sheet.getRow(0).getPhysicalNumberOfCells()) {
                return ResultUtil.error("标准格式为十六列，请检查文件后重试");
            }
            DetailPageSalesAndTrafficByChildItem item = new DetailPageSalesAndTrafficByChildItem();
            item.setShop(dataGetErrorRecord.getShop());
            item.setArea(dataGetErrorRecord.getArea());
            item.setDate(dataGetErrorRecord.getDate().substring(0, 10));
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
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
                    //excel表中%的列
                    List<Integer> percentageColumn = Arrays.asList(4, 6, 7, 10, 11);
                    if (percentageColumn.contains(k) && !StringUtil.isBlank(stringCellValue)) {
                        stringCellValue = stringCellValue.replace("E-2", "");
                    }
                    switch (k) {
                        case 0: {
                            item.setPasin(stringCellValue);
                        }
                        break;
                        case 1: {
                            item.setCasin(stringCellValue);
                        }
                        break;
                        case 2: {
                            item.setTitle(stringCellValue);
                        }
                        break;
                        case 3: {
                            item.setSessions(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 4: {
                            item.setSessionPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 5: {
                            item.setPageViews(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 6: {
                            item.setPageViewsPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 7: {
                            item.setBuyBoxPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 8: {
                            item.setUnitsOrdered(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 9: {
                            item.setUnitsOrderedB2b(stringCellValue);
                        }
                        break;
                        case 10: {
                            item.setUnitSessionPercentage(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 11: {
                            item.setUnitSessionPercentageB2b(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 12: {
                            item.setOrderedProductSales(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 13: {
                            item.setOrderedProductSalesB2b(Double.valueOf(stringCellValue));
                        }
                        break;
                        case 14: {
                            item.setTotalOrderItems(Integer.valueOf(stringCellValue));
                        }
                        break;
                        case 15: {
                            item.setTotalOrderItemsB2b(stringCellValue);
                        }
                        break;
                        default: {

                        }
                    }
                }
                insertList.add(item);
            }
            baseInsertList(insertList);
            DataGetErrorRecord record = new DataGetErrorRecord();
            record.setId(dataGetErrorRecord.getId());
            //将该数据修改为已处理
            record.setIsHandle(1);
            baseUpdate(record);
            return ResultUtil.success();
        } catch (Exception e) {
            log.warn("子商品详情页面上的销售量与访问量修复错误，错误详情:" + e);
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("系统错误，请重试");
        }
    }

    /**
     * 修复修复亚马逊广告api定时任务
     *
     * @param dataGetErrorRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataAdvertApiSchedule(DataGetErrorRecord dataGetErrorRecord) {
        if (StringUtil.isBlank(dataGetErrorRecord.getShop())) {
            return ResultUtil.error("网店不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getArea())) {
            return ResultUtil.error("国家代码不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        if (!SHOP_NAME.equals(dataGetErrorRecord.getShop())) {
            return ResultUtil.error("错误，网店服务器不匹配");
        }
        Shop shop = shopMapper.getOneInfoByName(dataGetErrorRecord.getShop());
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("亚马逊广告api定时任务修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> map = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(map.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        int maxErrorNum = 3;
        boolean tokenFlag = false;
        for (int i = 0; i < maxErrorNum; i++) {
            //获取token
            //tokenFlag = AmazonAdvertApiUtil.setAccessTokenToRedis(shop);
            if (tokenFlag) {
                break;
            }
        }
        if (!tokenFlag) {
            log.warn("修复亚马逊广告api定时任务，token获取失败");
            return ResultUtil.error("系统错误，请重试");
        }
        List<Map<String, Object>> profiles = null;
        for (int i = 0; i < maxErrorNum; i++) {
            //profiles = AmazonAdvertApiUtil.getProfiles(shop);
            if (null != profiles) {
                break;
            }
        }
        if (null == profiles) {
            log.warn("修复修复亚马逊广告api定时任务，profiles配置文件获取失败");
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> params = new HashMap<>(2);
        try {
            params.put("reportDate", DateUtil.dateToFutureMonthDate(dataGetErrorRecord.getDate(), -1).replace("-", ""));
        } catch (ParseException e) {
            log.warn("修复修复亚马逊广告api定时任务，时间转换出错");
            return ResultUtil.error("系统错误，请重试");
        }
        params.put("metrics", "currency,campaignName,adGroupName,sku,asin,impressions,clicks,cost,attributedSales7d,attributedUnitsOrdered7d" +
                ",attributedUnitsOrdered7dSameSKU,attributedSales7dSameSKU");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        for (Map<String, Object> profilesMap : profiles) {
            String profileId = String.valueOf(profilesMap.get("profileId"));
            String countryCode = String.valueOf(profilesMap.get("countryCode"));
            try {
//                List<Map<String, Object>> lists = AmazonAdvertApiUtil.getReport(profileId, CryptoUtil.decode(shop.getSellerId()),"sp", "productAds", params);
//                if (null == lists || 0 > lists.size()) {
//                    return ResultUtil.error("Sponsored Products Advertised product report数据获取失败");
//                }
                //处理Sponsored Products Advertised product report和Sponsored Products Performance Over Time report
                //获取数据出错时间的前天
                String date = DateUtil.dateToFutureDate(dataGetErrorRecord.getDate(), -2);
                String sponsoredProductsAdvertisedProductReportLatestDay = sponsoredProductsAdvertisedProductReportMapper.getLatestDay(shop.getShopName(),countryCode);
//                if (null == sponsoredProductsAdvertisedProductReportLatestDay
//                        || DateUtil.compateTime(sponsoredProductsAdvertisedProductReportLatestDay, date) < 0) {
//                    amazonAdvertApiSchedule.handleSponsoredProductsAdvertisedProductReport(lists,shop.getShopName(), countryCode, date);
//                }
//                String currency = String.valueOf(lists.get(0).get("currency"));
//                String sponsoredProductsPlacementReportLatestDay = sponsoredProductsPlacementReportMapper.getLatestDay(shop.getShopName(),countryCode);
//                if (null == sponsoredProductsPlacementReportLatestDay
//                        || DateUtil.compateTime(sponsoredProductsPlacementReportLatestDay, date) < 0) {
//                    boolean flag = amazonAdvertApiSchedule.handleSponsoredProductsPlacementReport(profileId,shop, currency, countryCode);
//                    if (!flag) {
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        return ResultUtil.error("处理Sponsored Products Placement report出错");
//                    }
//                }
//                String sponsoredProductsSearchTermReportLatestDay = sponsoredProductsSearchTermReportMapper.getLatestDay(shop.getShopName(),countryCode);
//                if (null == sponsoredProductsSearchTermReportLatestDay
//                        || DateUtil.compateTime(sponsoredProductsSearchTermReportLatestDay, date) < 0) {
//                    boolean flag = amazonAdvertApiSchedule.handleSponsoredProductsSearchTermReport(profileId, shop,currency, countryCode);
//                    if (!flag) {
//                        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
//                        return ResultUtil.error("处理Sponsored Products Search term report出错");
//                    }
//                }
            } catch (Exception e) {
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                log.warn("修复亚马逊广告api定时任务出错，详情:" + e);
                return ResultUtil.error("系统错误，请重试");
            }
        }
        try {
            DataGetErrorRecord record = new DataGetErrorRecord();
            record.setId(dataGetErrorRecord.getId());
            //将该数据修改为已处理
            record.setIsHandle(1);
            baseUpdate(record);
            return ResultUtil.success();
        } catch (Exception e) {
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            log.warn("修复亚马逊广告api定时任务出错，详情:" + e);
            return ResultUtil.error("修复失败，请重试");
        }
    }

    /**
     * 修复获取商品评价定时任务结束
     *
     * @param dataGetErrorRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataReviewSchedule(DataGetErrorRecord dataGetErrorRecord) {
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("获取商品评价定时任务修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> oldMap = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(oldMap.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        List<Object> insertList = new ArrayList<>();
        //获取所有的店铺、国家代码、asin信息
        List<Map<String, Object>> list = baseFindAllByClass(CrawlShop.class);
        AmazonGoodReviewSchedule schedule = new AmazonGoodReviewSchedule();
        try {
            for (Map<String, Object> map : list) {
                String area = StringUtil.valueOf(map.get("area"));
                String baseUrl = schedule.BASE_URL_PREFIX.get(area);
                //国家代码异常
                if (StringUtil.isBlank(baseUrl)) {
                    log.warn("获取商品评价定时任务,没有找到国家代码为:" + area + "对应的商品详情路径前缀,详细信息:" + JSON.toJSONString(map));
                    return ResultUtil.error("国家代码异常，请检查网店设置");
                }
                String shop = StringUtil.valueOf(map.get("shop"));
                //网点异常
                if (StringUtil.isBlank(shop)) {
                    log.warn("获取商品评价定时任务,网店为空,详细信息:" + JSON.toJSONString(map));
                    return ResultUtil.error("网店设置异常，请检查网店设置");
                }
                String asin = StringUtil.valueOf(map.get("asin"));
                if (StringUtil.isBlank(asin)) {
                    log.warn("获取商品评价定时任务,asin为空,详细信息:" + JSON.toJSONString(map));
                    return ResultUtil.error("asin为空，请检查网店设置");
                }
                Map<String, Object> review = AmazonGoodReviewUtil.getNumAndStar(baseUrl, asin);
                if (null == review) {
                    //回滚事务
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    log.warn("获取商品评价定时任务,没有获取到评论信息,详细信息:" + JSON.toJSONString(map));
                    return ResultUtil.error("asin为:" + asin + "数据获取失败，请重试");
                }
                CrawlGoodsReview goodsReview = new CrawlGoodsReview();
                goodsReview.setShop(shop);
                goodsReview.setArea(area);
                goodsReview.setAsin(asin);
                goodsReview.setStarDetail(StringUtil.valueOf(review.get("star")));
                goodsReview.setNum(ToolUtil.valueOfInteger(StringUtil.valueOf(review.get("num"))));
                CrawlGoodsReview oneInfoByShopAndAreaAndAsin = crawlGoodsReviewMapper.getOneInfoByShopAndAreaAndAsin(shop, area, asin);
                if (null != oneInfoByShopAndAreaAndAsin && null != oneInfoByShopAndAreaAndAsin.getId()) {
                    goodsReview.setId(oneInfoByShopAndAreaAndAsin.getId());
                    baseUpdate(goodsReview);
                } else {
                    goodsReview.setId(null);
                    insertList.add(goodsReview);
                }
                //随机休眠几秒
                Thread.sleep(1 + Math.round(Math.random() * 7));
            }
            baseInsertList(insertList);
            DataGetErrorRecord record = new DataGetErrorRecord();
            record.setId(dataGetErrorRecord.getId());
            //将该数据修改为已处理
            record.setIsHandle(1);
            baseUpdate(record);
            return ResultUtil.success();
        } catch (Exception e) {
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            log.warn("修复获取商品评价定时任务出错，详情:", e);
            return ResultUtil.error("系统错误，请重试");
        }
    }

    /**
     * 修复马帮api定时任务
     *
     * @param dataGetErrorRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil repairErrorDataCaravanApiSchedule(DataGetErrorRecord dataGetErrorRecord) {
        if (StringUtil.isBlank(dataGetErrorRecord.getName())) {
            return ResultUtil.error("任务名不能为空");
        }
        if (StringUtil.isBlank(dataGetErrorRecord.getDate())) {
            return ResultUtil.error("日期不能为空");
        }
        if (null == dataGetErrorRecord.getId()) {
            return ResultUtil.error("id不能为空");
        }
        String nowTime = DateUtil.getFutureDateTime(0);
        try {
            //如果当前时间小于记录出错时间
            if (DateUtil.compateTime(nowTime, dataGetErrorRecord.getDate()) < 0) {
                return ResultUtil.error("date时间错误");
            }
        } catch (ParseException e) {
            log.warn("获取商品评价定时任务修复错误，错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
        Map<String, Object> oldMap = baseFindByClassAndId(DataGetErrorRecord.class, dataGetErrorRecord.getId());
        String isHandle = String.valueOf(oldMap.get("isHandle"));
        //0---未处理该数据
        String notHandle = "0";
        if (!notHandle.equals(isHandle)) {
            return ResultUtil.error("该数据已处理，本次操作无效");
        }
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            return handleRepairErrorDataCaravanApiSchedule(savepoint, dataGetErrorRecord);
        } catch (Exception e) {
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            log.warn("修复马帮api出错，任务名" + dataGetErrorRecord.getName() + ",错误详情:" + e);
            return ResultUtil.error("系统错误，请重试");
        }
    }

    /**
     * 处理马帮api修复
     *
     * @param savepoint
     * @param dataGetErrorRecord
     * @return
     */
    private ResultUtil handleRepairErrorDataCaravanApiSchedule(Object savepoint, DataGetErrorRecord dataGetErrorRecord) throws Exception {
        String errorTime = dataGetErrorRecord.getDate();
        boolean handleSuccess = false;
        switch (dataGetErrorRecord.getName()) {
            case "调拨发货": {
                String startTime = getLastMinuteByTime(errorTime, 5);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("msku-shippbatch-data", "调拨发货"
                        , startTime, errorTime);
            }
            break;
            case "库存查询": {
                String startDate = getLastDateByDate(errorTime, 1);
                stockWarehouseDataMapper.deleteByDate(startDate);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("stock-warehouse-data", "库存查询"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            case "采购单": {
                String startDate = getLastDateByDate(errorTime, 1);
                purchaseMapper.deleteByDate(startDate);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("purchase-data", "采购单"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            case "出入库流水": {
                String startDate = getLastDateByDate(errorTime, 1);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("storage-log-data", "出入库流水"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            case "收付款单": {
                String startDate = getLastDateByDate(errorTime, 1);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("bills-data", "收付款单"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            case "MSKU列表": {
                String startDate = getLastDateByDate(errorTime, 1);
                mskuMapper.deleteByDate(startDate);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("msku-data", "MSKU列表"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            case "账户日志": {
                String startDate = getLastDateByDate(errorTime, 1);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("account-data", "账户日志"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            case "补货计划": {
                String startTime = getLastMinuteByTime(errorTime, 5);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("msku-ship-data", "补货计划"
                        , startTime, errorTime);
            }
            break;
            case "订单列表": {
                String startDate = getLastDateByDate(errorTime, 1);
                handleSuccess = caravanApiSchedule.baseHandleGetDate("order-data", "订单列表"
                        , startDate + " 00:00:00", errorTime.substring(0, 10) + " 00:00:00");
            }
            break;
            default: {
            }
        }
        if (!handleSuccess) {
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("修复失败，请重试");
        }
        DataGetErrorRecord record = new DataGetErrorRecord();
        record.setId(dataGetErrorRecord.getId());
        //将该数据修改为已处理
        record.setIsHandle(1);
        baseUpdate(record);
        return ResultUtil.success();
    }

    /**
     * 获取某个时间多少分钟前的时间
     *
     * @param time
     * @param past
     * @return
     * @throws ParseException
     */
    private String getLastMinuteByTime(String time, int past) throws ParseException {
        String basePattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(basePattern.substring(0, time.length()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(time));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - past);
        calendar.set(Calendar.SECOND, 0);
        Date calendarTime = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(basePattern);
        return simpleDateFormat.format(calendarTime);
    }

    /**
     * 获取某个时间多少天之前的日期
     *
     * @param date
     * @param past
     * @return
     * @throws ParseException
     */
    private String getLastDateByDate(String date, int past) throws ParseException {
        String basePattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(basePattern.substring(0, date.length()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - past);
        Date calendarTime = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendarTime);
    }

    public static void main(String[] args) throws ParseException {
        String date = "2019-09-03 16:00:10";
        DataGetErrorRecordService service = new DataGetErrorRecordService();
        String lastDateByDate = service.getLastDateByDate(date, -2);
        System.out.println(lastDateByDate);
        String lastDateByDate1 = service.getLastDateByDate(date.substring(0, 15), -2);
        System.out.println(lastDateByDate1);
    }
}
