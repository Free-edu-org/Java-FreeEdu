package com.byt.freeEdu.model.enums;

public enum AttendanceEnum {
  PRESENT("Obecny"), ABSENT("Nieobecny"), LATE("Spóźniony");

  private final String displayName;

  AttendanceEnum(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
