package com.byt.freeEdu.service.users;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.enums.UserRole;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    public User getUserById(int id) {
        return userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public Boolean addUser(String username, String firstname, String lastname, String email,
                           String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Istnieje użytkownik o podanej nazwie użytkownika");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Istnieję użytkownik z takim emailem");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(username, firstname, lastname, email, hashedPassword, UserRole.UNKNOWN);

        userRepository.save(user);
        return true;
    }

    @Transactional
    public User updateUser(int id, UserDto updatedUser) {
        User existingUser = userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getFirstname() != null && !updatedUser.getFirstname().trim().isEmpty()) {
            existingUser.setFirstname(updatedUser.getFirstname());
        }
        if (updatedUser.getLastname() != null && !updatedUser.getLastname().trim().isEmpty()) {
            existingUser.setLastname(updatedUser.getLastname());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        existingUser.setUserRole(UserRole.valueOf(updatedUser.getRole()));

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUserById(int id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username));
        userRepository.delete(user);
    }

}
