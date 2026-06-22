package com.codewithmosh.store.services;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.events.UserRegisteredEvent;
import com.codewithmosh.store.exceptions.InvalidPasswordException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.requests.RegisterUserRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserDto registerUser(RegisterUserRequest request) {
        var user = userMapper.toEntity(request);
        userRepository.save(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getName(), user.getEmail()));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(String sort) {
        return userRepository.findAll(Sort.by(sort)).stream()
                .map(userMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        var user = userRepository.findById(id).orElse(null);
        return userMapper.toDto(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null)
            throw new UserNotFoundException();
        if (!user.getPassword().equals(request.getOldPassword()))
            throw new InvalidPasswordException();
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
