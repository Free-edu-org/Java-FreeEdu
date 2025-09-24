package com.byt.freeEdu.service.users;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.enums.UserRole;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public List<UserDto> getAllUsers() {
        return new ArrayList<>(userRepository.findAll().stream().map(userMapper::toDto).toList());
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public Boolean addUser(UserDto newUser) {
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new IllegalArgumentException("Istnieje użytkownik o podanej nazwie użytkownika");
        }
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new IllegalArgumentException("Istnieję użytkownik z takim emailem");
        }

        String hashedPassword = passwordEncoder.encode(newUser.getPassword());

        User user = new User(newUser.getUsername(), newUser.getFirstname(), newUser.getLastname(), newUser.getEmail(), hashedPassword, UserRole.UNKNOWN);

        userRepository.save(user);
        return true;
    }

    @Transactional
    public User updateUser(int id, UserDto updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        if (hasText(updatedUser.getUsername())) {
            existingUser.setUsername(updatedUser.getUsername().trim());
        }
        if (hasText(updatedUser.getFirstname())) {
            existingUser.setFirstname(updatedUser.getFirstname().trim());
        }
        if (hasText(updatedUser.getLastname())) {
            existingUser.setLastname(updatedUser.getLastname().trim());
        }
        if (hasText(updatedUser.getEmail())) {
            existingUser.setEmail(updatedUser.getEmail().trim());
        }

        if (hasText(updatedUser.getRole())) {
            try {
                existingUser.setUserRole(UserRole.valueOf(updatedUser.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + updatedUser.getRole());
            }
        }

        if (hasText(updatedUser.getPassword())) {
            String hashedPassword = passwordEncoder.encode(updatedUser.getPassword().trim());
            existingUser.setPassword(hashedPassword);
        }

        return existingUser; // save() nie jest potrzebne przy @Transactional
    }

    @Transactional
    public void deleteUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username));
        userRepository.delete(user);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
