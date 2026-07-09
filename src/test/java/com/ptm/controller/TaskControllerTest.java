package com.ptm.controller;

import com.ptm.entity.Task;
import com.ptm.service.TaskService;
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

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private MockHttpSession session;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());

        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setName("需求分析");
        sampleTask.setCode("T001");
        sampleTask.setProjectId(1L);
        sampleTask.setStatus(1);
        sampleTask.setCreateBy(1L);
    }

    @Nested
    @DisplayName("查询任务列表 GET /api/tasks")
    class ListTest {

        @Test
        @DisplayName("查询全部任务 - 返回200和数据")
        void listAll() throws Exception {
            when(taskService.list()).thenReturn(ResponseResult.success(Arrays.asList(sampleTask)));

            mockMvc.perform(get("/api/tasks")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].name").value("需求分析"))
                    .andExpect(jsonPath("$.data[0].code").value("T001"));
        }

        @Test
        @DisplayName("按项目ID过滤")
        void listByProject() throws Exception {
            when(taskService.listByProject(1L))
                    .thenReturn(ResponseResult.success(Arrays.asList(sampleTask)));

            mockMvc.perform(get("/api/tasks")
                            .param("projectId", "1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].projectId").value(1));
        }

        @Test
        @DisplayName("空列表 - 返回空数组")
        void listEmpty() throws Exception {
            when(taskService.list()).thenReturn(ResponseResult.success(Arrays.asList()));

            mockMvc.perform(get("/api/tasks")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("未登录查询 - 返回401")
        void listWithoutLogin() throws Exception {
            mockMvc.perform(get("/api/tasks")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("获取单个任务 GET /api/tasks/{id}")
    class GetByIdTest {

        @Test
        @DisplayName("根据ID获取任务成功")
        void getById() throws Exception {
            when(taskService.getById(1L)).thenReturn(ResponseResult.success(sampleTask));

            mockMvc.perform(get("/api/tasks/1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("需求分析"));
        }

        @Test
        @DisplayName("任务不存在 - 返回404")
        void getByIdNotFound() throws Exception {
            when(taskService.getById(999L)).thenReturn(ResponseResult.error(404, "任务不存在"));

            mockMvc.perform(get("/api/tasks/999")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.msg").value("任务不存在"));
        }
    }

    @Nested
    @DisplayName("创建任务 POST /api/tasks")
    class CreateTest {

        @Test
        @DisplayName("创建任务成功")
        void createSuccess() throws Exception {
            when(taskService.add(any(Task.class))).thenReturn(ResponseResult.success(null));

            mockMvc.perform(post("/api/tasks")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"编码实现\",\"code\":\"T002\",\"projectId\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("任务名称为空 - 返回400")
        void createWithEmptyName() throws Exception {
            mockMvc.perform(post("/api/tasks")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"code\":\"T002\",\"projectId\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("任务名称不能为空"));
        }

        @Test
        @DisplayName("项目ID为空 - 返回400")
        void createWithoutProjectId() throws Exception {
            mockMvc.perform(post("/api/tasks")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"编码实现\",\"code\":\"T002\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("所属项目不能为空"));
        }

        @Test
        @DisplayName("未登录创建 - 返回401")
        void createWithoutLogin() throws Exception {
            mockMvc.perform(post("/api/tasks")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"编码实现\",\"code\":\"T002\",\"projectId\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("更新任务 PUT /api/tasks/{id}")
    class UpdateTest {

        @Test
        @DisplayName("更新任务成功")
        void updateSuccess() throws Exception {
            when(taskService.update(any(Task.class))).thenReturn(ResponseResult.success(null));

            mockMvc.perform(put("/api/tasks/1")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"更新后的任务\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("未登录更新 - 返回401")
        void updateWithoutLogin() throws Exception {
            mockMvc.perform(put("/api/tasks/1")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"更新后的任务\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("删除任务 DELETE /api/tasks/{id}")
    class DeleteTest {

        @Test
        @DisplayName("删除任务成功")
        void deleteSuccess() throws Exception {
            when(taskService.delete(1L)).thenReturn(ResponseResult.success(null));

            mockMvc.perform(delete("/api/tasks/1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("任务不存在 - 返回404")
        void deleteNotFound() throws Exception {
            when(taskService.delete(999L)).thenReturn(ResponseResult.error(404, "任务不存在"));

            mockMvc.perform(delete("/api/tasks/999")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.msg").value("任务不存在"));
        }

        @Test
        @DisplayName("未登录删除 - 返回401")
        void deleteWithoutLogin() throws Exception {
            mockMvc.perform(delete("/api/tasks/1")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}
