package com.weiziplus.springboot.service.caravan;

import com.weiziplus.springboot.base.BaseService;
import com.weiziplus.springboot.mapper.caravan.MskuMapper;
import com.weiziplus.springboot.mapper.caravan.PurchaseMapper;
import com.weiziplus.springboot.mapper.caravan.StockWarehouseDataMapper;
import com.weiziplus.springboot.scheduled.caravanapi.CaravanApiSchedule;
import com.weiziplus.springboot.utils.DateUtil;
import com.weiziplus.springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author wanglongwei
 * @data 2019/9/3 16:49
 */
@Slf4j
@Service
public class CaravanApiService extends BaseService {

    @Autowired
    CaravanApiSchedule caravanApiSchedule;

    @Autowired
    StockWarehouseDataMapper stockWarehouseDataMapper;

    @Autowired
    PurchaseMapper purchaseMapper;

    @Autowired
    MskuMapper mskuMapper;

    /**
     * 更新数据
     *
     * @param type
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil updateData(String type) {
        //创建事务还原点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        ResultUtil resultUtil = null;
        try {
            switch (type) {
                case "inventoryQuery": {
                    resultUtil = handleInventoryQuery();
                }
                break;
                case "purchase": {
                    resultUtil = handlePurchase();
                }
                break;
                case "msku": {
                    resultUtil = handleMsku();
                }
                break;
                default: {
                }
            }
        } catch (Exception e) {
            log.warn("马帮api，更新数据出错,类型:" + type + "，详情:" + e);
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ResultUtil.error("数据获取失败，请重试");
        }
        if (null == resultUtil) {
            return ResultUtil.error("类型错误，请重试");
        }
        //成功的状态码为200
        int successCode = 200;
        if (successCode == resultUtil.getCode()) {
            return ResultUtil.success();
        }
        //回滚事务
        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
        return ResultUtil.error(resultUtil.getMsg());
    }

    /**
     * 处理库存查询
     *
     * @return
     */
    private ResultUtil handleInventoryQuery() throws Exception {
        String nowTime = DateUtil.getFutureDateTime(0);
        String start = nowTime.substring(0, 10);
        //清除今天的数据
        stockWarehouseDataMapper.deleteByDate(start);
        boolean flag = caravanApiSchedule.baseHandleGetDate("stock-warehouse-data", "库存查询"
                , start + " 00:00:00", nowTime);
        if (!flag) {
            return ResultUtil.error("数据获取失败，请重试");
        }
        return ResultUtil.success();
    }

    /**
     * 处理采购单
     *
     * @return
     * @throws Exception
     */
    private ResultUtil handlePurchase() throws Exception {
        String nowTime = DateUtil.getFutureDateTime(0);
        String start = nowTime.substring(0, 10);
        //清除今天的数据
        purchaseMapper.deleteByDate(start);
        boolean flag = caravanApiSchedule.baseHandleGetDate("purchase-data", "采购单"
                , start + " 00:00:00", nowTime);
        if (!flag) {
            return ResultUtil.error("数据获取失败，请重试");
        }
        return ResultUtil.success();
    }

    /**
     * MSKU列表
     *
     * @return
     * @throws Exception
     */
    private ResultUtil handleMsku() throws Exception {
        String nowTime = DateUtil.getFutureDateTime(0);
        String start = nowTime.substring(0, 10);
        //清除今天的数据
        mskuMapper.deleteByDate(start);
        boolean flag = caravanApiSchedule.baseHandleGetDate("msku-data", "MSKU列表"
                , start + " 00:00:00", nowTime);
        if (!flag) {
            return ResultUtil.error("数据获取失败，请重试");
        }
        return ResultUtil.success();
    }
}
