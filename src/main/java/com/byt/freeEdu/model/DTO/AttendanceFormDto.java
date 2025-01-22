package com.byt.freeEdu.model.DTO;

import com.byt.freeEdu.model.enums.AttendanceEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AttendanceFormDto {
    private Map<Integer, AttendanceEnum> attendanceMap = new HashMap<>();

    public Map<Integer, AttendanceEnum> getAttendanceMap() {
        return attendanceMap;
    }

    public void setAttendanceMap(Map<Integer, AttendanceEnum> attendanceMap) {
        this.attendanceMap = attendanceMap;
    }
}
