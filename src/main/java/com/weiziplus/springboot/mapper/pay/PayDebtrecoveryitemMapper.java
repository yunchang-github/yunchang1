package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayDebtrecoveryitem;

public interface PayDebtrecoveryitemMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayDebtrecoveryitem record);

    int insertSelective(PayDebtrecoveryitem record);

    PayDebtrecoveryitem selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayDebtrecoveryitem record);

    int updateByPrimaryKey(PayDebtrecoveryitem record);
}