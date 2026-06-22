package com.codewithmosh.store.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisteredEvent {
    private String name;
    private String email;
}
