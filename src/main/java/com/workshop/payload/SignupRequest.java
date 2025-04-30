package com.workshop.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Data
@Setter
@Getter
public class SignupRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<String> roles=new HashSet<>();
}
