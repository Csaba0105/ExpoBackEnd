package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.EmailDTO;
import com.example.springboot3jwtauthentication.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot3jwtauthentication.dto.JwtAuthenticationResponse;
import com.example.springboot3jwtauthentication.dto.SignInRequest;
import com.example.springboot3jwtauthentication.dto.SignUpRequest;
import com.example.springboot3jwtauthentication.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        ResponseEntity<?> response = authenticationService.signup(request);

        if (response.getStatusCode().is2xxSuccessful()) {
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setTo(request.getEmail());
            emailDTO.setSubject("Welcome to Our Service!");
            emailDTO.setBody("Thank you for signing up, " + request.getFirstName() + " " + request.getLastName() + "!");

            emailService.sendEmail(emailDTO);
        }

        return response;
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        log.info("User signin : {}", request.getEmail());
        return authenticationService.signin(request);
    }
}