package com.ptm.mapper;

import com.ptm.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper {

    int insert(RolePermission rolePermission);

    int updateById(RolePermission rolePermission);

    int deleteById(@Param("id") Long id);

    int deleteByRoleId(@Param("roleId") Long roleId);

    RolePermission selectById(@Param("id") Long id);

    List<RolePermission> selectByRoleId(@Param("roleId") Long roleId);

    List<RolePermission> selectList(@Param("roleId") Long roleId,
                                    @Param("menuKey") String menuKey);

    // ===== 别名（兼容 RoleService 调用） =====
    List<RolePermission> findByRoleId(@Param("roleId") Long roleId);

    int insertByParams(@Param("roleId") Long roleId,
                       @Param("menuKey") String menuKey,
                       @Param("actions") String actions);

}
