package com.byt.freeEdu.service.users;

import java.util.List;

import org.springframework.stereotype.Service;

import com.byt.freeEdu.model.users.Admin;
import com.byt.freeEdu.repository.AdminRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminService{

  private final AdminRepository adminRepository;

  public AdminService(AdminRepository adminRepository) {
    this.adminRepository = adminRepository;
  }

  public Admin addAdmin(Admin admin) {
    return adminRepository.save(admin);
  }

  public Admin getAdminById(int id) {
    return adminRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Admin not found with ID: " + id));
  }

  public List<Admin> getAllAdmins() {
    return adminRepository.findAll();
  }

  public Admin updateAdmin(int id, Admin updatedAdmin) {
    Admin existingAdmin = adminRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Admin not found with ID: " + id));
    existingAdmin.setFirstname(updatedAdmin.getFirstname());
    existingAdmin.setLastname(updatedAdmin.getLastname());
    existingAdmin.setEmail(updatedAdmin.getEmail());
    existingAdmin.setPassword(updatedAdmin.getPassword());
    return adminRepository.save(existingAdmin);
  }

  public void deleteAdmin(int id) {
    adminRepository.deleteById(id);
  }

  public void addUserToAdmin(int id) {
    adminRepository.addUserToAdmins(id);
  }
}
