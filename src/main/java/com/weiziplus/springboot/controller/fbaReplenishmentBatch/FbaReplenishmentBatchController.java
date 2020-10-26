package com.weiziplus.springboot.controller.fbaReplenishmentBatch;

import com.weiziplus.springboot.service.fbaReplenishmentBatch.FbaReplenishmentBatchService;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pc/fbaReplenishmentBatch")
public class FbaReplenishmentBatchController {
    @Autowired
    FbaReplenishmentBatchService fbaReplenishmentBatchService;

    @GetMapping("/getData")
    public ResultUtil getFbaReplenishmentBatchData(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "replenishmentBatchNo", required = false) String replenishmentBatchNo,
                                                   @RequestParam(value = "localSku", required = false) String localSku){
        HashMap<String,Object> map = new HashMap<String, Object>();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        map.put("replenishmentBatchNo",replenishmentBatchNo);
        map.put("localSku",localSku);
        return fbaReplenishmentBatchService.getData(map);
    }
}
