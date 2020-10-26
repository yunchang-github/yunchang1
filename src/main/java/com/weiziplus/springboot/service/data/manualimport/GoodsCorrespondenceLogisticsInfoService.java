package com.weiziplus.springboot.service.data.manualimport;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.data.manualimport.GoodsCorrespondenceLogisticsInfoMapper;
import com.weiziplus.springboot.models.GoodsCorrespondenceLogisticsInfo;
import com.weiziplus.springboot.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author wanglongwei
 * @data 2019/7/17 17:51
 */
@Slf4j
@Service
public class GoodsCorrespondenceLogisticsInfoService extends BaseService {

    @Autowired
    GoodsCorrespondenceLogisticsInfoMapper mapper;

    /**
     * 基准redis的key
     */
    //private final String BASE_REDIS_KEY = "pc:service:data:manualimport:GoodsCorrespondenceLogisticsInfoService";

    /**
     * 获取分页列表数据
     *
     * @return
     */
    public ResultUtil getPageList(Map map) {

        Integer pageNum = MapUtils.getInteger(map,"pageNum");
        Integer pageSize = MapUtils.getInteger(map,"pageSize" );

        if (0 >= pageNum || 0 >= pageSize) {
            return ResultUtil.error("pageNum,pageSize错误");
        }
       /* String key = createRedisKey(BASE_REDIS_KEY + "getPageList", pageNum, pageSize, date, shipmentId, logisticsNumber
                , estimatedDeliveryTime, actualDepartureTime, predictedDetectionTime);
        Object object = RedisUtil.get(key);
        if (null != object) {
            return ResultUtil.success(object);
        }*/
        PageHelper.startPage(pageNum, pageSize);
        PageUtil pageUtil = PageUtil.pageInfo(
                mapper.getList(map));
       /* RedisUtil.set(key, pageUtil);*/
        return ResultUtil.success(pageUtil);
    }

    /**
     * 新增
     *
     * @param goodsCorrespondenceLogisticsInfo
     * @return
     */
    public ResultUtil add(GoodsCorrespondenceLogisticsInfo goodsCorrespondenceLogisticsInfo) {

        if(StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getShop())){
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getArea())){
            return ResultUtil.error("区域不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getDate())) {
            return ResultUtil.error("建计划日期不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getShipmentId())) {
            return ResultUtil.error("shipmentId不能为空");
        }
       if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getWmsGoodsNumber())) {
            return ResultUtil.error("WMS货件批次号不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getCaseNumber())) {
            return ResultUtil.error("箱号不能为空");
        }
     /*    if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getLogisticsNumber())) {
            return ResultUtil.error("物流单号不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getChargeLogisticsNumber())) {
            return ResultUtil.error("收费物流单号不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getEstimatedLogisticsMode())) {
            return ResultUtil.error("预计物流方式不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getPracticalLogisticsMode())) {
            return ResultUtil.error("实际物流方式不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getCarrier())) {
            return ResultUtil.error("承运商不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getCode())) {
            return ResultUtil.error("code不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getPosition())) {
            return ResultUtil.error("仓位不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getLongColumn()) {
            return ResultUtil.error("长不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getWidth()) {
            return ResultUtil.error("宽不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getHeight()) {
            return ResultUtil.error("高不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getActualWeight()) {
            return ResultUtil.error("实际重不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getVolumeWeight()) {
            return ResultUtil.error("体积重不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getAmountDeclared()) {
            return ResultUtil.error("申报金额不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getEstimatedTax()) {
            return ResultUtil.error("预估税金不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getOtherExpenses()) {
            return ResultUtil.error("其他费用不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getExpectedFreight()) {
            return ResultUtil.error("预计运费不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getEstimatedDeliveryTime())) {
            return ResultUtil.error("预计发货时间不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getActualDepartureTime())) {
            return ResultUtil.error("实际发货时间不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getPredictedDetectionTime())) {
            return ResultUtil.error("预计检出时间不能为空");
        }*/
        GoodsCorrespondenceLogisticsInfo oneInfoByCaseNumber = mapper.getOneInfoByCaseNumber(goodsCorrespondenceLogisticsInfo.getCaseNumber());
        if (null != oneInfoByCaseNumber && null != oneInfoByCaseNumber.getId()) {
            return ResultUtil.error("该箱号数据存在");
        }
        baseInsert(goodsCorrespondenceLogisticsInfo);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 修改
     *
     * @param goodsCorrespondenceLogisticsInfo
     * @return
     */
    public ResultUtil update(GoodsCorrespondenceLogisticsInfo goodsCorrespondenceLogisticsInfo) {
        if(StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getShop())){
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getArea())){
            return ResultUtil.error("区域不能为空");
        }

        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getDate())) {
            return ResultUtil.error("建计划日期不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getShipmentId())) {
            return ResultUtil.error("shipmentId不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getWmsGoodsNumber())) {
            return ResultUtil.error("WMS货件批次号不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getCaseNumber())) {
            return ResultUtil.error("箱号不能为空");
        }
       /* if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getLogisticsNumber())) {
            return ResultUtil.error("物流单号不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getChargeLogisticsNumber())) {
            return ResultUtil.error("收费物流单号不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getEstimatedLogisticsMode())) {
            return ResultUtil.error("预计物流方式不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getPracticalLogisticsMode())) {
            return ResultUtil.error("实际物流方式不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getCarrier())) {
            return ResultUtil.error("承运商不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getCode())) {
            return ResultUtil.error("code不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getPosition())) {
            return ResultUtil.error("仓位不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getLongColumn()) {
            return ResultUtil.error("长不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getWidth()) {
            return ResultUtil.error("宽不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getHeight()) {
            return ResultUtil.error("高不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getActualWeight()) {
            return ResultUtil.error("实际重不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getVolumeWeight()) {
            return ResultUtil.error("体积重不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getAmountDeclared()) {
            return ResultUtil.error("申报金额不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getEstimatedTax()) {
            return ResultUtil.error("预估税金不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getOtherExpenses()) {
            return ResultUtil.error("其他费用不能为空");
        }
        if (null == goodsCorrespondenceLogisticsInfo.getExpectedFreight()) {
            return ResultUtil.error("预计运费不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getEstimatedDeliveryTime())) {
            return ResultUtil.error("预计发货时间不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getActualDepartureTime())) {
            return ResultUtil.error("实际发货时间不能为空");
        }
        if (StringUtil.isBlank(goodsCorrespondenceLogisticsInfo.getPredictedDetectionTime())) {
            return ResultUtil.error("预计检出时间不能为空");
        }*/
        GoodsCorrespondenceLogisticsInfo oneInfoByCaseNumber = mapper.getOneInfoByCaseNumber(goodsCorrespondenceLogisticsInfo.getCaseNumber());
        if (null != oneInfoByCaseNumber && null != oneInfoByCaseNumber.getId() && !oneInfoByCaseNumber.getId().equals(goodsCorrespondenceLogisticsInfo.getId())) {
            return ResultUtil.error("该箱号数据存在");
        }
        baseUpdate(goodsCorrespondenceLogisticsInfo);
       // RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
        baseDeleteByClassAndIds(GoodsCorrespondenceLogisticsInfo.class, ids);
      //  RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
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
            int excelTitleNum = 24;
            int number = sheet.getRow(0).getPhysicalNumberOfCells();
            if (excelTitleNum != number) {
                return ResultUtil.error("标准格式为二十五列，请检查文件后重试");
            }
            //默认第一行为标题跳过
            List<Object> insertList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                GoodsCorrespondenceLogisticsInfo goodsCorrespondenceLogisticsInfo = new GoodsCorrespondenceLogisticsInfo();
                Row row = sheet.getRow(j);
                Cell dateCell = row.getCell(0);
                Cell shipmentIdCell = row.getCell(1);
                Cell wmsGoodsNumberCell = row.getCell(2);
                Cell caseNumberCell = row.getCell(3);
                Cell logNumberCell = row.getCell(4);
                Cell chargeLogNumberCell = row.getCell(5);
                Cell estimatedLogModeCell = row.getCell(6);
                Cell practicalLogModeCell = row.getCell(7);
                Cell carrierCell = row.getCell(8);
                Cell codeCell = row.getCell(9);
                Cell longCell = row.getCell(10);
                Cell widthCell = row.getCell(11);
                Cell heightCell = row.getCell(12);
                Cell actualWeightCell = row.getCell(13);
                Cell volumeWeightCell = row.getCell(14);
                Cell amountDeclaredCell = row.getCell(15);
                Cell estimatedTaxCell = row.getCell(16);
                Cell otherExpensesCell = row.getCell(17);
                Cell expectedFreightCell = row.getCell(18);
                Cell estimatedDeliveryTimeCell = row.getCell(19);
                Cell actualDepartureTimeCell = row.getCell(20);
                Cell predictedDetectionTimeCell = row.getCell(21);
                Cell shopCell = row.getCell(22);
                Cell areaCell = row.getCell(23);
                if (null == dateCell || null == shipmentIdCell || null == wmsGoodsNumberCell || null == caseNumberCell || null == shopCell
                        || null == areaCell) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }

                if(!StringUtils.isEmpty(shipmentIdCell)){
                    shipmentIdCell.setCellType(CellType.STRING);
                }

                if(!StringUtils.isEmpty(wmsGoodsNumberCell)){
                    wmsGoodsNumberCell.setCellType(CellType.STRING);
                }

                if(!StringUtils.isEmpty(caseNumberCell)){
                    caseNumberCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(logNumberCell)){
                    logNumberCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(chargeLogNumberCell)){
                    chargeLogNumberCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(estimatedLogModeCell)){
                    estimatedLogModeCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(practicalLogModeCell)){
                    practicalLogModeCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(carrierCell)){
                    carrierCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(codeCell)){
                    codeCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(shopCell)){
                    shopCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(areaCell)){
                    areaCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(longCell)){
                    longCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(widthCell)){
                    widthCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(heightCell)){
                    heightCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(actualWeightCell)){
                    actualWeightCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(volumeWeightCell)){
                    volumeWeightCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(amountDeclaredCell)){
                    amountDeclaredCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(estimatedTaxCell)){
                    estimatedTaxCell.setCellType(CellType.STRING);

                }
                if(!StringUtils.isEmpty(expectedFreightCell)){
                    expectedFreightCell.setCellType(CellType.STRING);

                }

                String dateValue = null;
                if (DateUtil.isCellDateFormatted(dateCell)) {
                    Date date = dateCell.getDateCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateValue = dateFormat.format(date);
                }
                String estimatedDeliveryTimeValue = null;
                if (StringUtils.isEmpty(estimatedDeliveryTimeCell)&&DateUtil.isCellDateFormatted(estimatedDeliveryTimeCell)) {
                    Date date = estimatedDeliveryTimeCell.getDateCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    estimatedDeliveryTimeValue = dateFormat.format(date);
                }
                String actualDepartureTimeValue = null;
                if (StringUtils.isEmpty(actualDepartureTimeCell)&&DateUtil.isCellDateFormatted(actualDepartureTimeCell)) {
                    Date date = actualDepartureTimeCell.getDateCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    actualDepartureTimeValue = dateFormat.format(date);
                }
                String predictedDetectionTimeValue = null;
                if (StringUtils.isEmpty(predictedDetectionTimeCell)&&DateUtil.isCellDateFormatted(predictedDetectionTimeCell)) {
                    Date date = predictedDetectionTimeCell.getDateCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    predictedDetectionTimeValue = dateFormat.format(date);
                }
                String otherExpensesValue = null;
                if (null != otherExpensesCell) {
                    otherExpensesCell.setCellType(CellType.STRING);
                    otherExpensesValue = otherExpensesCell.getStringCellValue();
                }
                String shipmentIdValue = StringUtils.isEmpty(shipmentIdCell)?null:shipmentIdCell.getStringCellValue();


                String wmsGoodsNumberValue = StringUtils.isEmpty(wmsGoodsNumberCell)?null: wmsGoodsNumberCell.getStringCellValue();
                String caseNumberValue = StringUtils.isEmpty(caseNumberCell)?null:caseNumberCell.getStringCellValue();
                String logNumberValue = StringUtils.isEmpty(logNumberCell)?null:logNumberCell.getStringCellValue();
                String chargeLogNumberValue = StringUtils.isEmpty(chargeLogNumberCell)?null:chargeLogNumberCell.getStringCellValue();
                String estimatedLogModeValue = StringUtils.isEmpty(estimatedLogModeCell)?null:estimatedLogModeCell.getStringCellValue();
                String practicalLogModeValue = StringUtils.isEmpty(practicalLogModeCell)?null:practicalLogModeCell.getStringCellValue();
                String carrierValue = StringUtils.isEmpty(carrierCell)?null:carrierCell.getStringCellValue();
                String codeValue = StringUtils.isEmpty(codeCell)?null:codeCell.getStringCellValue();
                String shopValue = StringUtils.isEmpty(shopCell)?null:shopCell.getStringCellValue();
                String areaValue = StringUtils.isEmpty(areaCell)?null:areaCell.getStringCellValue();
                String longValue = StringUtils.isEmpty(longCell)?null:longCell.getStringCellValue();
                String widthValue = StringUtils.isEmpty(widthCell)?null:widthCell.getStringCellValue();
                String heightValue = StringUtils.isEmpty(heightCell)?null:heightCell.getStringCellValue();
                String actualWeightValue = StringUtils.isEmpty(actualWeightCell)?null:actualWeightCell.getStringCellValue();
                String volumeWeightValue = StringUtils.isEmpty(volumeWeightCell)?null:volumeWeightCell.getStringCellValue();
                String amountDeclaredValue = StringUtils.isEmpty(amountDeclaredCell)?null:amountDeclaredCell.getStringCellValue();
                String estimatedTaxValue = StringUtils.isEmpty(estimatedTaxCell)?null:estimatedTaxCell.getStringCellValue();
                String expectedFreightValue = StringUtils.isEmpty(expectedFreightCell)?null:expectedFreightCell.getStringCellValue();
                if (StringUtil.isBlank(dateValue) || StringUtil.isBlank(shipmentIdValue) || StringUtil.isBlank(wmsGoodsNumberValue)
                        || StringUtil.isBlank(caseNumberValue)
                        || StringUtil.isBlank(shopValue)||StringUtil.isBlank(areaValue) ) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return ResultUtil.error("导入失败,第" + j + "行文件有空数据，请补充完整后重试");
                }
                goodsCorrespondenceLogisticsInfo.setDate(dateValue);
                goodsCorrespondenceLogisticsInfo.setShipmentId(shipmentIdValue);
                goodsCorrespondenceLogisticsInfo.setWmsGoodsNumber(wmsGoodsNumberValue);
                goodsCorrespondenceLogisticsInfo.setCaseNumber(caseNumberValue);
                goodsCorrespondenceLogisticsInfo.setLogisticsNumber(logNumberValue);
                goodsCorrespondenceLogisticsInfo.setChargeLogisticsNumber(chargeLogNumberValue);
                goodsCorrespondenceLogisticsInfo.setEstimatedLogisticsMode(estimatedLogModeValue);
                goodsCorrespondenceLogisticsInfo.setPracticalLogisticsMode(practicalLogModeValue);
                goodsCorrespondenceLogisticsInfo.setCarrier(carrierValue);
                goodsCorrespondenceLogisticsInfo.setCode(codeValue);
                goodsCorrespondenceLogisticsInfo.setArea(areaValue);
                goodsCorrespondenceLogisticsInfo.setShop(shopValue);
                goodsCorrespondenceLogisticsInfo.setLongColumn(StringUtils.isEmpty(longValue)?null:Double.valueOf(longValue));
                goodsCorrespondenceLogisticsInfo.setWidth(StringUtils.isEmpty(widthValue)?null:Double.valueOf(widthValue));
                goodsCorrespondenceLogisticsInfo.setHeight(StringUtils.isEmpty(heightValue)?null:Double.valueOf(heightValue));
                goodsCorrespondenceLogisticsInfo.setActualWeight(StringUtils.isEmpty(actualWeightValue)?null:Double.valueOf(actualWeightValue));
                goodsCorrespondenceLogisticsInfo.setVolumeWeight(StringUtils.isEmpty(volumeWeightValue)?null:Double.valueOf(volumeWeightValue));
                goodsCorrespondenceLogisticsInfo.setAmountDeclared(StringUtils.isEmpty(amountDeclaredValue)?null:Double.valueOf(amountDeclaredValue));
                goodsCorrespondenceLogisticsInfo.setEstimatedTax(StringUtils.isEmpty(estimatedTaxValue)?null:Double.valueOf(estimatedTaxValue));
                if (null != otherExpensesValue) {
                    goodsCorrespondenceLogisticsInfo.setOtherExpenses(StringUtils.isEmpty(otherExpensesValue)?null:Double.valueOf(otherExpensesValue));
                }
                goodsCorrespondenceLogisticsInfo.setExpectedFreight(StringUtils.isEmpty(expectedFreightValue)?null:Double.valueOf(expectedFreightValue));
                goodsCorrespondenceLogisticsInfo.setEstimatedDeliveryTime(estimatedDeliveryTimeValue);
                goodsCorrespondenceLogisticsInfo.setActualDepartureTime(actualDepartureTimeValue);
                goodsCorrespondenceLogisticsInfo.setPredictedDetectionTime(predictedDetectionTimeValue);
                GoodsCorrespondenceLogisticsInfo oneInfoByCaseNumber = mapper.getOneInfoByCaseNumber(caseNumberValue);
                if (null != oneInfoByCaseNumber && null != oneInfoByCaseNumber.getId()) {
                    goodsCorrespondenceLogisticsInfo.setId(oneInfoByCaseNumber.getId());
                    baseUpdate(goodsCorrespondenceLogisticsInfo);
                } else {
                    goodsCorrespondenceLogisticsInfo.setId(null);
                    insertList.add(goodsCorrespondenceLogisticsInfo);
                }
            }
            baseInsertList(insertList);
        } catch (IOException e) {
            log.warn("货件对应物流信息出错" + e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("文件读取失败,请检查文件");
        }
      //  RedisUtil.deleteLikeKey(BASE_REDIS_KEY);
        return ResultUtil.success();
    }

    /**
     * 模板下载
     *
     * @param response
     */
    public void downTemplate(HttpServletResponse response) {
        String path = "public/template/货件对应物流信息.xlsx";
        FileUtil.downResourceFile(response, path);
    }
}
