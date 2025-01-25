package com.byt.freeEdu.model.DTO;

import com.byt.freeEdu.model.enums.AttendanceEnum;
import com.byt.freeEdu.model.enums.SubjectEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceDto {
    private int attendanceId;
    private int studentId;
    private int teacherId;
    private LocalDate attendanceDate;
    private AttendanceEnum status;
    private SubjectEnum subject;
}