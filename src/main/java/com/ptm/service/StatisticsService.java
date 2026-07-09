package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.User;
import com.ptm.mapper.TimeEntryMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private TimeEntryMapper timeEntryMapper;

    /**
     * 员工统计：按用户聚合已通过工时
     */
    public ResponseResult<List<Map<String, Object>>> getEmployeeStats(int year, int month, String name) {
        return ResponseResult.success(timeEntryMapper.employeeStats(year, month, name));
    }

    /**
     * 项目统计：按项目聚合已通过工时
     */
    public ResponseResult<List<Map<String, Object>>> getProjectStats(int year, int month, String name) {
        return ResponseResult.success(timeEntryMapper.projectStats(year, month, name));
    }
}
