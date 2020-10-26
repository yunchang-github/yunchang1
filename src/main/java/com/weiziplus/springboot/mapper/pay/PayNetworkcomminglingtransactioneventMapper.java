package com.weiziplus.springboot.mapper.pay;

import com.weiziplus.springboot.models.PayNetworkcomminglingtransactionevent;

public interface PayNetworkcomminglingtransactioneventMapper {
    int deleteByPrimaryKey(String id);

    int insert(PayNetworkcomminglingtransactionevent record);

    int insertSelective(PayNetworkcomminglingtransactionevent record);

    PayNetworkcomminglingtransactionevent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PayNetworkcomminglingtransactionevent record);

    int updateByPrimaryKey(PayNetworkcomminglingtransactionevent record);
}