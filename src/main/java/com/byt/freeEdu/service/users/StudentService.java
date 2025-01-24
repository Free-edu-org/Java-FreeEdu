package com.byt.freeEdu.service.users;

import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.StudentDto;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final RemarkRepository remarkRepository;

    private final RemarkMapper remarkMapper;
    private final StudentRepository studentRepository;
    private final UserService userService;


    public StudentService(RemarkRepository remarkRepository, RemarkMapper remarkMapper, StudentRepository studentRepository, UserService userService) {
        this.remarkRepository = remarkRepository;
        this.remarkMapper = remarkMapper;
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
    public List<RemarkDto> getRemarksForStudent(int studentId) {
        return remarkRepository.findByStudent_UserId(studentId) // Użycie poprawionej metody
                .stream()
                .map(remarkMapper::toDto)
                .collect(Collectors.toList());
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

    public void changeStudentClass(int studentId, SchoolClass schoolClass) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));
        student.setSchoolClass(schoolClass);
        studentRepository.save(student);
    }
}
