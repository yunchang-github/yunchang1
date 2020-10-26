package com.weiziplus.springboot.mapper.caravan;

import com.weiziplus.springboot.models.AccountData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/9/6 10:39
 */
@Mapper
public interface AccountDataMapper {

    /**
     * 根据参数获取一条数据
     *
     * @param accountId
     * @param orderId
     * @param modifyTime
     * @return
     */
    AccountData getOneInfoByAccountIdAndOrderIdAndModifyTime(@Param("accountId") String accountId, @Param("orderId") String orderId
            , @Param("modifyTime") String modifyTime);
}
