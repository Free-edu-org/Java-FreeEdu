package com.byt.freeEdu.model.users;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "admin")
public class Admin extends User{

}
