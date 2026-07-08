package com.ptm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String dept;
    private Integer status;
    private Integer firstLogin;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
