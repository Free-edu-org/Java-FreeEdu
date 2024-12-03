package com.byt.freeEdu.model;

import com.byt.freeEdu.model.enums.AttendanceEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
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
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue
    @Column(name = "attendance_id", updatable = false, nullable = false)
    private UUID attendanceId;

    @NonNull
    @ManyToOne
    @Column(name = "student", nullable = false)
    private Student student;

    @NonNull
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @NonNull
    @Column(name = "status", nullable = false)
    private AttendanceEnum status;

    @NonNull
    @ManyToOne
    @Column(name = "teacher", nullable = false)
    private Teacher teacher;
}
