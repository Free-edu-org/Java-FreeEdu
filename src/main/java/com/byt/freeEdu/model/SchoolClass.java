package com.byt.freeEdu.model;

import com.byt.freeEdu.model.DTO.StudentDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "class")
public class SchoolClass{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, updatable = false, nullable = false)
  private int schoolClassId;

  @NonNull
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Transient
  private long studentCount;

  @Transient
  private List<StudentDto> students;
}
