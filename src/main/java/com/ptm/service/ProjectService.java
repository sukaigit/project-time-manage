package com.ptm.service;

import com.ptm.entity.Project;
import com.ptm.entity.User;
import com.ptm.mapper.ProjectMapper;
import com.ptm.mapper.ProjectMemberMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    public ResponseResult<List<Project>> list() {
        return ResponseResult.success(projectMapper.findAll());
    }

    public ResponseResult<Project> getById(Long id) {
        Project project = projectMapper.findById(id);
        if (project == null) {
            return ResponseResult.error(404, "项目不存在");
        }
        return ResponseResult.success(project);
    }

    public ResponseResult<Void> add(Project project) {
        if (project.getStatus() == null) {
            project.setStatus(1);
        }
        projectMapper.insert(project);
        return ResponseResult.success(null);
    }

    public ResponseResult<Void> update(Project project) {
        Project exist = projectMapper.findById(project.getId());
        if (exist == null) {
            return ResponseResult.error(404, "项目不存在");
        }
        projectMapper.update(project);
        return ResponseResult.success(null);
    }

    /**
     * 删除项目，检查是否存在未完成工时
     */
    public ResponseResult<Void> delete(Long id) {
        Project exist = projectMapper.findById(id);
        if (exist == null) {
            return ResponseResult.error(404, "项目不存在");
        }
        long unfinishedCount = projectMapper.countUnfinishedTimeEntries(id);
        if (unfinishedCount > 0) {
            return ResponseResult.error(400, "该项目下存在 " + unfinishedCount + " 条未完成的工时记录，无法删除");
        }
        projectMemberMapper.deleteByProjectId(id);
        projectMapper.deleteById(id);
        return ResponseResult.success(null);
    }

    /**
     * 获取项目成员
     */
    public ResponseResult<List<User>> getMembers(Long projectId) {
        Project project = projectMapper.findById(projectId);
        if (project == null) {
            return ResponseResult.error(404, "项目不存在");
        }
        return ResponseResult.success(projectMemberMapper.findMembersByProjectId(projectId));
    }

    /**
     * 更新项目成员（全量替换）
     */
    @Transactional
    public ResponseResult<Void> updateMembers(Long projectId, List<Long> userIds) {
        Project project = projectMapper.findById(projectId);
        if (project == null) {
            return ResponseResult.error(404, "项目不存在");
        }
        projectMemberMapper.deleteByProjectId(projectId);
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                projectMemberMapper.insertByParams(projectId, userId);
            }
        }
        return ResponseResult.success(null);
    }
}
