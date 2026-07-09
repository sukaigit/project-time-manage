package com.ptm.controller;

import com.ptm.service.DashboardService;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private DashboardService dashboardService;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());
    }

    @Test @DisplayName("获取仪表盘成功")
    void getDashboard() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("projectCount", 10L);
        data.put("taskCount", 25L);
        data.put("userCount", 50L);
        data.put("monthHours", 680.0);
        data.put("totalHours", 5600.0);
        data.put("top5", Collections.emptyList());
        data.put("recentActivities", Collections.emptyList());

        when(dashboardService.getDashboard()).thenReturn(ResponseResult.success(data));

        mockMvc.perform(get("/api/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.projectCount").value(10));
    }

    @Test @DisplayName("未登录 - 返回401")
    void withoutLogin() throws Exception {
        mockMvc.perform(get("/api/dashboard").session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }
}
