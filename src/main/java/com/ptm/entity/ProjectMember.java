package com.ptm.entity;

import lombok.Data;

@Data
public class ProjectMember {
    private Long id;
    private Long projectId;
    private Long userId;
}
