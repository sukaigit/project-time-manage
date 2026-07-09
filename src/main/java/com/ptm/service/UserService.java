package com.ptm.service;

import com.ptm.entity.User;
import com.ptm.entity.UserRole;
import com.ptm.mapper.UserMapper;
import com.ptm.mapper.UserRoleMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询用户列表
     */
    public ResponseResult<Map<String, Object>> list(int page, int size, String username,
                                                    String name, Long roleId, Integer status) {
        int offset = (page - 1) * size;

        List<User> users = userMapper.list(offset, size, username, name, roleId, status);
        long total = userMapper.count(username, name, roleId, status);

        List<Map<String, Object>> userList = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("name", u.getName());
            map.put("dept", u.getDept());
            map.put("status", u.getStatus());
            map.put("firstLogin", u.getFirstLogin());
            map.put("createTime", u.getCreateTime());
            map.put("updateTime", u.getUpdateTime());
            List<UserRole> urs = userRoleMapper.findByUserId(u.getId());
            if (urs != null && !urs.isEmpty()) {
                map.put("roleId", urs.get(0).getRoleId());
            }
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", userList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseResult.success(result);
    }

    /**
     * 根据ID查询用户
     */
    public ResponseResult<User> getById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        return ResponseResult.success(user);
    }

    /**
     * 新增用户
     */
    public ResponseResult<Void> add(User user) {
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) {
            return ResponseResult.error(400, "用户名已存在");
        }
        user.setPassword(passwordEncoder.encode("uu888888"));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getFirstLogin() == null) {
            user.setFirstLogin(1);
        }
        userMapper.insert(user);
        return ResponseResult.success(null);
    }

    /**
     * 更新用户
     */
    public ResponseResult<Void> update(User user) {
        User exist = userMapper.findById(user.getId());
        if (exist == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        userMapper.update(user);
        return ResponseResult.success(null);
    }

    /**
     * 删除用户
     */
    public ResponseResult<Void> delete(Long id) {
        User exist = userMapper.findById(id);
        if (exist == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        userRoleMapper.deleteByUserId(id);
        userMapper.deleteById(id);
        return ResponseResult.success(null);
    }

    /**
     * 重置密码为 uu888888
     */
    public ResponseResult<Void> resetPassword(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseResult.error(404, "用户不存在");
        }
        String encoded = passwordEncoder.encode("uu888888");
        userMapper.updatePassword(userId, encoded);
        return ResponseResult.success(null);
    }

    /**
     * 为用户分配角色
     */
    public ResponseResult<Void> assignRole(Long userId, Long roleId) {
        userRoleMapper.deleteByUserId(userId);
        if (roleId != null) {
            userRoleMapper.insertByParams(userId, roleId);
        }
        return ResponseResult.success(null);
    }
}
