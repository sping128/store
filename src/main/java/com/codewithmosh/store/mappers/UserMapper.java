package com.codewithmosh.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.requests.RegisterUserRequest;
import com.codewithmosh.store.requests.UpdateUserRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterUserRequest dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(UpdateUserRequest dto, @MappingTarget User user);
}
