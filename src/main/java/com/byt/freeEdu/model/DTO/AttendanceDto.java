package com.byt.freeEdu.model.DTO;

import java.time.LocalDate;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.enums.AttendanceEnum;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceDto{

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
    dto.setAttendanceId(attendance.getId());
    dto.setStudentFirstName(attendance.getStudent().getFirstname());
    dto.setStudentLastName(attendance.getStudent().getLastname());
    dto.setTeacherFirstName(attendance.getTeacher().getFirstname());
    dto.setTeacherLastName(attendance.getTeacher().getLastname());
    dto.setSubjectName(attendance.getSubject().getDisplayName());
    dto.setAttendanceDate(attendance.getAttendanceDate().toString());
    dto.setAttendanceStatus(attendance.getStatus().getDisplayName());
    return dto;
  }

  public Attendance toEntity(Student student, Teacher teacher) {
    Attendance attendance = new Attendance();
    attendance.setAttendanceId(this.attendanceId);
    attendance.setStudent(student);
    attendance.setTeacher(teacher);
    attendance.setAttendanceDate(LocalDate.parse(this.attendanceDate));
    attendance.setStatus(this.status != null
        ? this.status
        : AttendanceEnum.valueOf(this.attendanceStatus.toUpperCase()));
    attendance.setSubject(this.subjectEnum != null
        ? this.subjectEnum
        : SubjectEnum.valueOf(this.subjectName.toUpperCase()));
    return attendance;
  }
}
