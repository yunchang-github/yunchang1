package com.weiziplus.springboot.mapper.caravan;

import com.weiziplus.springboot.models.PurchaseData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/9/3 17:11
 */
@Mapper
public interface PurchaseMapper {

    /**
     * 根据日期删除数据
     *
     * @param date
     * @return
     */
    int deleteByDate(@Param("date") String date);

    /**
     * 根据purchaseId获取一条数据
     *
     * @param purchaseId
     * @return
     */
    PurchaseData getOneInfoByPurchaseId(@Param("purchaseId") String purchaseId);

    /**
     * 根据purchaseDataIds删除item
     *
     * @param purchaseDataIds
     * @return
     */
    int deletePurchaseDataItemByPurchaseDataIds(@Param("purchaseDataIds") List<Long> purchaseDataIds);
}
