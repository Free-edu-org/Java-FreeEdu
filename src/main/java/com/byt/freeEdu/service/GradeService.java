package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.repository.GradeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {
    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public Grade getGradeById(int id) {
        return gradeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grade not found with ID: " + id));
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Grade updateGrade(int id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grade not found with ID: " + id));
        existingGrade.setSubject(updatedGrade.getSubject());
        existingGrade.setValue(updatedGrade.getValue());
        existingGrade.setStudent(updatedGrade.getStudent());
        existingGrade.setTeacher(updatedGrade.getTeacher());
        existingGrade.setGradeDate(updatedGrade.getGradeDate());

        return gradeRepository.save(existingGrade);
    }

    public void deleteGrade(int id) {
        gradeRepository.deleteById(id);
    }
}
