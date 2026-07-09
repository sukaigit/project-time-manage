package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.RolePermission;
import com.ptm.mapper.RoleMapper;
import com.ptm.mapper.RolePermissionMapper;
import com.ptm.mapper.UserRoleMapper;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @InjectMocks
    private RoleService roleService;

    private Role adminRole;
    private Role userRole;
    private RolePermission perm1;
    private RolePermission perm2;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("管理员");
        adminRole.setCode("ADMIN");
        adminRole.setStatus(1);
        adminRole.setNote("系统管理员");

        userRole = new Role();
        userRole.setId(2L);
        userRole.setName("普通用户");
        userRole.setCode("USER");
        userRole.setStatus(1);
        userRole.setNote("普通用户");

        perm1 = new RolePermission();
        perm1.setId(1L);
        perm1.setRoleId(1L);
        perm1.setMenuKey("user");
        perm1.setActions("view,add,edit,delete");

        perm2 = new RolePermission();
        perm2.setId(2L);
        perm2.setRoleId(1L);
        perm2.setMenuKey("project");
        perm2.setActions("view,add,edit");
    }

    @Nested
    @DisplayName("角色列表测试")
    class ListTest {

        @Test
        @DisplayName("获取全部角色列表 - 成功")
        void listAllRoles_success() {
            when(roleMapper.findAll()).thenReturn(Arrays.asList(adminRole, userRole));

            ResponseResult<List<Role>> result = roleService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());
            assertEquals("管理员", result.getData().get(0).getName());
        }

        @Test
        @DisplayName("角色列表为空 - 返回空列表")
        void listAllRoles_empty() {
            when(roleMapper.findAll()).thenReturn(new ArrayList<>());

            ResponseResult<List<Role>> result = roleService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("角色查询测试")
    class GetByIdTest {

        @Test
        @DisplayName("根据ID查询角色 - 成功")
        void getById_success() {
            when(roleMapper.findById(1L)).thenReturn(adminRole);

            ResponseResult<Role> result = roleService.getById(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("ADMIN", result.getData().getCode());
        }

        @Test
        @DisplayName("角色不存在 - 返回404")
        void getById_notFound() {
            when(roleMapper.findById(999L)).thenReturn(null);

            ResponseResult<Role> result = roleService.getById(999L);

            assertEquals(404, result.getCode());
            assertEquals("角色不存在", result.getMsg());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("角色新增测试")
    class AddTest {

        @Test
        @DisplayName("新增角色 - 成功")
        void add_success() {
            Role newRole = new Role();
            newRole.setName("测试角色");
            newRole.setCode("TEST");
            newRole.setStatus(1);

            when(roleMapper.findByCode("TEST")).thenReturn(null);

            ResponseResult<Void> result = roleService.add(newRole);

            assertEquals(200, result.getCode());
            verify(roleMapper).insert(newRole);
        }

        @Test
        @DisplayName("角色编码已存在 - 返回400")
        void add_duplicateCode() {
            Role newRole = new Role();
            newRole.setName("测试角色");
            newRole.setCode("ADMIN");

            when(roleMapper.findByCode("ADMIN")).thenReturn(adminRole);

            ResponseResult<Void> result = roleService.add(newRole);

            assertEquals(400, result.getCode());
            assertEquals("角色编码已存在", result.getMsg());
            verify(roleMapper, never()).insert(any());
        }

        @Test
        @DisplayName("新增角色 - 状态为空时默认为1")
        void add_defaultStatus() {
            Role newRole = new Role();
            newRole.setName("新角色");
            newRole.setCode("NEW");

            when(roleMapper.findByCode("NEW")).thenReturn(null);

            ResponseResult<Void> result = roleService.add(newRole);

            assertEquals(200, result.getCode());
            assertEquals(1, newRole.getStatus().intValue());
            verify(roleMapper).insert(newRole);
        }
    }

    @Nested
    @DisplayName("角色更新测试")
    class UpdateTest {

        @Test
        @DisplayName("更新角色 - 成功")
        void update_success() {
            Role updateRole = new Role();
            updateRole.setId(1L);
            updateRole.setName("超管");
            updateRole.setCode("SUPER_ADMIN");

            when(roleMapper.findById(1L)).thenReturn(adminRole);

            ResponseResult<Void> result = roleService.update(updateRole);

            assertEquals(200, result.getCode());
            verify(roleMapper).update(updateRole);
        }

        @Test
        @DisplayName("更新不存在的角色 - 返回404")
        void update_notFound() {
            Role updateRole = new Role();
            updateRole.setId(999L);
            updateRole.setName("不存在");

            when(roleMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = roleService.update(updateRole);

            assertEquals(404, result.getCode());
            assertEquals("角色不存在", result.getMsg());
            verify(roleMapper, never()).update(any());
        }
    }

    @Nested
    @DisplayName("角色删除测试")
    class DeleteTest {

        @Test
        @DisplayName("删除角色 - 无关联用户时成功")
        void delete_success() {
            when(roleMapper.findById(1L)).thenReturn(adminRole);
            when(userRoleMapper.countByRoleId(1L)).thenReturn(0L);

            ResponseResult<Void> result = roleService.delete(1L);

            assertEquals(200, result.getCode());
            verify(rolePermissionMapper).deleteByRoleId(1L);
            verify(roleMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的角色 - 返回404")
        void delete_notFound() {
            when(roleMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = roleService.delete(999L);

            assertEquals(404, result.getCode());
            assertEquals("角色不存在", result.getMsg());
            verify(rolePermissionMapper, never()).deleteByRoleId(anyLong());
            verify(roleMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("删除有关联用户的角色 - 返回400")
        void delete_hasUsers() {
            when(roleMapper.findById(1L)).thenReturn(adminRole);
            when(userRoleMapper.countByRoleId(1L)).thenReturn(3L);

            ResponseResult<Void> result = roleService.delete(1L);

            assertEquals(400, result.getCode());
            assertTrue(result.getMsg().contains("3"));
            assertTrue(result.getMsg().contains("无法删除"));
            verify(rolePermissionMapper, never()).deleteByRoleId(anyLong());
            verify(roleMapper, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("权限管理测试")
    class PermissionTest {

        @Test
        @DisplayName("获取角色权限 - 成功")
        void getPermissions_success() {
            when(rolePermissionMapper.findByRoleId(1L)).thenReturn(Arrays.asList(perm1, perm2));

            ResponseResult<Map<String, List<String>>> result = roleService.getPermissions(1L);

            assertEquals(200, result.getCode());
            Map<String, List<String>> data = result.getData();
            assertNotNull(data);
            assertTrue(data.containsKey("user"));
            assertTrue(data.containsKey("project"));
            assertEquals(4, data.get("user").size());
            assertEquals(3, data.get("project").size());
            assertTrue(data.get("user").contains("delete"));
        }

        @Test
        @DisplayName("获取权限 - 角色无权限时返回空Map")
        void getPermissions_empty() {
            when(rolePermissionMapper.findByRoleId(2L)).thenReturn(new ArrayList<>());

            ResponseResult<Map<String, List<String>>> result = roleService.getPermissions(2L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }

        @Test
        @DisplayName("保存权限 - 成功")
        void savePermissions_success() {
            when(roleMapper.findById(1L)).thenReturn(adminRole);

            Map<String, List<String>> perms = new HashMap<>();
            perms.put("user", Arrays.asList("view", "edit"));
            perms.put("dashboard", Arrays.asList("view"));

            ResponseResult<Void> result = roleService.savePermissions(1L, perms);

            assertEquals(200, result.getCode());
            verify(rolePermissionMapper).deleteByRoleId(1L);
            verify(rolePermissionMapper).insertByParams(1L, "user", "view,edit");
            verify(rolePermissionMapper).insertByParams(1L, "dashboard", "view");
        }

        @Test
        @DisplayName("保存权限 - 角色不存在返回404")
        void savePermissions_roleNotFound() {
            when(roleMapper.findById(999L)).thenReturn(null);

            Map<String, List<String>> perms = new HashMap<>();
            perms.put("user", Arrays.asList("view"));

            ResponseResult<Void> result = roleService.savePermissions(999L, perms);

            assertEquals(404, result.getCode());
            assertEquals("角色不存在", result.getMsg());
            verify(rolePermissionMapper, never()).deleteByRoleId(anyLong());
            verify(rolePermissionMapper, never()).insertByParams(anyLong(), anyString(), anyString());
        }

        @Test
        @DisplayName("保存权限 - permissions为空时只删除不插入")
        void savePermissions_nullPermissions() {
            when(roleMapper.findById(1L)).thenReturn(adminRole);

            ResponseResult<Void> result = roleService.savePermissions(1L, null);

            assertEquals(200, result.getCode());
            verify(rolePermissionMapper).deleteByRoleId(1L);
            verify(rolePermissionMapper, never()).insertByParams(anyLong(), anyString(), anyString());
        }

        @Test
        @DisplayName("保存权限 - action列表为空时写入空字符串")
        void savePermissions_emptyActions() {
            when(roleMapper.findById(1L)).thenReturn(adminRole);

            Map<String, List<String>> perms = new HashMap<>();
            perms.put("user", new ArrayList<>());

            ResponseResult<Void> result = roleService.savePermissions(1L, perms);

            assertEquals(200, result.getCode());
            verify(rolePermissionMapper).insertByParams(1L, "user", "");
        }
    }
}
