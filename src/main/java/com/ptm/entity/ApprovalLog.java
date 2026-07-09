package com.ptm.entity;

import lombok.Data;

@Data
public class ApprovalLog {
    private Long id;
    private Long timeEntryId;
    private Long approverId;
    private Integer action;
    private String reason;
}
