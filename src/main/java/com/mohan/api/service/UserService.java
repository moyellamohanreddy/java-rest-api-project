package com.mohan.api.service;

import com.mohan.api.model.User;
import com.mohan.api.repository.UserRepository;
import com.mohan.api.exception.UserNotFoundException;
import com.mohan.api.exception.DuplicateUserException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found in the database");
        }
        return users;
    }

    public User getUserById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User addUser(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateUserException("User with email '" + user.getEmail() + "' already exists");
        }
        return repository.save(user);
    }

    public User updateUser(Long id, User user) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        
        // Check if new email already exists for a different user
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            User existingUser = repository.findByEmail(user.getEmail()).get();
            if (!existingUser.getId().equals(id)) {
                throw new DuplicateUserException("User with email '" + user.getEmail() + "' already exists");
            }
        }
        
        user.setId(id);
        return repository.save(user);
    }

    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
