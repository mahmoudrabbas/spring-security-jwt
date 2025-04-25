package com.workshop.payload;

import com.workshop.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String name;
    private String username;
    private String email;
    private List<String> rolesList;
}
