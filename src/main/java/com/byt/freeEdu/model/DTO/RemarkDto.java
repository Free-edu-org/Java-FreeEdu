package com.byt.freeEdu.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RemarkDto {
    private int remarkId;
    private String content;
    private String addDate;
    private String studentName;
}

