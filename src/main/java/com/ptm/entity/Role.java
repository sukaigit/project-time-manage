package com.ptm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role {
    private Long id;
    private String name;
    private String code;
    private Integer status;
    private String note;
    private LocalDateTime createTime;
}
