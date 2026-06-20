package com.codewithmosh.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.dtos.UpdateUserDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterUserDto dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(UpdateUserDto dto, @MappingTarget User user);
}
