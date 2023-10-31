package com.htn.funiture.auth;

import com.htn.funiture.dtos.UserDTO;
import com.htn.funiture.entity.User;
import com.htn.funiture.enums.Role;
import com.htn.funiture.repository.UserRepository;
import com.htn.funiture.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse signup(RegisterRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        UserDTO userWithoutPw = new UserDTO(user);
        var jwtToken = jwtService.generateToken(userWithoutPw);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDTO userWithoutPw = new UserDTO(user);
//        System.out.println(user.toString());
        var jwtToken = jwtService.generateToken(userWithoutPw);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public boolean existEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
