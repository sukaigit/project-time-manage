package com.ptm.controller;

import com.ptm.entity.TimeEntry;
import com.ptm.service.TimeEntryService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeEntryController.class)
@AutoConfigureMockMvc(addFilters = false)
class TimeEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeEntryService timeEntryService;

    private MockHttpSession session;

    private TimeEntry sampleEntry;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());

        sampleEntry = new TimeEntry();
        sampleEntry.setId(1L);
        sampleEntry.setUserId(1L);
        sampleEntry.setProjectId(1L);
        sampleEntry.setTaskId(1L);
        sampleEntry.setWorkDate("2026-07-09");
        sampleEntry.setHours(8.0);
        sampleEntry.setContent("完成模块开发");
        sampleEntry.setStatus(0);
    }

    @Nested
    @DisplayName("分页查询工时列表 GET /api/time-entries")
    class ListTest {

        @Test
        @DisplayName("查询全部工时 - 带分页")
        void listAll() throws Exception {
            Map<String, Object> pageResult = new HashMap<>();
            pageResult.put("list", java.util.Collections.singletonList(sampleEntry));
            pageResult.put("total", 1L);
            pageResult.put("page", 1);
            pageResult.put("size", 10);

            when(timeEntryService.list(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(ResponseResult.success(pageResult));

            mockMvc.perform(get("/api/time-entries")
                            .param("page", "1")
                            .param("size", "10")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(1))
                    .andExpect(jsonPath("$.data.list[0].content").value("完成模块开发"));
        }

        @Test
        @DisplayName("按项目过滤查询")
        void listByProject() throws Exception {
            Map<String, Object> pageResult = new HashMap<>();
            pageResult.put("list", java.util.Collections.singletonList(sampleEntry));
            pageResult.put("total", 1L);

            when(timeEntryService.list(anyInt(), anyInt(), any(), eq(1L), any(), any(), any(), any()))
                    .thenReturn(ResponseResult.success(pageResult));

            mockMvc.perform(get("/api/time-entries")
                            .param("page", "1")
                            .param("size", "10")
                            .param("projectId", "1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("默认分页参数")
        void listDefaultPage() throws Exception {
            Map<String, Object> pageResult = new HashMap<>();
            pageResult.put("list", java.util.Collections.emptyList());
            pageResult.put("total", 0L);

            when(timeEntryService.list(eq(1), eq(10), any(), any(), any(), any(), any(), any()))
                    .thenReturn(ResponseResult.success(pageResult));

            mockMvc.perform(get("/api/time-entries")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("未登录查询 - 返回401")
        void listWithoutLogin() throws Exception {
            mockMvc.perform(get("/api/time-entries")
                            .param("page", "1")
                            .param("size", "10")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("创建工时 POST /api/time-entries")
    class CreateTest {

        @Test
        @DisplayName("创建工时成功")
        void createSuccess() throws Exception {
            when(timeEntryService.add(any(TimeEntry.class))).thenReturn(ResponseResult.success(null));

            mockMvc.perform(post("/api/time-entries")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"projectId\":1,\"taskId\":1,\"workDate\":\"2026-07-09\",\"hours\":8.0,\"content\":\"开发测试\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("工时为0 - 返回400")
        void createWithZeroHours() throws Exception {
            mockMvc.perform(post("/api/time-entries")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"projectId\":1,\"taskId\":1,\"workDate\":\"2026-07-09\",\"hours\":0,\"content\":\"开发\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("工时必须大于0"));
        }

        @Test
        @DisplayName("项目为空 - 返回400")
        void createWithoutProject() throws Exception {
            mockMvc.perform(post("/api/time-entries")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"projectId\":null,\"taskId\":1,\"workDate\":\"2026-07-09\",\"hours\":8.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("所属项目不能为空"));
        }

        @Test
        @DisplayName("未登录创建 - 返回401")
        void createWithoutLogin() throws Exception {
            mockMvc.perform(post("/api/time-entries")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"projectId\":1,\"taskId\":1,\"workDate\":\"2026-07-09\",\"hours\":8.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("更新工时 PUT /api/time-entries/{id}")
    class UpdateTest {

        @Test
        @DisplayName("更新工时成功")
        void updateSuccess() throws Exception {
            when(timeEntryService.update(any(TimeEntry.class))).thenReturn(ResponseResult.success(null));

            mockMvc.perform(put("/api/time-entries/1")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"hours\":6.0,\"content\":\"更新内容\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("未登录更新 - 返回401")
        void updateWithoutLogin() throws Exception {
            mockMvc.perform(put("/api/time-entries/1")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"hours\":6.0}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("删除工时 DELETE /api/time-entries/{id}")
    class DeleteTest {

        @Test
        @DisplayName("删除工时成功")
        void deleteSuccess() throws Exception {
            when(timeEntryService.delete(1L)).thenReturn(ResponseResult.success(null));

            mockMvc.perform(delete("/api/time-entries/1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("记录不存在 - 返回404")
        void deleteNotFound() throws Exception {
            when(timeEntryService.delete(999L)).thenReturn(ResponseResult.error(404, "工时记录不存在"));

            mockMvc.perform(delete("/api/time-entries/999")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.msg").value("工时记录不存在"));
        }

        @Test
        @DisplayName("未登录删除 - 返回401")
        void deleteWithoutLogin() throws Exception {
            mockMvc.perform(delete("/api/time-entries/1")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}
