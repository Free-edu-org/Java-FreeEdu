package com.byt.freeEdu.model.users;

import com.byt.freeEdu.model.SchoolClass;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student")
public class Student extends User {

    @NonNull
    @ManyToOne // Relacja Many-to-One z Parent
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;
}
