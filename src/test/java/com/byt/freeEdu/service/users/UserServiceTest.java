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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.enums.UserRole;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest{

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("getAllUsers: zwraca listę użytkowników")
  void getAllUsers_returnsListOfUsers() {
    User u1 = new User("user1", "John", "Doe", "john.doe@example.com", "pw", UserRole.UNKNOWN);
    User u2 = new User("user2", "Alice", "Smith", "alice@example.com", "pw", UserRole.UNKNOWN);
    when(userRepository.findAll()).thenReturn(List.of(u1,u2));

    List<User> result = userService.getAllUsers();

    assertNotNull(result);
    assertEquals(2,result.size());
    verify(userRepository).findAll();
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("getUserById: zwraca użytkownika gdy istnieje")
  void getUserById_returnsUser() {
    int id = 1;
    User u = new User();
    when(userRepository.findById(id)).thenReturn(Optional.of(u));

    User out = userService.getUserById(id);

    assertNotNull(out);
    verify(userRepository).findById(id);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("getUserById: gdy brak, rzuca EntityNotFoundException z komunikatem")
  void getUserById_userNotFound_throwsException() {
    int id = 999;
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
        () -> userService.getUserById(id));
    assertTrue(ex.getMessage() != null && ex.getMessage().contains(String.valueOf(id)));

    verify(userRepository).findById(id);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("getUserByUsername: zwraca użytkownika albo null")
  void getUserByUsername_behaviour() {
    when(userRepository.findByUsername("exists"))
        .thenReturn(Optional.of(new User("exists", "A", "B", "a@b", "pw", UserRole.UNKNOWN)));
    when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

    assertNotNull(userService.getUserByUsername("exists"));
    assertNull(userService.getUserByUsername("missing"));

    verify(userRepository).findByUsername("exists");
    verify(userRepository).findByUsername("missing");
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("getUserByEmail: zwraca użytkownika albo null")
  void getUserByEmail_behaviour() {
    when(userRepository.findByEmail("e@x"))
        .thenReturn(Optional.of(new User("u", "A", "B", "e@x", "pw", UserRole.UNKNOWN)));
    when(userRepository.findByEmail("none")).thenReturn(Optional.empty());

    assertNotNull(userService.getUserByEmail("e@x"));
    assertNull(userService.getUserByEmail("none"));

    verify(userRepository).findByEmail("e@x");
    verify(userRepository).findByEmail("none");
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("addUser: odrzuca puste username/email/password")
  void addUser_validation_emptyFields_throw() {
    UserDto dto = new UserDto();

    dto.setUsername(null);
    dto.setEmail("a@b");
    dto.setPassword("x");
    assertThrows(IllegalArgumentException.class,() -> userService.addUser(dto));

    dto.setUsername("u");
    dto.setEmail(" ");
    dto.setPassword("x");
    assertThrows(IllegalArgumentException.class,() -> userService.addUser(dto));

    dto.setUsername("u");
    dto.setEmail("a@b");
    dto.setPassword(" ");
    assertThrows(IllegalArgumentException.class,() -> userService.addUser(dto));

    verifyNoInteractions(userRepository);
  }

  @Test
  @DisplayName("addUser: odrzuca duplikat username")
  void addUser_duplicateUsername_throwsException() {
    UserDto dto = new UserDto();
    dto.setUsername("dup");
    dto.setFirstname("John");
    dto.setLastname("Doe");
    dto.setEmail("john@ex.com");
    dto.setPassword("secret");

    when(userRepository.existsByUsername("dup")).thenReturn(true);

    assertThrows(IllegalArgumentException.class,() -> userService.addUser(dto));
    verify(userRepository).existsByUsername("dup");
    verify(userRepository,never()).save(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("addUser: odrzuca duplikat email")
  void addUser_duplicateEmail_throwsException() {
    UserDto dto = new UserDto();
    dto.setUsername("unique");
    dto.setFirstname("John");
    dto.setLastname("Doe");
    dto.setEmail("dup@ex.com");
    dto.setPassword("secret");

    when(userRepository.existsByUsername("unique")).thenReturn(false);
    when(userRepository.existsByEmail("dup@ex.com")).thenReturn(true);

    assertThrows(IllegalArgumentException.class,() -> userService.addUser(dto));
    verify(userRepository).existsByUsername("unique");
    verify(userRepository).existsByEmail("dup@ex.com");
    verify(userRepository,never()).save(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("addUser: haszuje hasło i zapisuje użytkownika")
  void addUser_savesUserSuccessfully_withHashedPassword() {
    UserDto dto = new UserDto();
    dto.setUsername("unique");
    dto.setFirstname("John");
    dto.setLastname("Doe");
    dto.setEmail("john@ex.com");
    dto.setPassword("password");

    when(userRepository.existsByUsername("unique")).thenReturn(false);
    when(userRepository.existsByEmail("john@ex.com")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    Boolean result = userService.addUser(dto);

    assertTrue(result);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).existsByUsername("unique");
    verify(userRepository).existsByEmail("john@ex.com");
    verify(userRepository).save(captor.capture());

    User saved = captor.getValue();
    assertEquals("unique",saved.getUsername());
    assertEquals("John",saved.getFirstname());
    assertEquals("Doe",saved.getLastname());
    assertEquals("john@ex.com",saved.getEmail());
    assertNotNull(saved.getPassword());
    assertNotEquals("password",saved.getPassword());
    assertTrue(new BCryptPasswordEncoder().matches("password",saved.getPassword()));
    assertEquals(UserRole.UNKNOWN,saved.getUserRole());

    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("updateUser: gdy brak użytkownika, rzuca EntityNotFoundException")
  void updateUser_userNotFound_throwsException() {
    int id = 999;
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> userService.updateUser(id,new UserDto()));
    verify(userRepository).findById(id);
    verify(userRepository,never()).save(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("updateUser: aktualizuje pola tekstowe i rolę; hasła nie zmienia gdy puste")
  void updateUser_updatesFieldsAndRole_noPasswordChangeWhenEmpty() {
    int id = 1;
    User existing = new User("oldu", "Old", "User", "old@ex.com", "oldhash", UserRole.UNKNOWN);

    UserDto patch = new UserDto();
    patch.setUsername("newu");
    patch.setFirstname("New");
    patch.setLastname("Name");
    patch.setEmail("new@ex.com");
    patch.setRole("ADMIN");
    patch.setPassword("  ");

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User out = userService.updateUser(id,patch);

    assertEquals("newu",out.getUsername());
    assertEquals("New",out.getFirstname());
    assertEquals("Name",out.getLastname());
    assertEquals("new@ex.com",out.getEmail());
    assertEquals(UserRole.ADMIN,out.getUserRole());
    assertEquals("oldhash",out.getPassword());
    verify(userRepository).findById(id);
    verify(userRepository).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("updateUser: aktualizuje hasło – zapisuje z haszem")
  void updateUser_updatesPassword_hashes() {
    int id = 2;
    User existing = new User("u", "A", "B", "a@b", "oldhash", UserRole.UNKNOWN);

    UserDto patch = new UserDto();
    patch.setRole("ADMIN");
    patch.setPassword("secret123");

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User out = userService.updateUser(id,patch);

    assertNotNull(out.getPassword());
    assertNotEquals("secret123",out.getPassword());
    assertTrue(new BCryptPasswordEncoder().matches("secret123",out.getPassword()));
    verify(userRepository).findById(id);
    verify(userRepository).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("deleteUserById: gdy brak, rzuca EntityNotFoundException")
  void deleteUserById_userNotFound_throwsException() {
    int id = 999;
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> userService.deleteUserById(id));
    verify(userRepository).findById(id);
    verify(userRepository,never()).delete(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("deleteUserById: usuwa istniejącego użytkownika")
  void deleteUserById_deletesUserSuccessfully() {
    int id = 1;
    User u = new User();
    when(userRepository.findById(id)).thenReturn(Optional.of(u));

    userService.deleteUserById(id);

    verify(userRepository).findById(id);
    verify(userRepository).delete(u);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("deleteUserByUsername: gdy brak, rzuca EntityNotFoundException")
  void deleteUserByUsername_userNotFound_throwsException() {
    String username = "missing";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> userService.deleteUserByUsername(username));
    verify(userRepository).findByUsername(username);
    verify(userRepository,never()).delete(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @DisplayName("deleteUserByUsername: usuwa istniejącego użytkownika")
  void deleteUserByUsername_deletesUserSuccessfully() {
    String username = "exists";
    User u = new User();
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(u));

    userService.deleteUserByUsername(username);

    verify(userRepository).findByUsername(username);
    verify(userRepository).delete(u);
    verifyNoMoreInteractions(userRepository);
  }
}
