package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class StatisticsController {

    @GetMapping("/employees")
    public ResponseResult<List<Map<String, Object>>> employeeStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name) {
        // TODO: 实现员工工时统计
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("userId", 1L);
        item.put("name", "张三");
        item.put("dept", "技术部");
        item.put("totalHours", 168.0);
        item.put("workDays", 21);
        list.add(item);
        return ResponseResult.success(list);
    }

    @GetMapping("/projects")
    public ResponseResult<List<Map<String, Object>>> projectStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name) {
        // TODO: 实现项目工时统计
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("projectId", 1L);
        item.put("projectName", "示例项目");
        item.put("totalHours", 560.0);
        item.put("memberCount", 5);
        list.add(item);
        return ResponseResult.success(list);
    }
}
