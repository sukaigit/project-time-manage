package com.ptm.controller;

import com.ptm.entity.Project;
import com.ptm.entity.User;
import com.ptm.service.ProjectService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private MockHttpSession session;

    private Project sampleProject;
    private User sampleMember;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());

        sampleProject = new Project();
        sampleProject.setId(1L);
        sampleProject.setName("ERP 系统开发");
        sampleProject.setCode("P001");
        sampleProject.setDescription("企业资源计划系统");
        sampleProject.setDept("技术部");
        sampleProject.setStatus(1);
        sampleProject.setStartDate("2026-01-01");
        sampleProject.setEndDate("2026-12-31");
        sampleProject.setCreateBy(1L);

        sampleMember = new User();
        sampleMember.setId(2L);
        sampleMember.setUsername("zhangsan");
        sampleMember.setName("张三");
    }

    @Nested
    @DisplayName("查询项目列表 GET /api/projects")
    class ListTest {

        @Test
        @DisplayName("查询全部项目 - 返回200和数据")
        void listAll() throws Exception {
            when(projectService.list()).thenReturn(ResponseResult.success(Arrays.asList(sampleProject)));

            mockMvc.perform(get("/api/projects")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("ERP 系统开发"))
                    .andExpect(jsonPath("$.data[0].code").value("P001"));
        }

        @Test
        @DisplayName("按名称模糊查询")
        void listByName() throws Exception {
            when(projectService.list()).thenReturn(ResponseResult.success(Arrays.asList(sampleProject)));

            mockMvc.perform(get("/api/projects")
                            .param("name", "ERP")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].name").value("ERP 系统开发"));
        }

        @Test
        @DisplayName("未登录查询 - 返回401")
        void listWithoutLogin() throws Exception {
            mockMvc.perform(get("/api/projects")
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("获取单个项目 GET /api/projects/{id}")
    class GetByIdTest {

        @Test
        @DisplayName("根据ID获取项目成功")
        void getById() throws Exception {
            when(projectService.getById(1L)).thenReturn(ResponseResult.success(sampleProject));

            mockMvc.perform(get("/api/projects/1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("ERP 系统开发"));
        }

        @Test
        @DisplayName("项目不存在 - 返回404")
        void getByIdNotFound() throws Exception {
            when(projectService.getById(999L)).thenReturn(ResponseResult.error(404, "项目不存在"));

            mockMvc.perform(get("/api/projects/999")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.msg").value("项目不存在"));
        }
    }

    @Nested
    @DisplayName("创建项目 POST /api/projects")
    class CreateTest {

        @Test
        @DisplayName("创建项目成功")
        void createSuccess() throws Exception {
            when(projectService.add(any(Project.class))).thenReturn(ResponseResult.success(null));

            mockMvc.perform(post("/api/projects")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"新项目\",\"code\":\"P002\",\"dept\":\"技术部\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("名称为空 - 返回400")
        void createWithEmptyName() throws Exception {
            mockMvc.perform(post("/api/projects")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"code\":\"P002\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("项目名称不能为空"));
        }

        @Test
        @DisplayName("编码为空 - 返回400")
        void createWithEmptyCode() throws Exception {
            mockMvc.perform(post("/api/projects")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"新项目\",\"code\":\"\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("项目编码不能为空"));
        }

        @Test
        @DisplayName("未登录创建 - 返回401")
        void createWithoutLogin() throws Exception {
            mockMvc.perform(post("/api/projects")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"新项目\",\"code\":\"P002\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("更新项目 PUT /api/projects/{id}")
    class UpdateTest {

        @Test
        @DisplayName("更新项目成功")
        void updateSuccess() throws Exception {
            when(projectService.update(any(Project.class))).thenReturn(ResponseResult.success(null));

            mockMvc.perform(put("/api/projects/1")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"更新后的项目\",\"dept\":\"研发部\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("未登录更新 - 返回401")
        void updateWithoutLogin() throws Exception {
            mockMvc.perform(put("/api/projects/1")
                            .session(new MockHttpSession())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"更新后的项目\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("删除项目 DELETE /api/projects/{id}")
    class DeleteTest {

        @Test
        @DisplayName("删除项目成功")
        void deleteSuccess() throws Exception {
            when(projectService.delete(1L)).thenReturn(ResponseResult.success(null));

            mockMvc.perform(delete("/api/projects/1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("删除有未完成任务的项目 - 返回400")
        void deleteWithUnfinishedTasks() throws Exception {
            when(projectService.delete(1L)).thenReturn(ResponseResult.error(400, "该项目下存在 2 条未完成的工时记录，无法删除"));

            mockMvc.perform(delete("/api/projects/1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("该项目下存在 2 条未完成的工时记录，无法删除"));
        }

        @Test
        @DisplayName("项目不存在 - 返回404")
        void deleteNotFound() throws Exception {
            when(projectService.delete(999L)).thenReturn(ResponseResult.error(404, "项目不存在"));

            mockMvc.perform(delete("/api/projects/999")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    @Nested
    @DisplayName("获取项目成员 GET /api/projects/{id}/members")
    class GetMembersTest {

        @Test
        @DisplayName("获取成员列表成功")
        void getMembers() throws Exception {
            when(projectService.getMembers(1L))
                    .thenReturn(ResponseResult.success(Arrays.asList(sampleMember)));

            mockMvc.perform(get("/api/projects/1/members")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].username").value("zhangsan"))
                    .andExpect(jsonPath("$.data[0].name").value("张三"));
        }
    }

    @Nested
    @DisplayName("更新项目成员 PUT /api/projects/{id}/members")
    class UpdateMembersTest {

        @Test
        @DisplayName("更新成员列表成功")
        void updateMembers() throws Exception {
            when(projectService.updateMembers(eq(1L), anyList()))
                    .thenReturn(ResponseResult.success(null));

            mockMvc.perform(put("/api/projects/1/members")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[1, 2, 3]"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("清空成员列表成功")
        void clearMembers() throws Exception {
            when(projectService.updateMembers(eq(1L), anyList()))
                    .thenReturn(ResponseResult.success(null));

            mockMvc.perform(put("/api/projects/1/members")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[]"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }
}
