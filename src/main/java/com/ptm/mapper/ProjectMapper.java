package com.ptm.mapper;

import com.ptm.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    int insert(Project project);

    int updateById(Project project);

    int deleteById(@Param("id") Long id);

    Project selectById(@Param("id") Long id);

    Project selectByCode(@Param("code") String code);

    List<Project> selectList(@Param("name") String name,
                             @Param("code") String code,
                             @Param("status") Integer status);

    long totalCount();

    // ===== 别名（兼容 Service 调用） =====
    List<Project> findAll();

    Project findById(@Param("id") Long id);

    int update(Project project);

    long countUnfinishedTimeEntries(@Param("projectId") Long projectId);

}
