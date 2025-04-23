package com.workshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingsController {

    @GetMapping
    public ResponseEntity<String> sayHi(){
        return ResponseEntity.ok("Hi From /api/v1/greetings");
    }

    @GetMapping("goodbye")
    public ResponseEntity<String> sayGoodBye(){
        return ResponseEntity.ok("Goodbye From <span style='color:red'> /api/v1/greetings/goodbye </span>");
    }
}
