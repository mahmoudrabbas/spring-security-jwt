package com.workshop.payload;

import com.workshop.entity.Role;
import lombok.Data;

import java.util.Set;


@Data
public class SignupRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
}
