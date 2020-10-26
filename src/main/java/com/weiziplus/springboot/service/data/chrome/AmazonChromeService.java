package com.weiziplus.springboot.service.data.chrome;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.models.SponsoredProductsSearchTermReport;
import com.weiziplus.springboot.utils.ResultUtil;
import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.amazon.AmazonExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wanglongwei
 * @data 2019/8/9 10:21
 */
@Slf4j
@Service
public class AmazonChromeService extends BaseService {

    /**
     * 搜索词(search term)===商品推广自动投放报告
     *
     * @param url
     * @param shop
     * @param area
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil searchTerm(String[] url, String shop, String area) {
        if (null == url || StringUtil.isBlank(url[0])) {
            return ResultUtil.error("url不能为空");
        }
        if (StringUtil.isBlank(shop)) {
            return ResultUtil.error("店铺不能为空");
        }
        if (StringUtil.isBlank(area)) {
            return ResultUtil.error("区域不能为空");
        }
        log.info("***********处理------搜索词(search term)===商品推广自动投放报告-----数据开始**********");
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        int maxErrorNum = 5;
        for (int i = 0; i < maxErrorNum; i++) {
            Sheet excelSheet = AmazonExcelUtil.getExcelSheet(url[0]);
            if (null == excelSheet) {
                if (i >= (maxErrorNum - 1)) {
                    log.warn("***********处理------搜索词(search term)===商品推广自动投放报告-----数据获取失败**********");
                    return ResultUtil.error("数据获取失败");
                }
                continue;
            }
            try {
                //一共有多少列
                int physicalNumberOfCells = excelSheet.getRow(0).getPhysicalNumberOfCells();
                //默认第一行标题跳过
                SponsoredProductsSearchTermReport report = new SponsoredProductsSearchTermReport();
                report.setShop(shop);
                report.setArea(area);
                for (int j = 1; j < excelSheet.getPhysicalNumberOfRows(); j++) {
                    Row row = excelSheet.getRow(j);
                    for (int k = 0; k < physicalNumberOfCells; k++) {
                        Cell cell = row.getCell(k);
                        String stringCellValue = null;
                        if (null != cell) {
                            if (0 == k && DateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                stringCellValue = dateFormat.format(date);
                            } else {
                                //数字类型转为string后再处理
                                if (k > 7) {
                                    cell.setCellType(CellType.STRING);
                                }
                                stringCellValue = cell.getStringCellValue();
                            }
                        }
                        switch (k) {
                            case 0: {
                                report.setDate(stringCellValue);
                            }
                            break;
                            case 1: {
                                report.setPortfolioName(String.valueOf(cell));
                            }
                            break;
                            case 2: {
                                report.setCurrency(stringCellValue);
                            }
                            break;
                            case 3: {
                                report.setCampaignName(stringCellValue);
                            }
                            break;
                            case 4: {
                                report.setAdGroupName(stringCellValue);
                            }
                            break;
                            case 5: {
                                report.setTargeting(stringCellValue);
                            }
                            break;
                            case 6: {
                                report.setMatchType(stringCellValue);
                            }
                            break;
                            case 7: {
                                report.setCustomerSearchTerm(stringCellValue);
                            }
                            break;
                            case 8: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setImpressions(null);
                                } else {
                                    int index = stringCellValue.indexOf(".");
                                    report.setImpressions(Integer.valueOf(stringCellValue.substring(0, index)));
                                }
                            }
                            break;
                            case 9: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setClicks(null);
                                } else {
                                    int index = stringCellValue.indexOf(".");
                                    report.setClicks(Integer.valueOf(stringCellValue.substring(0, index)));
                                }
                            }
                            break;
                            case 10: {
                                report.setClickThruRate(stringCellValue);
                            }
                            break;
                            case 11: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setCostPerClick(null);
                                } else {
                                    report.setCostPerClick(Double.valueOf(stringCellValue));
                                }
                            }
                            break;
                            case 12: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSpend(null);
                                } else {
                                    report.setSpend(Double.valueOf(stringCellValue));
                                }
                            }
                            break;
                            case 13: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayTotalSales(null);
                                } else {
                                    report.setSevenDayTotalSales(Double.valueOf(stringCellValue));
                                }
                            }
                            break;
                            case 14: {
                                report.setTotalAdvertisingCostOfSales(stringCellValue);
                            }
                            break;
                            case 15: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setTotalReturnOnAdvertisingSpend(null);
                                } else {
                                    report.setTotalReturnOnAdvertisingSpend(Double.valueOf(stringCellValue));
                                }
                            }
                            break;
                            case 16: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayTotalOrders(null);
                                } else {
                                    int index = stringCellValue.indexOf(".");
                                    report.setSevenDayTotalOrders(Integer.valueOf(stringCellValue.substring(0, index)));
                                }
                            }
                            break;
                            case 17: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayTotalUnits(null);
                                } else {
                                    int index = stringCellValue.indexOf(".");
                                    report.setSevenDayTotalUnits(Integer.valueOf(stringCellValue.substring(0, index)));
                                }
                            }
                            break;
                            case 18: {
                                report.setSevenDayConversionRate(stringCellValue);
                            }
                            break;
                            case 19: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayAdvertisedSkuUnits(null);
                                } else {
                                    int index = stringCellValue.indexOf(".");
                                    report.setSevenDayAdvertisedSkuUnits(Integer.valueOf(stringCellValue.substring(0, index)));
                                }
                            }
                            break;
                            case 20: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayOtherSkuUnits(null);
                                } else {
                                    int index = stringCellValue.indexOf(".");
                                    report.setSevenDayOtherSkuUnits(Integer.valueOf(stringCellValue.substring(0, index)));
                                }
                            }
                            break;
                            case 21: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayAdvertisedSkuSales(null);
                                } else {
                                    report.setSevenDayAdvertisedSkuSales(Double.valueOf(stringCellValue));
                                }
                            }
                            break;
                            case 22: {
                                if (StringUtil.isBlank(stringCellValue)) {
                                    report.setSevenDayOtherSkuSales(null);
                                } else {
                                    report.setSevenDayOtherSkuSales(Double.valueOf(stringCellValue));
                                }
                            }
                            break;
                            default: {

                            }
                        }
                    }
                    baseInsert(report);
                }
                i = maxErrorNum;
            } catch (Exception e) {
                e.printStackTrace();
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                if (i < (maxErrorNum - 1)) {
                    continue;
                }
                log.warn("***********处理------搜索词(search term)===商品推广自动投放报告-----数据获取失败**********详情:" + e.getMessage());
                return ResultUtil.error("系统错误，请重试");
            }
        }
        log.info("***********处理------搜索词(search term)===商品推广自动投放报告-----数据结束**********");
        return ResultUtil.success();
    }

    public static void main(String[] args) {
        String a = "1.0";
        int index = a.indexOf(".");
        System.out.println(a.substring(0, index));
    }
}
