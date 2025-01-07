package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
