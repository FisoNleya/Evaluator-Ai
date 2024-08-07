package com.fiso.nleya.marker.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {


    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    ASSIGNER_READ("assigner:read"),
    ASSIGNER_UPDATE("assigner:update"),
    ASSIGNER_CREATE("assigner:create"),
    ASSIGNER_DELETE("assigner:delete"),


    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete");


    @Getter
    private final String permissionValue;
}
