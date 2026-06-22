package com.codewithmosh.store.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.requests.RegisterUserRequest;
import com.codewithmosh.store.requests.UpdateUserRequest;
import com.codewithmosh.store.services.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(
            @RequestHeader(required = false, name = "x-auth-token") String authToken,
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        System.out.println(authToken);
        if (!Set.of("name", "email").contains(sortBy)) {
            sortBy = "name";
        }
        return userService.getAllUsers(sortBy);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var userDto = userService.getUser(id);
        if (userDto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody RegisterUserRequest dto,
            UriComponentsBuilder uriBuilder) {
        var userDto = userService.registerUser(dto);
        var uri = uriBuilder.path("api/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id, @RequestBody UpdateUserRequest dto) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null)
            return ResponseEntity.notFound().build();
        userMapper.update(dto, user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null)
            return ResponseEntity.notFound().build();
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

}
