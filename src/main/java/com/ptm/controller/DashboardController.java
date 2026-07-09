package com.ptm.controller;

import com.ptm.service.DashboardService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseResult<Map<String, Object>> dashboard() {
        return dashboardService.getDashboard();
    }
}
