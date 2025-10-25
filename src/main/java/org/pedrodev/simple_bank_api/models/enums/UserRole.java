package org.pedrodev.simple_bank_api.models.enums;

public enum UserRole {
    COMUM("ROLE_COMUM"),
    LOJISTA("ROLE_LOJISTA");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
