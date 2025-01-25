package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Parent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ParentRepository extends JpaRepository<Parent, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO FreeEduDB.parent(id, contact_info) VALUES (:id, :contactInfo)", nativeQuery = true)
    void addUserToParents(int id, String contactInfo);
}
