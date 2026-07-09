package com.ptm.controller;

import com.ptm.service.ApprovalService;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApprovalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApprovalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApprovalService approvalService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());
        session.setAttribute("userId", 1L);
    }

    @Nested
    @DisplayName("查询待审批列表 GET /api/approvals")
    class ListTest {
        @Test
        @DisplayName("查询待审批列表成功")
        void listSuccess() throws Exception {
            Map<String, Object> item = new HashMap<>();
            item.put("id", 1L);
            item.put("userName", "张三");
            item.put("hours", 8.0);

            when(approvalService.list()).thenReturn(ResponseResult.success(Arrays.asList(item)));

            mockMvc.perform(get("/api/approvals").session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].userName").value("张三"));
        }

        @Test
        @DisplayName("未登录查询 - 返回401")
        void listWithoutLogin() throws Exception {
            mockMvc.perform(get("/api/approvals").session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("审批通过 POST /api/approvals/{id}/approve")
    class ApproveTest {
        @Test
        @DisplayName("审批通过成功")
        void approveSuccess() throws Exception {
            when(approvalService.approve(eq(1L), anyLong())).thenReturn(ResponseResult.success(null));

            mockMvc.perform(post("/api/approvals/1/approve").session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("未登录审批 - 返回401")
        void approveWithoutLogin() throws Exception {
            mockMvc.perform(post("/api/approvals/1/approve").session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("驳回 POST /api/approvals/{id}/reject")
    class RejectTest {
        @Test
        @DisplayName("驳回成功")
        void rejectSuccess() throws Exception {
            when(approvalService.reject(eq(1L), anyLong(), eq("原因说明")))
                    .thenReturn(ResponseResult.success(null));

            mockMvc.perform(post("/api/approvals/1/reject")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"reason\":\"原因说明\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("驳回原因为空 - 返回400")
        void rejectEmptyReason() throws Exception {
            mockMvc.perform(post("/api/approvals/1/reject")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"reason\":\"\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("驳回原因不能为空"));
        }

        @Test
        @DisplayName("未登录驳回 - 返回401")
        void rejectWithoutLogin() throws Exception {
            mockMvc.perform(post("/api/approvals/1/reject")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"reason\":\"原因\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}
