package com.byt.freeEdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.byt.freeEdu.model.users.Parent;

import jakarta.transaction.Transactional;

public interface ParentRepository extends JpaRepository<Parent, Integer> {

    @Modifying
    @Transactional
    @Query(value = "CALL addUserToParents(:id, :contactInfo)", nativeQuery = true)
    void addUserToParents(int id, String contactInfo);
}
