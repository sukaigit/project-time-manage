package com.ptm.controller;

import com.ptm.entity.User;
import com.ptm.service.UserService;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("user", new Object());
    }

    @Nested
    @DisplayName("查询用户列表 GET /api/users")
    class ListTest {
        @Test
        @DisplayName("分页查询成功")
        void listSuccess() throws Exception {
            Map<String, Object> pageResult = new HashMap<>();
            pageResult.put("list", Collections.singletonList(new HashMap<String, Object>() {{ put("username","admin"); put("name","管理员"); }}));
            pageResult.put("total", 1L);
            when(userService.list(anyInt(), anyInt(), any(), any(), any(), any()))
                    .thenReturn(ResponseResult.success(pageResult));
            mockMvc.perform(get("/api/users").param("page","1").param("size","10").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
        @Test @DisplayName("未登录 - 返回401")
        void withoutLogin() throws Exception {
            mockMvc.perform(get("/api/users").session(new MockHttpSession()))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested @DisplayName("创建用户 POST /api/users")
    class CreateTest {
        @Test @DisplayName("创建成功")
        void createSuccess() throws Exception {
            when(userService.add(any(User.class))).thenReturn(ResponseResult.success(null));
            mockMvc.perform(post("/api/users").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"test\",\"name\":\"测试\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
        @Test @DisplayName("用户名为空 - 返回400")
        void emptyUsername() throws Exception {
            mockMvc.perform(post("/api/users").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"\",\"name\":\"测试\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("用户名不能为空"));
        }
    }

    @Nested @DisplayName("更新用户 PUT /api/users/{id}")
    class UpdateTest {
        @Test @DisplayName("更新成功")
        void updateSuccess() throws Exception {
            when(userService.update(any(User.class))).thenReturn(ResponseResult.success(null));
            mockMvc.perform(put("/api/users/1").session(session).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"新名称\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested @DisplayName("删除用户 DELETE /api/users/{id}")
    class DeleteTest {
        @Test @DisplayName("删除成功")
        void deleteSuccess() throws Exception {
            when(userService.delete(1L)).thenReturn(ResponseResult.success(null));
            mockMvc.perform(delete("/api/users/1").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
        @Test @DisplayName("用户不存在 - 返回404")
        void deleteNotFound() throws Exception {
            when(userService.delete(999L)).thenReturn(ResponseResult.error(404, "用户不存在"));
            mockMvc.perform(delete("/api/users/999").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(404));
        }
    }

    @Nested @DisplayName("重置密码 POST /api/users/{id}/reset-password")
    class ResetPasswordTest {
        @Test @DisplayName("重置成功")
        void resetSuccess() throws Exception {
            when(userService.resetPassword(1L)).thenReturn(ResponseResult.success(null));
            mockMvc.perform(post("/api/users/1/reset-password").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        }
    }
}
