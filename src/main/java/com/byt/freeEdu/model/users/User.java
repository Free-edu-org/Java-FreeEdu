package com.byt.freeEdu.model.users;

import com.byt.freeEdu.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private int userId;

    @NonNull
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NonNull
    @Column(name = "firstname", nullable = false)
    private String firstname;

    @NonNull
    @Column(name = "lastname", nullable = false)
    private String lastname;

    @NonNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NonNull
    @Column(name = "password", nullable = false)
    private String password;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole user_role;
}
