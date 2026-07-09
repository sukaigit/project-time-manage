package com.ptm.service;

import com.ptm.mapper.ProjectMapper;
import com.ptm.mapper.TaskMapper;
import com.ptm.mapper.TimeEntryMapper;
import com.ptm.mapper.UserMapper;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TimeEntryMapper timeEntryMapper;

    @InjectMocks
    private DashboardService dashboardService;

    private List<Map<String, Object>> top5Employees;
    private List<Map<String, Object>> recentActivities;

    @BeforeEach
    void setUp() {
        top5Employees = new ArrayList<>();
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("user_name", "张三");
        emp1.put("total_hours", 160.0);
        top5Employees.add(emp1);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("user_name", "李四");
        emp2.put("total_hours", 120.0);
        top5Employees.add(emp2);

        recentActivities = new ArrayList<>();
        Map<String, Object> act1 = new HashMap<>();
        act1.put("user_name", "张三");
        act1.put("hours", 8.0);
        act1.put("content", "开发登录功能");
        recentActivities.add(act1);

        Map<String, Object> act2 = new HashMap<>();
        act2.put("user_name", "王五");
        act2.put("hours", 4.0);
        act2.put("content", "修改bug");
        recentActivities.add(act2);
    }

    @Nested
    @DisplayName("仪表盘数据测试")
    class GetDashboardTest {

        @Test
        @DisplayName("获取仪表盘完整数据 - 成功")
        void getDashboard_success() {
            LocalDate now = LocalDate.now();

            when(projectMapper.totalCount()).thenReturn(5L);
            when(taskMapper.totalCount()).thenReturn(20L);
            when(userMapper.totalCount()).thenReturn(10L);
            when(timeEntryMapper.sumApprovedHoursByMonth(now.getYear(), now.getMonthValue())).thenReturn(320.5);
            when(timeEntryMapper.sumApprovedHours()).thenReturn(5600.0);
            when(timeEntryMapper.top5Employees()).thenReturn(top5Employees);
            when(timeEntryMapper.recentEntries()).thenReturn(recentActivities);

            ResponseResult<Map<String, Object>> result = dashboardService.getDashboard();

            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertNotNull(data);

            assertEquals(5L, data.get("projectCount"));
            assertEquals(20L, data.get("taskCount"));
            assertEquals(10L, data.get("userCount"));
            assertEquals(320.5, (Double) data.get("monthHours"), 0.001);
            assertEquals(5600.0, (Double) data.get("totalHours"), 0.001);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> top5 = (List<Map<String, Object>>) data.get("top5");
            assertNotNull(top5);
            assertEquals(2, top5.size());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recent = (List<Map<String, Object>>) data.get("recentActivities");
            assertNotNull(recent);
            assertEquals(2, recent.size());
        }

        @Test
        @DisplayName("项目数量为0时数据正确")
        void getDashboard_zeroProjectCount() {
            LocalDate now = LocalDate.now();

            when(projectMapper.totalCount()).thenReturn(0L);
            when(taskMapper.totalCount()).thenReturn(0L);
            when(userMapper.totalCount()).thenReturn(0L);
            when(timeEntryMapper.sumApprovedHoursByMonth(now.getYear(), now.getMonthValue())).thenReturn(0.0);
            when(timeEntryMapper.sumApprovedHours()).thenReturn(0.0);
            when(timeEntryMapper.top5Employees()).thenReturn(new ArrayList<>());
            when(timeEntryMapper.recentEntries()).thenReturn(new ArrayList<>());

            ResponseResult<Map<String, Object>> result = dashboardService.getDashboard();

            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertEquals(0L, data.get("projectCount"));
            assertEquals(0L, data.get("taskCount"));
            assertEquals(0L, data.get("userCount"));
            assertEquals(0.0, (Double) data.get("monthHours"), 0.001);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> top5 = (List<Map<String, Object>>) data.get("top5");
            assertTrue(top5.isEmpty());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recent = (List<Map<String, Object>>) data.get("recentActivities");
            assertTrue(recent.isEmpty());
        }

        @Test
        @DisplayName("返回的数据包含所有7个字段")
        void getDashboard_containsAllFields() {
            LocalDate now = LocalDate.now();

            when(projectMapper.totalCount()).thenReturn(1L);
            when(taskMapper.totalCount()).thenReturn(1L);
            when(userMapper.totalCount()).thenReturn(1L);
            when(timeEntryMapper.sumApprovedHoursByMonth(now.getYear(), now.getMonthValue())).thenReturn(10.0);
            when(timeEntryMapper.sumApprovedHours()).thenReturn(100.0);
            when(timeEntryMapper.top5Employees()).thenReturn(top5Employees);
            when(timeEntryMapper.recentEntries()).thenReturn(recentActivities);

            ResponseResult<Map<String, Object>> result = dashboardService.getDashboard();

            Map<String, Object> data = result.getData();
            assertTrue(data.containsKey("projectCount"));
            assertTrue(data.containsKey("taskCount"));
            assertTrue(data.containsKey("userCount"));
            assertTrue(data.containsKey("monthHours"));
            assertTrue(data.containsKey("totalHours"));
            assertTrue(data.containsKey("top5"));
            assertTrue(data.containsKey("recentActivities"));
        }

        @Test
        @DisplayName("Top5员工列表顺序与Mapper返回一致")
        void getDashboard_top5OrderPreserved() {
            LocalDate now = LocalDate.now();

            when(projectMapper.totalCount()).thenReturn(1L);
            when(taskMapper.totalCount()).thenReturn(1L);
            when(userMapper.totalCount()).thenReturn(1L);
            when(timeEntryMapper.sumApprovedHoursByMonth(now.getYear(), now.getMonthValue())).thenReturn(10.0);
            when(timeEntryMapper.sumApprovedHours()).thenReturn(100.0);
            when(timeEntryMapper.top5Employees()).thenReturn(top5Employees);
            when(timeEntryMapper.recentEntries()).thenReturn(recentActivities);

            ResponseResult<Map<String, Object>> result = dashboardService.getDashboard();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> top5 = (List<Map<String, Object>>) result.getData().get("top5");
            assertEquals("张三", top5.get(0).get("user_name"));
            assertEquals("李四", top5.get(1).get("user_name"));
        }

        @Test
        @DisplayName("本月工时使用当前年月查询")
        void getDashboard_usesCurrentMonth() {
            LocalDate now = LocalDate.now();

            when(projectMapper.totalCount()).thenReturn(0L);
            when(taskMapper.totalCount()).thenReturn(0L);
            when(userMapper.totalCount()).thenReturn(0L);
            when(timeEntryMapper.sumApprovedHoursByMonth(now.getYear(), now.getMonthValue())).thenReturn(0.0);
            when(timeEntryMapper.sumApprovedHours()).thenReturn(0.0);
            when(timeEntryMapper.top5Employees()).thenReturn(new ArrayList<>());
            when(timeEntryMapper.recentEntries()).thenReturn(new ArrayList<>());

            dashboardService.getDashboard();

            verify(timeEntryMapper).sumApprovedHoursByMonth(now.getYear(), now.getMonthValue());
        }
    }
}
