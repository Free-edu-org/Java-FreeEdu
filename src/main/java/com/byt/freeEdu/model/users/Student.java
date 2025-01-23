package com.byt.freeEdu.model.users;

import com.byt.freeEdu.model.SchoolClass;
import jakarta.persistence.*;
import lombok.*;

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