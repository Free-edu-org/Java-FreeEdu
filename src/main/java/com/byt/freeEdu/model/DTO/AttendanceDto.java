package com.byt.freeEdu.model.DTO;

import com.byt.freeEdu.model.Attendance;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceDto {

    private int id;
    private String studentFirstName;
    private String studentLastName;

    private String teacherFirstName;
    private String teacherLastName;

    private String subjectName;
    private String attendanceDate;
    private String status;

    public static AttendanceDto fromEntity(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setStudentFirstName(attendance.getStudent().getFirstname());
        dto.setStudentLastName(attendance.getStudent().getLastname());
        dto.setTeacherFirstName(attendance.getTeacher().getFirstname());
        dto.setTeacherLastName(attendance.getTeacher().getLastname());
        dto.setSubjectName(attendance.getSubject().getDisplayName());
        dto.setAttendanceDate(attendance.getAttendanceDate().toString());
        dto.setStatus(attendance.getStatus().getDisplayName());
        return dto;
    }
}
