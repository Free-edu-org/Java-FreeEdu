package com.byt.freeEdu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class")
public class SchoolClass {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int schoolClassId;

    @NonNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
