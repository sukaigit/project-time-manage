package com.ptm.mapper;

import com.ptm.entity.TimeEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TimeEntryMapper {

    int insert(TimeEntry timeEntry);

    int updateById(TimeEntry timeEntry);

    int deleteById(@Param("id") Long id);

    TimeEntry selectById(@Param("id") Long id);

    List<TimeEntry> selectList(@Param("userId") Long userId,
                               @Param("projectId") Long projectId,
                               @Param("taskId") Long taskId,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate,
                               @Param("status") Integer status);

    List<Map<String, Object>> countByStatus(@Param("userId") Long userId,
                                            @Param("projectId") Long projectId);

    // ===== 别名（兼容 Service 调用） =====
    TimeEntry findById(@Param("id") Long id);

    int update(TimeEntry timeEntry);

    List<Map<String, Object>> list(@Param("offset") int offset, @Param("size") int size,
                                    @Param("userId") Long userId,
                                    @Param("projectId") Long projectId,
                                    @Param("taskId") Long taskId,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate,
                                    @Param("status") Integer status);

    long count(@Param("userId") Long userId,
               @Param("projectId") Long projectId,
               @Param("taskId") Long taskId,
               @Param("startDate") String startDate,
               @Param("endDate") String endDate,
               @Param("status") Integer status);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // ===== 统计查询 =====
    List<Map<String, Object>> employeeStats(@Param("year") int year,
                                            @Param("month") int month,
                                            @Param("name") String name);

    List<Map<String, Object>> projectStats(@Param("year") int year,
                                           @Param("month") int month,
                                           @Param("name") String name);

    double sumApprovedHoursByMonth(@Param("year") int year, @Param("month") int month);

    double sumApprovedHours();

    List<Map<String, Object>> top5Employees();

    List<Map<String, Object>> recentEntries();

}
