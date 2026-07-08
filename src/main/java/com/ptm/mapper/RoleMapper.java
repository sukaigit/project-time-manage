package com.ptm.mapper;

import com.ptm.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    int insert(Role role);

    int updateById(Role role);

    int deleteById(@Param("id") Long id);

    Role selectById(@Param("id") Long id);

    Role selectByCode(@Param("code") String code);

    List<Role> selectList(@Param("name") String name,
                          @Param("code") String code,
                          @Param("status") Integer status);

    // ===== 别名（兼容 RoleService 调用） =====
    List<Role> findAll();

    Role findById(@Param("id") Long id);

    Role findByCode(@Param("code") String code);

    int update(Role role);

    List<Role> findByUserId(@Param("userId") Long userId);

}
