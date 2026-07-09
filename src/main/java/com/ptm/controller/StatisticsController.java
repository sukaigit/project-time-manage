package com.ptm.controller;

import com.ptm.service.StatisticsService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/employees")
    public ResponseResult<List<Map<String, Object>>> employeeStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name) {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        return statisticsService.getEmployeeStats(year, month, name);
    }

    @GetMapping("/projects")
    public ResponseResult<List<Map<String, Object>>> projectStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name) {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        return statisticsService.getProjectStats(year, month, name);
    }
}
