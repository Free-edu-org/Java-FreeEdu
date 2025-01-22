package com.byt.freeEdu.model.enums;

public enum SubjectEnum {
    POLISH("Polski"),
    MATH("Matematyka"),
    GEOGRAPHY("Geografia");

    private final String displayName;

    SubjectEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
