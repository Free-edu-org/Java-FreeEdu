package com.byt.freeEdu.service.users;

import java.util.List;

import org.springframework.stereotype.Service;

import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.TeacherRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TeacherService{

  private final TeacherRepository teacherRepository;

  public TeacherService(TeacherRepository teacherRepository) {
    this.teacherRepository = teacherRepository;
  }

  public Teacher addTeacher(Teacher teacher) {
    return teacherRepository.save(teacher);
  }

  public Teacher getTeacherById(int id) {
    return teacherRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + id));
  }

  public List<Teacher> getAllTeachers() {
    return teacherRepository.findAll();
  }

  public Teacher updateTeacher(int id, Teacher updatedTeacher) {
    Teacher existingTeacher = teacherRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + id));
    existingTeacher.setFirstname(updatedTeacher.getFirstname());
    existingTeacher.setLastname(updatedTeacher.getLastname());
    existingTeacher.setEmail(updatedTeacher.getEmail());
    existingTeacher.setPassword(updatedTeacher.getPassword());
    return teacherRepository.save(existingTeacher);
  }

  public void deleteTeacher(int id) {
    teacherRepository.deleteById(id);
  }

  public void addUserToTeacher(int id) {
    teacherRepository.addUserToTeachers(id);
  }
}
