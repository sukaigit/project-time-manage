package com.ptm.controller;

import com.ptm.service.StatisticsService;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());
    }

    @Nested
    @DisplayName("员工工时统计 GET /api/statistics/employees")
    class EmployeeStatsTest {
        @Test
        @DisplayName("查询员工统计成功")
        void getEmployeeStats() throws Exception {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "张三");
            item.put("dept", "技术部");
            item.put("totalHours", 168.0);

            when(statisticsService.getEmployeeStats(eq(2026), eq(7), any()))
                    .thenReturn(ResponseResult.success(Arrays.asList(item)));

            mockMvc.perform(get("/api/statistics/employees")
                            .param("year", "2026").param("month", "7")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].name").value("张三"));
        }

        @Test
        @DisplayName("未登录查询 - 返回401")
        void withoutLogin() throws Exception {
            mockMvc.perform(get("/api/statistics/employees")
                            .param("year", "2026").param("month", "7")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("项目工时统计 GET /api/statistics/projects")
    class ProjectStatsTest {
        @Test
        @DisplayName("查询项目统计成功")
        void getProjectStats() throws Exception {
            Map<String, Object> item = new HashMap<>();
            item.put("projectName", "ERP系统");
            item.put("totalHours", 560.0);

            when(statisticsService.getProjectStats(eq(2026), eq(7), any()))
                    .thenReturn(ResponseResult.success(Arrays.asList(item)));

            mockMvc.perform(get("/api/statistics/projects")
                            .param("year", "2026").param("month", "7")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].projectName").value("ERP系统"));
        }

        @Test
        @DisplayName("未登录查询 - 返回401")
        void withoutLogin() throws Exception {
            mockMvc.perform(get("/api/statistics/projects")
                            .param("year", "2026").param("month", "7")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}
