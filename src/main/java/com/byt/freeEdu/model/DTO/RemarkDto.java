package com.byt.freeEdu.model.DTO;

import com.byt.freeEdu.model.Remark;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RemarkDto{

  private int remarkId;

  private int teacherId;
  private String teacherFirstName;
  private String teacherLastName;

  private String content;
  private String addDate;

  private int studentId;
  private String studentFirstName;
  private String studentLastName;

  private String teacherName;

  public static RemarkDto fromEntity(Remark remark) {
    RemarkDto dto = new RemarkDto();
    dto.setRemarkId(remark.getRemarkId());
    dto.setTeacherId(remark.getTeacher().getUserId());
    dto.setTeacherFirstName(remark.getTeacher().getFirstname());
    dto.setTeacherLastName(remark.getTeacher().getLastname());
    dto.setTeacherName(
        remark.getTeacher().getFirstname() + " " + remark.getTeacher().getLastname());
    dto.setContent(remark.getContent());
    dto.setAddDate(remark.getAddDate().toString());
    dto.setStudentId(remark.getStudent().getUserId());
    dto.setStudentFirstName(remark.getStudent().getFirstname());
    dto.setStudentLastName(remark.getStudent().getLastname());
    return dto;
  }
}
