package com.mohan.api.controller;

import com.mohan.api.model.User;
import com.mohan.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty() || 
            user.getEmail() == null || user.getEmail().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User name and email cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty() || 
            user.getEmail() == null || user.getEmail().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User name and email cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.ok(service.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User with id " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }
}
