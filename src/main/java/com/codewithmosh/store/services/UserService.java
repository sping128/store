package com.codewithmosh.store.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail())).toList();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
