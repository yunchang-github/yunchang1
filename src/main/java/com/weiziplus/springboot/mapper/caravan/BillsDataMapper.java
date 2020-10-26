package com.weiziplus.springboot.mapper.caravan;

import com.weiziplus.springboot.models.BillsData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/9/6 9:57
 */
@Mapper
public interface BillsDataMapper {

    /**
     * 根据payMentOrderId、orderNum、payTime获取一条数据
     *
     * @param payMentOrderId
     * @param orderNum
     * @param payTime
     * @return
     */
    BillsData getOneInfoByPayMentOrderIdAndOrderNumAndPayTime(@Param("payMentOrderId") String payMentOrderId
            , @Param("orderNum") String orderNum, @Param("payTime") String payTime);
}
