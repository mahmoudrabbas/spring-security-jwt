package com.workshop.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workshop.annotation.SkipSanitizing;
import com.workshop.security.config.SanitizingDeserializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Data
@Setter
@Getter
public class SignupRequest {
//    @JsonDeserialize(using = SanitizingDeserializer.class)
    private String name;
//    @JsonDeserialize(using = SanitizingDeserializer.class)
    private String username;

    @SkipSanitizing
    private String email;
    @SkipSanitizing
    private String password;
    private Set<String> roles=new HashSet<>();
}
