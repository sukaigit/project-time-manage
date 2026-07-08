package com.ptm.mapper;

import com.ptm.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper {

    int insert(Task task);

    int updateById(Task task);

    int deleteById(@Param("id") Long id);

    Task selectById(@Param("id") Long id);

    Task selectByCode(@Param("code") String code);

    List<Task> selectByProjectId(@Param("projectId") Long projectId);

    List<Task> selectList(@Param("name") String name,
                          @Param("projectId") Long projectId,
                          @Param("status") Integer status);

    long totalCount();

    // ===== 别名（兼容 Service 调用） =====
    List<Task> findAll();

    Task findById(@Param("id") Long id);

    int update(Task task);

    List<Task> findByProjectId(@Param("projectId") Long projectId);

}
