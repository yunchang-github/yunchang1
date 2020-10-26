package com.weiziplus.springboot.mapper.caravan;

import com.weiziplus.springboot.models.OrderData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanglongwei
 * @data 2019/9/6 8:34
 */
@Mapper
public interface OrderDataMapper {

    /**
     * 根据platformOrderId获取一条数据
     *
     * @param platformOrderId
     * @return
     */
    OrderData getOneInfoByPlatformOrderId(@Param("platformOrderId") String platformOrderId);

    /**
     * 根据orderId删除order_data_item的数据
     *
     * @param orderIds
     * @return
     */
    int deleteOrderDataItemByOrderIds(@Param("orderIds") List<Long> orderIds);
}
