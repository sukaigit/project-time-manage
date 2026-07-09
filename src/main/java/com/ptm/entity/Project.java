package com.ptm.entity;

import lombok.Data;

@Data
public class Project {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String dept;
    private Integer status;
    private String startDate;
    private String endDate;
    private Long createBy;
}
