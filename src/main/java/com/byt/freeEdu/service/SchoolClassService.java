package com.byt.freeEdu.service;

import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.repository.SchoolClassRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchoolClassService{

  private final SchoolClassRepository schoolClassRepository;

  public SchoolClassService(SchoolClassRepository schoolClassRepository) {
    this.schoolClassRepository = schoolClassRepository;
  }

  public List<SchoolClass> getAllSchoolClass() {
    return new ArrayList<>(schoolClassRepository.findAll());
  }

  public SchoolClass getSchoolClassById(int id) {
    return schoolClassRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("School class not found with ID: " + id));
  }

  public SchoolClass getSchoolClassByName(String name) {
    return schoolClassRepository.findByName(name).orElseThrow(
        () -> new EntityNotFoundException("School class not found with name: " + name));
  }

  public List<SchoolClass> getAllClassesWithStudentCount() {
    return schoolClassRepository.findAll().stream().map(schoolClass -> {
      long studentCount = schoolClassRepository
          .countStudentsInClass(schoolClass.getSchoolClassId());
      schoolClass.setStudentCount(studentCount);
      return schoolClass;
    }).toList();
  }

  @Transactional
  public SchoolClass addSchoolClass(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Class name cannot be empty");
    }
    SchoolClass schoolClass = new SchoolClass(name);
    return schoolClassRepository.save(schoolClass);
  }

  @Transactional
  public void deleteSchoolClassById(int id) {
    SchoolClass schoolClass = schoolClassRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("School class not found with ID: " + id));
    schoolClassRepository.delete(schoolClass);
  }

  @Transactional
  public void deleteSchoolClassByName(String name) {
    SchoolClass schoolClass = schoolClassRepository.findByName(name).orElseThrow(
        () -> new EntityNotFoundException("School class not found with name: " + name));
    schoolClassRepository.delete(schoolClass);
  }
}
