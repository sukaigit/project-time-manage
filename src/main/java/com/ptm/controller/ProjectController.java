package com.ptm.controller;

import com.ptm.entity.Project;
import com.ptm.service.ProjectService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseResult<List<Project>> list() {
        return projectService.list();
    }

    @GetMapping("/{id}")
    public ResponseResult<Project> getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            return ResponseResult.error(400, "项目名称不能为空");
        }
        if (project.getCode() == null || project.getCode().trim().isEmpty()) {
            return ResponseResult.error(400, "项目编码不能为空");
        }
        return projectService.add(project);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        return projectService.update(project);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        return projectService.delete(id);
    }

    @GetMapping("/{id}/members")
    public ResponseResult<List<com.ptm.entity.User>> getMembers(@PathVariable Long id) {
        return projectService.getMembers(id);
    }

    @PutMapping("/{id}/members")
    public ResponseResult<Void> updateMembers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        return projectService.updateMembers(id, userIds);
    }
}
