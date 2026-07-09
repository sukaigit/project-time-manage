package com.ptm.controller;

import com.ptm.entity.Role;
import com.ptm.service.RoleService;
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

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private RoleService roleService;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());
    }

    @Nested @DisplayName("查询角色列表 GET /api/roles")
    class ListTest {
        @Test @DisplayName("查询成功")
        void listSuccess() throws Exception {
            Role r = new Role(); r.setId(1L); r.setName("管理员"); r.setCode("admin");
            when(roleService.list()).thenReturn(ResponseResult.success(Arrays.asList(r)));
            mockMvc.perform(get("/api/roles").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].name").value("管理员"));
        }
        @Test @DisplayName("未登录 - 返回401")
        void withoutLogin() throws Exception {
            mockMvc.perform(get("/api/roles").session(new MockHttpSession()))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested @DisplayName("创建角色 POST /api/roles")
    class CreateTest {
        @Test @DisplayName("创建成功")
        void createSuccess() throws Exception {
            when(roleService.add(any(Role.class))).thenReturn(ResponseResult.success(null));
            mockMvc.perform(post("/api/roles").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"测试角色\",\"code\":\"test\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
        @Test @DisplayName("名称为空 - 返回400")
        void emptyName() throws Exception {
            mockMvc.perform(post("/api/roles").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"\",\"code\":\"test\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("角色名称不能为空"));
        }
        @Test @DisplayName("编码为空 - 返回400")
        void emptyCode() throws Exception {
            mockMvc.perform(post("/api/roles").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"测试角色\",\"code\":\"\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("角色编码不能为空"));
        }
    }

    @Nested @DisplayName("更新角色 PUT /api/roles/{id}")
    class UpdateTest {
        @Test @DisplayName("更新成功")
        void updateSuccess() throws Exception {
            when(roleService.update(any(Role.class))).thenReturn(ResponseResult.success(null));
            mockMvc.perform(put("/api/roles/1").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"新角色名\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested @DisplayName("删除角色 DELETE /api/roles/{id}")
    class DeleteTest {
        @Test @DisplayName("删除成功")
        void deleteSuccess() throws Exception {
            when(roleService.delete(1L)).thenReturn(ResponseResult.success(null));
            mockMvc.perform(delete("/api/roles/1").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
        @Test @DisplayName("有关联用户 - 返回400")
        void deleteWithUsers() throws Exception {
            when(roleService.delete(1L)).thenReturn(ResponseResult.error(400, "该角色下存在 3 个用户，无法删除"));
            mockMvc.perform(delete("/api/roles/1").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested @DisplayName("角色权限 GET /api/roles/{id}/permissions")
    class GetPermissionsTest {
        @Test @DisplayName("获取权限成功")
        void getPermissions() throws Exception {
            Map<String, List<String>> perms = new HashMap<>();
            perms.put("project", Arrays.asList("view","edit"));
            when(roleService.getPermissions(1L)).thenReturn(ResponseResult.success(perms));
            mockMvc.perform(get("/api/roles/1/permissions").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.project[0]").value("view"));
        }
    }

    @Nested @DisplayName("更新角色权限 PUT /api/roles/{id}/permissions")
    class UpdatePermissionsTest {
        @Test @DisplayName("更新权限成功")
        void updatePermissions() throws Exception {
            when(roleService.savePermissions(eq(1L), any())).thenReturn(ResponseResult.success(null));
            mockMvc.perform(put("/api/roles/1/permissions").session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"project\":[\"view\",\"edit\"],\"task\":[\"view\"]}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
    }
}
