package com.byt.freeEdu.model;

import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int gradeId;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false)
    private SubjectEnum subject;

    @NonNull
    @Column(name = "value", nullable = false)
    private double value;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @NonNull
    @Column(name = "add_date", nullable = false)
    private LocalDate gradeDate;
}
