package com.byt.freeEdu.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
public class Student extends User {

    @NonNull
    @OneToOne
    @Column(name = "parent", nullable = false)
    private Parent parent;
}
