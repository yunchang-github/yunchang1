package com.weiziplus.springboot.controller.amazon;

import com.weiziplus.springboot.models.DO.DetailPagesDO;
import com.weiziplus.springboot.service.shop.ShopService;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

@EnableAsync
@RestController
public class Sddacontroller {
    @Autowired
    ShopService shopService;


    @GetMapping("/ttt")
    public ResultUtil ttt(Long id) {
        return shopService.ttt(id);
    }

}
