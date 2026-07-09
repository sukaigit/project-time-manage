package com.ptm.controller;

import com.ptm.entity.TimeEntry;
import com.ptm.service.TimeEntryService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/time-entries")
public class TimeEntryController {

    @Autowired
    private TimeEntryService timeEntryService;

    @GetMapping
    public ResponseResult<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status) {
        return timeEntryService.list(page, size, employeeId, projectId, taskId, startDate, endDate, status);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody TimeEntry timeEntry) {
        if (timeEntry.getProjectId() == null) {
            return ResponseResult.error(400, "所属项目不能为空");
        }
        if (timeEntry.getHours() == null || timeEntry.getHours() <= 0) {
            return ResponseResult.error(400, "工时必须大于0");
        }
        return timeEntryService.add(timeEntry);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id, @RequestBody TimeEntry timeEntry) {
        timeEntry.setId(id);
        return timeEntryService.update(timeEntry);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        return timeEntryService.delete(id);
    }
}
