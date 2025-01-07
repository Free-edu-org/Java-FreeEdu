package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Integer> {
}
