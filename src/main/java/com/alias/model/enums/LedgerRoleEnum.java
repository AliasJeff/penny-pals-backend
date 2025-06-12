package com.alias.model.enums;

public enum LedgerRoleEnum {
    OWNER("所有者", "owner"),
    EDITOR("编辑者", "editor"),
    VIEWER("查看者", "viewer");

    private final String text;
    private final String value;

    LedgerRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
