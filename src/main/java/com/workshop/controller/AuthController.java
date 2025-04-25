package com.workshop.controller;

import com.workshop.entity.Role;
import com.workshop.entity.User;
import com.workshop.payload.JwtResponse;
import com.workshop.payload.LoginRequest;
import com.workshop.payload.SignupRequest;
import com.workshop.repository.RoleRepository;
import com.workshop.repository.UserRepository;
import com.workshop.security.jwt.JwtService;
import com.workshop.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest signupRequest){
        if(userRepository.existsByEmail(signupRequest.getEmail()))
            return ResponseEntity.badRequest().body("Email Is Already In Use");
        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles==null){
            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role Not Found"));
            roles.add(userRole);
        }else{
            strRoles.forEach(role -> {
                if(role.equalsIgnoreCase("admin")){
                    Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role Not Found"));
                    roles.add(adminRole);
                } else if (role.equalsIgnoreCase("user")) {
                    Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role Not Found"));
                    roles.add(userRole);
                }
            });

        };
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok().body("User Registered Successfully");
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return ResponseEntity.ok(new JwtResponse(token, "Bearer", userDetails.getName(), userDetails.getUsername(), userDetails.getEmail(), roles));


    }
}
