package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
    Optional<SchoolClass> findByName(String name);
}
