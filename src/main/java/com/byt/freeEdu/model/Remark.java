package com.byt.freeEdu.model;

import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "remark")
public class Remark {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private int remarkId;

    @NonNull
    @Column(name = "content", nullable = false)
    private String content;

    @NonNull
    @Column(name = "add_date", nullable = false)
    private LocalDate addDate;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
}
