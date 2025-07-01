package com.byt.freeEdu.model.DTO;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.enums.AttendanceEnum;
import com.byt.freeEdu.model.enums.SubjectEnum;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceDto {

    private int attendanceId;

    private int id;

    private String studentFirstName;

    private String studentLastName;

    private int studentId;

    private String teacherFirstName;

    private String teacherLastName;

    private int teacherId;

    private String subjectName;

    private String attendanceDate;

    private String attendanceStatus;

    private SubjectEnum subjectEnum;

    private AttendanceEnum status;

    public static AttendanceDto fromEntity(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setStudentFirstName(attendance.getStudent().getFirstname());
        dto.setStudentLastName(attendance.getStudent().getLastname());
        dto.setTeacherFirstName(attendance.getTeacher().getFirstname());
        dto.setTeacherLastName(attendance.getTeacher().getLastname());
        dto.setSubjectName(attendance.getSubject().getDisplayName());
        dto.setAttendanceDate(attendance.getAttendanceDate().toString());
        dto.setAttendanceStatus(attendance.getStatus().getDisplayName());
        return dto;
    }
}
