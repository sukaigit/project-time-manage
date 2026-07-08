package com.ptm.mapper;

import com.ptm.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    int insert(UserRole userRole);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    UserRole selectById(@Param("id") Long id);

    List<UserRole> selectByUserId(@Param("userId") Long userId);

    List<UserRole> selectByRoleId(@Param("roleId") Long roleId);

    long countByRoleId(@Param("roleId") Long roleId);

    // ===== 别名（兼容 Service 调用） =====
    List<UserRole> findByUserId(@Param("userId") Long userId);

    int insertByParams(@Param("userId") Long userId, @Param("roleId") Long roleId);

}
