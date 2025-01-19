package com.byt.freeEdu.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentDto {
    private int studentId;
    private String firstname;
    private String lastname;
}