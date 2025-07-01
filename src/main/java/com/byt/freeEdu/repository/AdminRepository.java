package com.byt.freeEdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.byt.freeEdu.model.users.Admin;

import jakarta.transaction.Transactional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CALL addUserToAdmins(:id)", nativeQuery = true)
    void addUserToAdmins(int id);
}
