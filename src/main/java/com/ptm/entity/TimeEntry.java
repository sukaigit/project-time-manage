package com.ptm.entity;

import lombok.Data;

@Data
public class TimeEntry {
    private Long id;
    private Long userId;
    private Long projectId;
    private Long taskId;
    private String workDate;
    private Double hours;
    private String content;
    private Integer status;
    private String rejectReason;
    private String createTime;
    private String updateTime;
}
