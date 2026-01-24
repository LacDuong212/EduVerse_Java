package com.eduverse.eduversebe.common.globalEnums;

public enum UserRole {
    student,
    instructor
    ;

    public String asAuthority() {
        return "ROLE_" + name().toUpperCase();
    }
}
