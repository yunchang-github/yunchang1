package com.weiziplus.springboot.mapper.scheduled;

import com.weiziplus.springboot.models.RemovalOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/8/1 11:05
 */
@Mapper
public interface RemovalOrderDetailMapper {

    /**
     * 根据订单id、sku、fnsku、disposition获取一条数据
     *
     * @param orderId
     * @param sku
     * @param fnSku
     * @param disposition
     * @return
     */
    RemovalOrderDetail getOneInfoByOrderIdAndSkuAndFnSkuAndDisposition(@Param("orderId") String orderId, @Param("sku") String sku,
                                                                       @Param("fnsku") String fnSku, @Param("disposition") String disposition);
}
