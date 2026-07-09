package com.ptm.mapper;

import com.ptm.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM tb_user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM tb_user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT u.* FROM tb_user u " +
            "LEFT JOIN tb_user_role ur ON u.id = ur.user_id " +
            "WHERE 1=1 " +
            "AND (#{username} IS NULL OR u.username LIKE CONCAT('%',#{username},'%')) " +
            "AND (#{name} IS NULL OR u.name LIKE CONCAT('%',#{name},'%')) " +
            "AND (#{roleId} IS NULL OR ur.role_id = #{roleId}) " +
            "AND (#{status} IS NULL OR u.status = #{status}) " +
            "GROUP BY u.id " +
            "ORDER BY u.id DESC " +
            "LIMIT #{offset}, #{size}")
    List<User> list(@Param("offset") int offset, @Param("size") int size,
                    @Param("username") String username, @Param("name") String name,
                    @Param("roleId") Long roleId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM tb_user u " +
            "LEFT JOIN tb_user_role ur ON u.id = ur.user_id " +
            "WHERE 1=1 " +
            "AND (#{username} IS NULL OR u.username LIKE CONCAT('%',#{username},'%')) " +
            "AND (#{name} IS NULL OR u.name LIKE CONCAT('%',#{name},'%')) " +
            "AND (#{roleId} IS NULL OR ur.role_id = #{roleId}) " +
            "AND (#{status} IS NULL OR u.status = #{status})")
    long count(@Param("username") String username, @Param("name") String name,
               @Param("roleId") Long roleId, @Param("status") Integer status);

    @Insert("INSERT INTO tb_user(username, password, name, dept, status, first_login, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{name}, #{dept}, #{status}, #{firstLogin}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE tb_user SET username=#{username}, name=#{name}, dept=#{dept}, status=#{status}, update_time=NOW() WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE tb_user SET password=#{password}, update_time=NOW() WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Delete("DELETE FROM tb_user WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM tb_user")
    long totalCount();
}
