package com.codewithmosh.store.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers(String sort) {
        return userRepository.findAll(Sort.by(sort)).stream()
                .map(userMapper::toDto).toList();
    }

    public UserDto getUser(Long id) {
        var user = userRepository.findById(id).orElse(null);
        return userMapper.toDto(user);
    }
}
