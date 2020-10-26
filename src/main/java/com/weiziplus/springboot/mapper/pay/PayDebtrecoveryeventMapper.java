package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayDebtrecoveryevent;

public interface PayDebtrecoveryeventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayDebtrecoveryevent record);

    int insertSelective(PayDebtrecoveryevent record);

    PayDebtrecoveryevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayDebtrecoveryevent record);

    int updateByPrimaryKey(PayDebtrecoveryevent record);
}