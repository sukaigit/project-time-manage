package com.ptm.entity;

import lombok.Data;

@Data
public class Task {
    private Long id;
    private String name;
    private String code;
    private Long projectId;
    private Integer status;
    private Long createBy;
    private String createTime;
}
