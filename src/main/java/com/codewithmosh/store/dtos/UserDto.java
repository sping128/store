package com.codewithmosh.store.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {
    @JsonIgnore // Don't show in response
    private Long id;
    @JsonProperty("name") // Rename the field in response
    private String name;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL) // Include only when it's not null
    private String phoneNumber;
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
