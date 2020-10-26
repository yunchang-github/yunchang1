package com.weiziplus.springboot.mapper.data.manualimport;

import com.weiziplus.springboot.models.OverseasWarehousePlanInformationRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/7/29 11:26
 */
@Mapper
public interface OverseasWarehousePlanInformationRegistrationMapper {

    /**
     * 根据放置箱号获取一条数据
     *
     * @param number
     * @return
     */
    OverseasWarehousePlanInformationRegistration getOneInfoByBoxNumber(@Param("number") String number);

    List<OverseasWarehousePlanInformationRegistration> getPageList(Map map);
}
