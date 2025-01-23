package com.byt.freeEdu.service.users;

import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.users.Student;
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

    public StudentService(RemarkRepository remarkRepository, RemarkMapper remarkMapper, StudentRepository studentRepository) {
        this.remarkRepository = remarkRepository;
        this.remarkMapper = remarkMapper;
        this.studentRepository = studentRepository;
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
