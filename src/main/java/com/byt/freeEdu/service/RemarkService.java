package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import com.byt.freeEdu.service.users.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemarkService {

    private final RemarkRepository remarkRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public RemarkService(RemarkRepository remarkRepository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.remarkRepository = remarkRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<Remark> getAllRemarks() {
        return new ArrayList<>(remarkRepository
                .findAll());
    }

    public Remark getRemarkById(int id) {
        return remarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Remark not found with ID: " + id));
    }

    public Remark getRemarkByContent(String name) {
        return remarkRepository.findByContent(name);
    }

    public List<Remark> getRemarksByTeacherId(int teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + teacherId));
        return remarkRepository.findByTeacher(teacher);
    }

    public List<Remark> getRemarksByStudentId(int studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));
        return remarkRepository.findByStudent(student);
    }

    @Transactional
    public Remark addRemark(String content, int studentId, int teacherId) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + teacherId));

        LocalDate date = LocalDate.now();
        Remark remark = new Remark(content, date, student, teacher);
        return remarkRepository.save(remark);
    }

    @Transactional
    public Remark updateRemark(int id, Remark updatedRemark) {
        Remark existingRemark = remarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Remark not found with ID: " + id));

        if (isNotEmpty(updatedRemark.getContent())) {
            existingRemark.setContent(updatedRemark.getContent());
        }

        existingRemark.setStudent(updatedRemark.getStudent());
        existingRemark.setTeacher(updatedRemark.getTeacher());
        return remarkRepository.save(existingRemark);
    }

    @Transactional
    public void deleteRemark(int id) {
        Remark remark = remarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Remark not found with ID: " + id));
        remarkRepository.delete(remark);
    }

    private boolean isNotEmpty(String content) {
        return content != null && !content.trim().isEmpty();
    }
}
