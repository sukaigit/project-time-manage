package com.ptm.controller;

import com.ptm.entity.Task;
import com.ptm.service.TaskService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseResult<List<Task>> list(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return taskService.listByProject(projectId);
        }
        return taskService.list();
    }

    @GetMapping("/{id}")
    public ResponseResult<Task> getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Task task) {
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            return ResponseResult.error(400, "任务名称不能为空");
        }
        if (task.getProjectId() == null) {
            return ResponseResult.error(400, "所属项目不能为空");
        }
        return taskService.add(task);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return taskService.update(task);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        return taskService.delete(id);
    }
}
