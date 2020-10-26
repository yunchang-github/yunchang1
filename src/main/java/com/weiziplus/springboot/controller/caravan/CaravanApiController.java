package com.weiziplus.springboot.controller.caravan;

import com.weiziplus.springboot.service.caravan.CaravanApiService;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author wanglongwei
 * @data 2019/9/3 16:47
 */
@RestController
//@ApiIgnore
@RequestMapping("/pc/caravan")
public class CaravanApiController {

    @Autowired
    CaravanApiService service;

    /**
     * 更新数据
     *
     * @param type
     * @return
     */
    @PostMapping("/updateData")
    public ResultUtil updateData(String type) {
        return service.updateData(type);
    }
}
