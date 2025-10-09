package com.byt.freeEdu.service.users;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.model.users.Admin;
import com.byt.freeEdu.repository.AdminRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest{

  @InjectMocks
  private AdminService adminService;

  @Mock
  private AdminRepository adminRepository;

  @Test
  @DisplayName("addAdmin: zapisuje i zwraca admina")
  void addAdmin_savesAndReturns() {
    Admin payload = new Admin();
    payload.setFirstname("John");
    when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> inv.getArgument(0));

    Admin out = adminService.addAdmin(payload);

    assertNotNull(out);
    assertEquals("John",out.getFirstname());

    ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
    verify(adminRepository,times(1)).save(captor.capture());
    assertEquals("John",captor.getValue().getFirstname());
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("getAdminById: gdy istnieje, zwraca admina")
  void getAdminById_returnsAdmin() {
    int id = 1;
    Admin admin = new Admin();
    admin.setUserId(id);
    admin.setFirstname("Alice");
    when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

    Admin out = adminService.getAdminById(id);

    assertNotNull(out);
    assertEquals(id,out.getUserId());
    assertEquals("Alice",out.getFirstname());
    verify(adminRepository,times(1)).findById(id);
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("getAdminById: gdy brak, rzuca EntityNotFoundException z komunikatem zawierającym ID")
  void getAdminById_notFound_throwsWithMessage() {
    int id = 999;
    when(adminRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> adminService.getAdminById(id));

    assertTrue(ex.getMessage() != null && ex.getMessage().contains(String.valueOf(id)));
    verify(adminRepository,times(1)).findById(id);
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("getAllAdmins: zwraca listę adminów")
  void getAllAdmins_returnsList() {
    when(adminRepository.findAll()).thenReturn(List.of(new Admin(),new Admin()));

    List<Admin> out = adminService.getAllAdmins();

    assertNotNull(out);
    assertEquals(2,out.size());
    verify(adminRepository,times(1)).findAll();
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("updateAdmin: gdy brak admina, rzuca EntityNotFoundException z komunikatem")
  void updateAdmin_notFound_throwsWithMessage() {
    int id = 1234;
    when(adminRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> adminService.updateAdmin(id,new Admin()));

    assertTrue(ex.getMessage() != null && ex.getMessage().contains(String.valueOf(id)));
    verify(adminRepository,times(1)).findById(id);
    verify(adminRepository,never()).save(any());
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("updateAdmin: aktualizuje wszystkie pola i zapisuje (happy path)")
  void updateAdmin_updatesAllFieldsAndSaves() {
    int id = 2;
    Admin existing = new Admin();
    existing.setUserId(id);
    existing.setFirstname("Old");
    existing.setLastname("Name");
    existing.setEmail("old@example.com");
    existing.setPassword("oldpass");

    Admin patch = new Admin();
    patch.setFirstname("New");
    patch.setLastname("Surname");
    patch.setEmail("new@example.com");
    patch.setPassword("newpass");

    when(adminRepository.findById(id)).thenReturn(Optional.of(existing));
    when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> inv.getArgument(0));

    Admin out = adminService.updateAdmin(id,patch);

    assertNotNull(out);
    assertEquals(id,out.getUserId());
    assertEquals("New",out.getFirstname());
    assertEquals("Surname",out.getLastname());
    assertEquals("new@example.com",out.getEmail());
    assertEquals("newpass",out.getPassword());

    ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
    verify(adminRepository).findById(id);
    verify(adminRepository).save(captor.capture());
    Admin saved = captor.getValue();
    assertEquals(id,saved.getUserId());
    assertEquals("New",saved.getFirstname());
    assertEquals("Surname",saved.getLastname());
    assertEquals("new@example.com",saved.getEmail());
    assertEquals("newpass",saved.getPassword());
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("updateAdmin: przy patchu z null-ami rzuca NullPointerException (pola non-null w modelu)")
  void updateAdmin_overwritesWithNulls_throwsNpe() {
    int id = 3;
    Admin existing = new Admin();
    existing.setUserId(id);
    existing.setFirstname("Old");
    existing.setLastname("Name");
    existing.setEmail("old@example.com");
    existing.setPassword("oldpass");

    Admin patch = new Admin();

    when(adminRepository.findById(id)).thenReturn(Optional.of(existing));

    assertThrows(NullPointerException.class,() -> adminService.updateAdmin(id,patch));
    verify(adminRepository).findById(id);
    verify(adminRepository,never()).save(any(Admin.class));
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("deleteAdmin: deleguje do deleteById")
  void deleteAdmin_deletesAdminSuccessfully() {
    int id = 10;

    adminService.deleteAdmin(id);

    verify(adminRepository,times(1)).deleteById(id);
    verifyNoMoreInteractions(adminRepository);
  }

  @Test
  @DisplayName("addUserToAdmin: deleguje do repozytorium")
  void addUserToAdmin_executesSuccessfully() {
    int userId = 7;

    adminService.addUserToAdmin(userId);

    verify(adminRepository,times(1)).addUserToAdmins(userId);
    verifyNoMoreInteractions(adminRepository);
  }
}
