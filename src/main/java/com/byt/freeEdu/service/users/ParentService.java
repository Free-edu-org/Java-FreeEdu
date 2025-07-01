package com.byt.freeEdu.service.users;

import java.util.List;

import org.springframework.stereotype.Service;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.Parent;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.repository.ParentRepository;
import com.byt.freeEdu.repository.StudentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ParentService {

    private final ParentRepository parentRepository;

    private final StudentRepository studentRepository;

    // Konstruktor z wymaganymi zależnościami
    public ParentService(ParentRepository parentRepository, StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    // Dodaj rodzica
    public Parent addParent(Parent parent) {
        return parentRepository.save(parent);
    }

    // Pobierz rodzica po ID
    public Parent getParentById(int id) {
        return parentRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Parent not found with ID: " + id));
    }

    // Pobierz wszystkich rodziców
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    // Zaktualizuj rodzica
    public Parent updateParent(int id, Parent updatedParent) {
        Parent existingParent = parentRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Parent not found with ID: " + id));
        existingParent.setFirstname(updatedParent.getFirstname());
        existingParent.setLastname(updatedParent.getLastname());
        existingParent.setEmail(updatedParent.getEmail());
        existingParent.setPassword(updatedParent.getPassword());
        existingParent.setContactInfo(updatedParent.getContactInfo());
        return parentRepository.save(existingParent);
    }

    // Usuń rodzica
    public void deleteParent(int id) {
        parentRepository.deleteById(id);
    }

    // Pobierz uczniów powiązanych z rodzicem
    public List<Student> getStudentsByParentId(int parentId) {
        return studentRepository.findByParentUserId(parentId); // Użyj poprawnej metody
    }

    public void addUserToParent(int id, UserDto user) {
        parentRepository.addUserToParents(id, user.getContactInfo());
    }
}
