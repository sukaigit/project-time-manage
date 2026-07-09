package com.ptm.mapper;

import com.ptm.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMemberMapper {

    int insert(ProjectMember projectMember);

    int insertBatch(List<ProjectMember> list);

    int deleteById(@Param("id") Long id);

    int deleteByProjectId(@Param("projectId") Long projectId);

    int deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    ProjectMember selectById(@Param("id") Long id);

    List<ProjectMember> selectByProjectId(@Param("projectId") Long projectId);

    List<ProjectMember> selectByUserId(@Param("userId") Long userId);

    // ===== 别名（兼容 Service 调用） =====
    List<com.ptm.entity.User> findMembersByProjectId(@Param("projectId") Long projectId);

    int insertByParams(@Param("projectId") Long projectId, @Param("userId") Long userId);

}
