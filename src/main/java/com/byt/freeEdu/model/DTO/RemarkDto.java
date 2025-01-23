package com.byt.freeEdu.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RemarkDto {

    private int remarkId;

    private int teacherId;
    private String teacherFirstName;
    private String teacherLastName;

    private String content;
    private String addDate;

    private int studentId;
    private String studentFirstName;
    private String studentLastName;
}

