package com.byt.freeEdu.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
public class Student extends User {

    @NonNull
    @Column(name = "parent_id", nullable = false)
    private UUID parentId;
}
