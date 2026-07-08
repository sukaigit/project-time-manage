package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.RolePermission;
import com.ptm.mapper.RoleMapper;
import com.ptm.mapper.RolePermissionMapper;
import com.ptm.mapper.UserRoleMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    public ResponseResult<List<Role>> list() {
        return ResponseResult.success(roleMapper.findAll());
    }

    public ResponseResult<Role> getById(Long id) {
        Role role = roleMapper.findById(id);
        if (role == null) {
            return ResponseResult.error(404, "角色不存在");
        }
        return ResponseResult.success(role);
    }

    public ResponseResult<Void> add(Role role) {
        Role exist = roleMapper.findByCode(role.getCode());
        if (exist != null) {
            return ResponseResult.error(400, "角色编码已存在");
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        roleMapper.insert(role);
        return ResponseResult.success(null);
    }

    public ResponseResult<Void> update(Role role) {
        Role exist = roleMapper.findById(role.getId());
        if (exist == null) {
            return ResponseResult.error(404, "角色不存在");
        }
        roleMapper.update(role);
        return ResponseResult.success(null);
    }

    /**
     * 删除角色，检查是否有关联用户
     */
    public ResponseResult<Void> delete(Long id) {
        Role exist = roleMapper.findById(id);
        if (exist == null) {
            return ResponseResult.error(404, "角色不存在");
        }
        long userCount = userRoleMapper.countByRoleId(id);
        if (userCount > 0) {
            return ResponseResult.error(400, "该角色下存在 " + userCount + " 个用户，无法删除");
        }
        rolePermissionMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);
        return ResponseResult.success(null);
    }

    /**
     * 获取角色权限
     */
    public ResponseResult<Map<String, List<String>>> getPermissions(Long roleId) {
        List<RolePermission> list = rolePermissionMapper.findByRoleId(roleId);
        Map<String, List<String>> permMap = new HashMap<>();
        for (RolePermission rp : list) {
            String actions = rp.getActions();
            List<String> actionList = (actions != null && !actions.isEmpty())
                    ? Arrays.asList(actions.split(","))
                    : new ArrayList<>();
            permMap.put(rp.getMenuKey(), actionList);
        }
        return ResponseResult.success(permMap);
    }

    /**
     * 保存角色权限
     */
    @Transactional
    public ResponseResult<Void> savePermissions(Long roleId, Map<String, List<String>> permissions) {
        Role exist = roleMapper.findById(roleId);
        if (exist == null) {
            return ResponseResult.error(404, "角色不存在");
        }
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissions != null) {
            for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
                String menuKey = entry.getKey();
                List<String> actionList = entry.getValue();
                String actions = (actionList != null && !actionList.isEmpty())
                        ? String.join(",", actionList)
                        : "";
                rolePermissionMapper.insertByParams(roleId, menuKey, actions);
            }
        }
        return ResponseResult.success(null);
    }
}
