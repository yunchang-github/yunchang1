package com.weiziplus.springboot.controller.salesStatisticsReport;

import com.weiziplus.springboot.service.salesStatisticsReport.SalesStatisticsReportService;
import com.weiziplus.springboot.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pc")
public class SalesStatisticsReportController {
    @Autowired
    SalesStatisticsReportService salesStatisticsReportService;

    @GetMapping("/salesStatisticsReport")
    public ResultUtil getSalesStatisticsReport(){

        return salesStatisticsReportService.salesStatisticsReport();
    }
}
