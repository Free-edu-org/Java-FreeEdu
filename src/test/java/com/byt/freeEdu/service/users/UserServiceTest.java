package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.enums.UserRole;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest{

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  public void getAllUsers_returnsListOfUsers() {
    // given
    User user1 = new User("user1", "John", "Doe", "john.doe@example.com", "password",
        UserRole.UNKNOWN);
    User user2 = new User("user2", "Alice", "Smith", "alice.smith@example.com", "password",
        UserRole.UNKNOWN);
    when(userRepository.findAll()).thenReturn(List.of(user1,user2));

    // when
    List<User> users = userService.getAllUsers();

    // then
    assertNotNull(users);
    assertEquals(2,users.size());
    verify(userRepository,times(1)).findAll();
  }

  @Test
  public void getUserById_userNotFound_throwsException() {
    // given
    int userId = 999;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> userService.getUserById(userId));
  }

  @Test
  public void getUserById_returnsUser() {
    // given
    int userId = 1;
    User user = new User();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // when
    User result = userService.getUserById(userId);

    // then
    assertNotNull(result);
    verify(userRepository,times(1)).findById(userId);
  }

  @Test
  public void addUser_duplicateUsername_throwsException() {
    // given
    when(userRepository.existsByUsername("duplicate")).thenReturn(true);

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> userService.addUser("duplicate","John","Doe","john.doe@example.com","password"));
    verify(userRepository,never()).save(any(User.class));
  }

  @Test
  public void addUser_duplicateEmail_throwsException() {
    // given
    when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> userService.addUser("unique","John","Doe","duplicate@example.com","password"));
    verify(userRepository,never()).save(any(User.class));
  }

  @Test
  public void addUser_savesUserSuccessfully() {
    // given
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String rawPassword = "password";
    String hashedPassword = passwordEncoder.encode(rawPassword);

    User user = new User("unique", "John", "Doe", "john.doe@example.com", hashedPassword,
        UserRole.UNKNOWN);
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // when
    Boolean result = userService.addUser("unique","John","Doe","john.doe@example.com",rawPassword);

    // then
    assertTrue(result);
    verify(userRepository,times(1)).save(any(User.class));
  }

  @Test
  public void updateUser_userNotFound_throwsException() {
    // given
    int userId = 999;
    UserDto updatedUser = new UserDto();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> userService.updateUser(userId,updatedUser));
  }

  @Test
  public void updateUser_updatesUserSuccessfully() {
    // given
    int userId = 1;
    User existingUser = new User("user1", "John", "Doe", "john.doe@example.com", "password",
        UserRole.UNKNOWN);
    UserDto updatedUser = new UserDto();
    updatedUser.setUsername("updatedUser");
    updatedUser.setRole("ADMIN");

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(existingUser)).thenReturn(existingUser);

    // when
    User result = userService.updateUser(userId,updatedUser);

    // then
    assertNotNull(result);
    assertEquals("updatedUser",result.getUsername());
    assertEquals(UserRole.ADMIN,result.getUser_role());
    verify(userRepository,times(1)).save(existingUser);
  }

  @Test
  public void deleteUserById_userNotFound_throwsException() {
    // given
    int userId = 999;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> userService.deleteUserById(userId));
    verify(userRepository,never()).delete(any(User.class));
  }

  @Test
  public void deleteUserById_deletesUserSuccessfully() {
    // given
    int userId = 1;
    User user = new User();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // when
    userService.deleteUserById(userId);

    // then
    verify(userRepository,times(1)).delete(user);
  }

  @Test
  public void deleteUserByUsername_userNotFound_throwsException() {
    // given
    String username = "nonexistent";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> userService.deleteUserByUsername(username));
    verify(userRepository,never()).delete(any(User.class));
  }

  @Test
  public void deleteUserByUsername_deletesUserSuccessfully() {
    // given
    String username = "existingUser";
    User user = new User();
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    // when
    userService.deleteUserByUsername(username);

    // then
    verify(userRepository,times(1)).delete(user);
  }
}
