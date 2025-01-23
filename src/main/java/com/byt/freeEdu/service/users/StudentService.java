package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.DTO.StudentDto;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserService userService;

    public StudentService(StudentRepository studentRepository, UserService userService) {
        this.studentRepository = studentRepository;
        this.userService = userService;
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Integer> getAllStudentIds() {
        return studentRepository.findAll().stream()
                .map(Student::getUserId)
                .collect(Collectors.toList());
    }

    public Student getStudentById(int id) {
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<StudentDto> getStudentsBySchoolClassId(int schoolClassId) {
        return studentRepository.getStudentsBySchoolClassId(schoolClassId)
                .stream()
                .map(id -> {
                    User user = userService.getUserById(id.getUserId());
                    return new StudentDto(user.getUserId(), user.getFirstname(), user.getLastname());
                })
                .collect(Collectors.toList());
    }

    public List<StudentDto> getAllStudentsDto() {
        return getAllStudentIds().stream()
                .map(id -> {
                    User user = userService.getUserById(id);
                    return new StudentDto(user.getUserId(), user.getFirstname(), user.getLastname());
                })
                .collect(Collectors.toList());
    }

    public Student updateStudent(int id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + id));
        existingStudent.setFirstname(updatedStudent.getFirstname());
        existingStudent.setLastname(updatedStudent.getLastname());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPassword(updatedStudent.getPassword());
        existingStudent.setParent(updatedStudent.getParent());
        existingStudent.setSchoolClass(updatedStudent.getSchoolClass());
        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(int id) {
        studentRepository.deleteById(id);
    }
}
