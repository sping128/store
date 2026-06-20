package com.codewithmosh.store.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.entities.Message;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @GetMapping("/hello")
    public Message sayHello() {
        return new Message("Hello World!");
    }
}