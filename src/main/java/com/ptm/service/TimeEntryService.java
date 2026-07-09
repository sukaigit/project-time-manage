package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.TimeEntry;
import com.ptm.entity.User;
import com.ptm.mapper.TimeEntryMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimeEntryService {

    @Autowired
    private TimeEntryMapper timeEntryMapper;

    @Autowired
    private HttpSession session;

    /**
     * 分页查询工时列表，根据当前用户角色决定可见数据范围
     */
    public ResponseResult<Map<String, Object>> list(int page, int size, Long employeeId,
                                                    Long projectId, Long taskId,
                                                    String startDate, String endDate,
                                                    Integer status) {
        User currentUser = (User) session.getAttribute("user");
        List<Role> roles = (List<Role>) session.getAttribute("roles");

        Long filterEmployeeId = employeeId;
        if (currentUser != null && roles != null) {
            boolean isAdmin = roles.stream().anyMatch(r -> "admin".equals(r.getCode()));
            if (!isAdmin) {
                filterEmployeeId = currentUser.getId();
            }
        }

        int offset = (page - 1) * size;
        List<Map<String, Object>> list = timeEntryMapper.list(offset, size, filterEmployeeId,
                projectId, taskId, startDate, endDate, status);
        long total = timeEntryMapper.count(filterEmployeeId, projectId, taskId, startDate, endDate, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseResult.success(result);
    }

    public ResponseResult<TimeEntry> getById(Long id) {
        TimeEntry entry = timeEntryMapper.findById(id);
        if (entry == null) {
            return ResponseResult.error(404, "工时记录不存在");
        }
        return ResponseResult.success(entry);
    }

    public ResponseResult<Void> add(TimeEntry timeEntry) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            timeEntry.setUserId(currentUser.getId());
        }
        if (timeEntry.getStatus() == null) {
            timeEntry.setStatus(0);
        }
        timeEntryMapper.insert(timeEntry);
        return ResponseResult.success(null);
    }

    /**
     * 更新工时，只有 status=2(已驳回) 才可修改
     */
    public ResponseResult<Void> update(TimeEntry timeEntry) {
        TimeEntry exist = timeEntryMapper.findById(timeEntry.getId());
        if (exist == null) {
            return ResponseResult.error(404, "工时记录不存在");
        }
        if (exist.getStatus() != 2) {
            return ResponseResult.error(400, "只有已驳回的工时记录才能修改");
        }
        timeEntry.setStatus(0);
        timeEntryMapper.update(timeEntry);
        return ResponseResult.success(null);
    }

    public ResponseResult<Void> delete(Long id) {
        TimeEntry exist = timeEntryMapper.findById(id);
        if (exist == null) {
            return ResponseResult.error(404, "工时记录不存在");
        }
        timeEntryMapper.deleteById(id);
        return ResponseResult.success(null);
    }
}
