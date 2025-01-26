package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.users.Admin;
import com.byt.freeEdu.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Test
    void addAdmin_savesAdminSuccessfully() {
        // given
        Admin admin = new Admin();
        admin.setFirstname("John");
        when(adminRepository.save(admin)).thenReturn(admin);

        // when
        Admin result = adminService.addAdmin(admin);

        // then
        assertNotNull(result);
        assertEquals("John", result.getFirstname());
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void getAdminById_adminNotFound_throwsException() {
        // given
        int adminId = 999;
        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> adminService.getAdminById(adminId));
        verify(adminRepository, times(1)).findById(adminId);
    }

    @Test
    void getAdminById_returnsAdmin() {
        // given
        int adminId = 1;
        Admin admin = new Admin();
        admin.setFirstname("Alice");
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // when
        Admin result = adminService.getAdminById(adminId);

        // then
        assertNotNull(result);
        assertEquals("Alice", result.getFirstname());
        verify(adminRepository, times(1)).findById(adminId);
    }

    @Test
    void getAllAdmins_returnsListOfAdmins() {
        // given
        Admin admin1 = new Admin();
        Admin admin2 = new Admin();
        when(adminRepository.findAll()).thenReturn(List.of(admin1, admin2));

        // when
        List<Admin> result = adminService.getAllAdmins();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(adminRepository, times(1)).findAll();
    }

    @Test
    void updateAdmin_adminNotFound_throwsException() {
        // given
        int adminId = 999;
        Admin updatedAdmin = new Admin();
        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> adminService.updateAdmin(adminId, updatedAdmin));
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void deleteAdmin_deletesAdminSuccessfully() {
        // given
        int adminId = 1;

        // when
        adminService.deleteAdmin(adminId);

        // then
        verify(adminRepository, times(1)).deleteById(adminId);
    }

    @Test
    void addUserToAdmin_executesSuccessfully() {
        // given
        int userId = 1;

        // when
        adminService.addUserToAdmin(userId);

        // then
        verify(adminRepository, times(1)).addUserToAdmins(userId);
    }
}
