package com.ptm.entity;

import lombok.Data;

@Data
public class RolePermission {
    private Long id;
    private Long roleId;
    private String menuKey;
    private String actions;
}
