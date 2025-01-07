package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.users.Parent;
import com.byt.freeEdu.repository.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentService {
    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public Parent addParent(Parent parent) {
        return parentRepository.save(parent);
    }

    public Parent getParentById(int id) {
        return parentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Parent not found with ID: " + id));
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Parent updateParent(int id, Parent updatedParent) {
        Parent existingParent = parentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Parent not found with ID: " + id));
        existingParent.setFirstname(updatedParent.getFirstname());
        existingParent.setLastname(updatedParent.getLastname());
        existingParent.setEmail(updatedParent.getEmail());
        existingParent.setPassword(updatedParent.getPassword());
        existingParent.setContactInfo(updatedParent.getContactInfo());
        return parentRepository.save(existingParent);
    }

    public void deleteParent(int id) {
        parentRepository.deleteById(id);
    }
}
