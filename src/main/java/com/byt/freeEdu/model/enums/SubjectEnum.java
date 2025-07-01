package com.byt.freeEdu.model.enums;

public enum SubjectEnum {
    POLISH("Polski"), MATH("Matematyka"), GEOGRAPHY("Geografia"), HISTORY("Historia"), SCIENCE(
            "Nauka"), ART("Sztuka"), SPORTS("Sport");

    private final String displayName;

    SubjectEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
