package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
