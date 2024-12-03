package com.byt.freeEdu.model;

import com.byt.freeEdu.model.enums.SubjectEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grade")
public class Grade {
    @Id
    @GeneratedValue
    @Column(name = "grade_id", updatable = false, nullable = false)
    private UUID gradeId;

    @Column(name = "value", nullable = false)
    private double value;

    @NonNull
    @Column(name = "grade_date")
    private LocalDate gradeDate;

    @NonNull
    @Column(name = "subject")
    private SubjectEnum subject;
}
