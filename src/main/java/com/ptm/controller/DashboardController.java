package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class DashboardController {

    @GetMapping
    public ResponseResult<Map<String, Object>> dashboard() {
        // TODO: 实现仪表盘数据统计
        Map<String, Object> data = new HashMap<>();

        // 项目统计
        Map<String, Object> projectStats = new HashMap<>();
        projectStats.put("total", 10);
        projectStats.put("active", 8);
        projectStats.put("completed", 2);
        data.put("projectStats", projectStats);

        // 今日工时
        Map<String, Object> todayHours = new HashMap<>();
        todayHours.put("total", 56.0);
        todayHours.put("submitted", 42.0);
        todayHours.put("pending", 14.0);
        data.put("todayHours", todayHours);

        // 待审批数量
        data.put("pendingApproval", 5);

        // 近期工时趋势（最近7天）
        List<Map<String, Object>> trend = new ArrayList<>();
        String[] days = {"01-08", "01-09", "01-10", "01-11", "01-12", "01-13", "01-14"};
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", days[i]);
            point.put("hours", 40 + i * 2);
            trend.add(point);
        }
        data.put("weeklyTrend", trend);

        return ResponseResult.success(data);
    }
}
