package com.ptm.service;

import com.ptm.mapper.TimeEntryMapper;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private TimeEntryMapper timeEntryMapper;

    @InjectMocks
    private StatisticsService statisticsService;

    private List<Map<String, Object>> employeeStatsData;
    private List<Map<String, Object>> projectStatsData;

    @BeforeEach
    void setUp() {
        employeeStatsData = new ArrayList<>();

        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("user_name", "张三");
        emp1.put("dept", "技术部");
        emp1.put("total_hours", 168.0);
        emp1.put("entry_count", 21);
        employeeStatsData.add(emp1);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("user_name", "李四");
        emp2.put("dept", "产品部");
        emp2.put("total_hours", 120.5);
        emp2.put("entry_count", 15);
        employeeStatsData.add(emp2);

        projectStatsData = new ArrayList<>();

        Map<String, Object> proj1 = new HashMap<>();
        proj1.put("project_name", "项目A");
        proj1.put("total_hours", 560.0);
        proj1.put("member_count", 5);
        projectStatsData.add(proj1);

        Map<String, Object> proj2 = new HashMap<>();
        proj2.put("project_name", "项目B");
        proj2.put("total_hours", 320.0);
        proj2.put("member_count", 3);
        projectStatsData.add(proj2);
    }

    @Nested
    @DisplayName("员工统计测试")
    class EmployeeStatsTest {

        @Test
        @DisplayName("获取员工工时统计 - 返回列表")
        void getEmployeeStats_success() {
            when(timeEntryMapper.employeeStats(2026, 7, null)).thenReturn(employeeStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getEmployeeStats(2026, 7, null);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());

            Map<String, Object> first = result.getData().get(0);
            assertEquals("张三", first.get("user_name"));
            assertEquals(168.0, (Double) first.get("total_hours"), 0.001);
        }

        @Test
        @DisplayName("按姓名过滤员工统计")
        void getEmployeeStats_withNameFilter() {
            List<Map<String, Object>> filtered = new ArrayList<>();
            Map<String, Object> emp = new HashMap<>();
            emp.put("user_name", "张三");
            emp.put("total_hours", 168.0);
            filtered.add(emp);

            when(timeEntryMapper.employeeStats(2026, 7, "张三")).thenReturn(filtered);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getEmployeeStats(2026, 7, "张三");

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().size());
            assertEquals("张三", result.getData().get(0).get("user_name"));
        }

        @Test
        @DisplayName("员工统计结果为空 - 返回空列表")
        void getEmployeeStats_empty() {
            when(timeEntryMapper.employeeStats(2026, 8, "不存在")).thenReturn(new ArrayList<>());

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getEmployeeStats(2026, 8, "不存在");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }

        @Test
        @DisplayName("员工统计 - name参数为null时调用正确")
        void getEmployeeStats_nameIsNull() {
            when(timeEntryMapper.employeeStats(2026, 6, null)).thenReturn(employeeStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getEmployeeStats(2026, 6, null);

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().size());
            verify(timeEntryMapper).employeeStats(2026, 6, null);
        }

        @Test
        @DisplayName("员工统计 - name参数为空字符串")
        void getEmployeeStats_nameIsEmpty() {
            when(timeEntryMapper.employeeStats(2026, 6, "")).thenReturn(employeeStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getEmployeeStats(2026, 6, "");

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().size());
            verify(timeEntryMapper).employeeStats(2026, 6, "");
        }

        @Test
        @DisplayName("员工统计 - 跨年查询正常")
        void getEmployeeStats_crossYear() {
            when(timeEntryMapper.employeeStats(2025, 1, null)).thenReturn(employeeStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getEmployeeStats(2025, 1, null);

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().size());
            verify(timeEntryMapper).employeeStats(2025, 1, null);
        }
    }

    @Nested
    @DisplayName("项目统计测试")
    class ProjectStatsTest {

        @Test
        @DisplayName("获取项目工时统计 - 返回列表")
        void getProjectStats_success() {
            when(timeEntryMapper.projectStats(2026, 7, null)).thenReturn(projectStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getProjectStats(2026, 7, null);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());

            Map<String, Object> first = result.getData().get(0);
            assertEquals("项目A", first.get("project_name"));
            assertEquals(560.0, (Double) first.get("total_hours"), 0.001);
        }

        @Test
        @DisplayName("按项目名过滤统计")
        void getProjectStats_withNameFilter() {
            List<Map<String, Object>> filtered = new ArrayList<>();
            Map<String, Object> proj = new HashMap<>();
            proj.put("project_name", "项目A");
            proj.put("total_hours", 560.0);
            filtered.add(proj);

            when(timeEntryMapper.projectStats(2026, 7, "项目A")).thenReturn(filtered);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getProjectStats(2026, 7, "项目A");

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().size());
            assertEquals("项目A", result.getData().get(0).get("project_name"));
        }

        @Test
        @DisplayName("项目统计结果为空 - 返回空列表")
        void getProjectStats_empty() {
            when(timeEntryMapper.projectStats(2026, 8, "不存在")).thenReturn(new ArrayList<>());

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getProjectStats(2026, 8, "不存在");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }

        @Test
        @DisplayName("项目统计 - name参数为null时调用正确")
        void getProjectStats_nameIsNull() {
            when(timeEntryMapper.projectStats(2026, 6, null)).thenReturn(projectStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getProjectStats(2026, 6, null);

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().size());
            verify(timeEntryMapper).projectStats(2026, 6, null);
        }

        @Test
        @DisplayName("项目统计 - 不同年月查询")
        void getProjectStats_differentMonth() {
            when(timeEntryMapper.projectStats(2026, 12, null)).thenReturn(projectStatsData);

            ResponseResult<List<Map<String, Object>>> result = statisticsService.getProjectStats(2026, 12, null);

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().size());
            verify(timeEntryMapper).projectStats(2026, 12, null);
        }
    }
}
