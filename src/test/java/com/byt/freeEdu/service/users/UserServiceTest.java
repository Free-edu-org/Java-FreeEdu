package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.enums.UserRole;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void addUser_emptyUsername_throwsException() {
        //given
        String emptyUsername = "";
        String email = "user@example.com";
        String password = "password123";
        UserRole userRole = UserRole.TEACHER;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(emptyUsername, "John", "Doe", email, password, userRole)
        );

        //then
        assertEquals("Username cannot be empty", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void addUser_emptyPassword_throwsException() {
        //given
        String username = "testUser";
        String email = "user@example.com";
        String emptyPassword = "";
        UserRole userRole = UserRole.TEACHER;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(username, "John", "Doe", email, emptyPassword, userRole)
        );

        //then
        assertEquals("Password cannot be empty", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void addUser_usernameAlreadyExists_throwsException() {
        //given
        User existingUser = new User("existingUser", "John", "Doe", "john.doe@example.com", "password123", UserRole.TEACHER);

        //when
        when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(existingUser.getUsername(), "John", "Doe", existingUser.getEmail(), existingUser.getPassword(), existingUser.getUser_role())
        );

        //then
        assertEquals("Username already exists: " + existingUser.getUsername(), exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(existingUser.getUsername());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void deleteUserById_nonExistingUser_throwsException() {
        //given
        int nonExistingUserId = 999;

        //when
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(nonExistingUserId));
        verify(userRepository, times(1)).findById(nonExistingUserId);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    public void deleteUserByUsername_nonExistingUser_throwsException() {
        //given
        String nonExistingUsername = "nonExistingUser";

        //when
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserByUsername(nonExistingUsername));
        verify(userRepository, times(1)).findByUsername(nonExistingUsername);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    public void addUser_emailAlreadyExists_throwsException() {
        //given
        User existingUser = new User("user123", "John", "Doe", "john.doe@example.com", "password123", UserRole.TEACHER);

        //when
        when(userRepository.existsByEmail(existingUser.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(existingUser.getUsername(), "John", "Doe", existingUser.getEmail(), existingUser.getPassword(), existingUser.getUser_role())
        );

        //then
        assertEquals("Email already exists: " + existingUser.getEmail(), exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(existingUser.getEmail());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void updateUser_nonExistingUser_throwsException() {
        //given
        int nonExistingUserId = 999;
        User updatedUser = new User("updatedUser", "Jane", "Doe", "janedoe@example.com", "newpassword123", UserRole.ADMIN);

        //when
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(nonExistingUserId, updatedUser));
        verify(userRepository, times(1)).findById(nonExistingUserId);
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void getUserById_existingId_returnsUser() {
        //given
        User user = createUser();
        int userId = user.getUserId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User result = userService.getUserById(userId);

        //then
        assertNotNull(result);
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void getAllUsers_returnsListOfUsers() {
        //given
        User user1 = createUser();
        User user2 = createUser();
        List<User> userList = List.of(user1, user2);

        //when
        when(userRepository.findAll()).thenReturn(userList);
        List<User> result = userService.getAllUsers();

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getUserById_nonExistingId_throwsException() {
        //given
        int nonExistingId = 999;

        //when
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(nonExistingId));
        verify(userRepository, times(1)).findById(nonExistingId);
    }

    @Test
    public void getUserByUsername_existingUsername_returnsUser() {
        //given
        User user = createUser();
        String username = user.getUsername();

        //when
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        User result = userService.getUserByUsername(username);

        //then
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getUserByUsername_nonExistingUsername_throwsException() {
        //given
        String nonExistingUsername = "nonExistingUser";

        //when
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsername(nonExistingUsername));
        verify(userRepository, times(1)).findByUsername(nonExistingUsername);
    }

    @Test
    public void addUser_validUser_savesAndReturnsUser() {
        //given
        User user = createUser();

        //when
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addUser(
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getUser_role()
        );

        //then
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void addUser_duplicateUsername_throwsException() {
        //given
        User user = createUser();

        //when
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        //then
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getUser_role()
        ));
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void updateUser_existingUser_updatesAndReturnsUser() {
        //given
        User existingUser = createUser();
        User updatedUser = createUpdatedUser();
        int userId = existingUser.getUserId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(userId, updatedUser);

        //then
        assertNotNull(result);
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void deleteUserById_existingId_deletesUser() {
        //given
        User user = createUser();
        int userId = user.getUserId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserById(userId);

        //then
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void deleteUserById_nonExistingId_throwsException() {
        //given
        int nonExistingId = 999;

        //when
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(nonExistingId));
        verify(userRepository, times(1)).findById(nonExistingId);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    public void deleteUserByUsername_existingUsername_deletesUser() {
        //given
        User user = createUser();
        String username = user.getUsername();

        //when
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserByUsername(username);

        //then
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void deleteUserByUsername_nonExistingUsername_throwsException() {
        //given
        String nonExistingUsername = "nonExistingUser";

        //when
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserByUsername(nonExistingUsername));
        verify(userRepository, times(1)).findByUsername(nonExistingUsername);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @ParameterizedTest
    @MethodSource("invalidUsernameAndEmail")
    public void addUser_invalidInput_throwsException(String username, String email, String password, String expectedMessage) {
        //given
        UserRole userRole = UserRole.STUDENT;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addUser(username, "John", "Doe", email, password, userRole)
        );

        //then
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    private User createUser() {
        return new User(
                1,
                "testUser",
                "John",
                "Doe",
                "johndoe@example.com",
                "password123",
                UserRole.TEACHER
        );
    }

    private User createUpdatedUser() {
        return new User(
                1,
                "updatedUser",
                "Jane",
                "Doe",
                "janedoe@example.com",
                "newpassword123",
                UserRole.ADMIN
        );
    }

    private static Stream<Arguments> invalidUsernameAndEmail() {
        return Stream.of(
                Arguments.of("", "email@example.com", "password", "Username cannot be empty"),
                Arguments.of("username", "", "password", "Email cannot be empty"),
                Arguments.of("username", "email@example.com", "", "Password cannot be empty")
        );
    }
}
