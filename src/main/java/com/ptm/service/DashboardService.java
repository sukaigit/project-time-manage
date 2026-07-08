package com.ptm.service;

import com.ptm.mapper.ProjectMapper;
import com.ptm.mapper.TaskMapper;
import com.ptm.mapper.TimeEntryMapper;
import com.ptm.mapper.UserMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TimeEntryMapper timeEntryMapper;

    /**
     * 获取仪表盘数据
     */
    public ResponseResult<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        long projectCount = projectMapper.totalCount();
        dashboard.put("projectCount", projectCount);

        long taskCount = taskMapper.totalCount();
        dashboard.put("taskCount", taskCount);

        long userCount = userMapper.totalCount();
        dashboard.put("userCount", userCount);

        LocalDate now = LocalDate.now();
        double monthHours = timeEntryMapper.sumApprovedHoursByMonth(now.getYear(), now.getMonthValue());
        dashboard.put("monthHours", monthHours);

        double totalHours = timeEntryMapper.sumApprovedHours();
        dashboard.put("totalHours", totalHours);

        List<Map<String, Object>> top5 = timeEntryMapper.top5Employees();
        dashboard.put("top5", top5);

        List<Map<String, Object>> recent = timeEntryMapper.recentEntries();
        dashboard.put("recentActivities", recent);

        return ResponseResult.success(dashboard);
    }
}
