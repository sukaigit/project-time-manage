package com.ptm.service;

import com.ptm.entity.Task;
import com.ptm.mapper.TaskMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    public ResponseResult<List<Task>> list() {
        return ResponseResult.success(taskMapper.findAll());
    }

    public ResponseResult<Task> getById(Long id) {
        Task task = taskMapper.findById(id);
        if (task == null) {
            return ResponseResult.error(404, "任务不存在");
        }
        return ResponseResult.success(task);
    }

    public ResponseResult<Void> add(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(1);
        }
        taskMapper.insert(task);
        return ResponseResult.success(null);
    }

    public ResponseResult<Void> update(Task task) {
        Task exist = taskMapper.findById(task.getId());
        if (exist == null) {
            return ResponseResult.error(404, "任务不存在");
        }
        taskMapper.update(task);
        return ResponseResult.success(null);
    }

    public ResponseResult<Void> delete(Long id) {
        Task exist = taskMapper.findById(id);
        if (exist == null) {
            return ResponseResult.error(404, "任务不存在");
        }
        taskMapper.deleteById(id);
        return ResponseResult.success(null);
    }

    /**
     * 按项目ID查询任务列表
     */
    public ResponseResult<List<Task>> listByProject(Long projectId) {
        return ResponseResult.success(taskMapper.findByProjectId(projectId));
    }
}
