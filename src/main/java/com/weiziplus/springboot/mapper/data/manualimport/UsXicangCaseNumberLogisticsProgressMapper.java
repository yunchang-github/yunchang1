package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.UsXicangCaseNumberLogisticsProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wanglongwei
 * @data 2019/7/29 15:14
 */
@Mapper
public interface UsXicangCaseNumberLogisticsProgressMapper {

    /**
     * 根据箱号获取一条数据
     *
     * @param number
     * @return
     */
    UsXicangCaseNumberLogisticsProgress getOneInfoByCaseNumber(@Param("number") String number);
}
