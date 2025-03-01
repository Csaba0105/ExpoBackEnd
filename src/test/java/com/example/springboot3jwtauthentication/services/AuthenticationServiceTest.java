package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.JwtAuthenticationResponse;
import com.example.springboot3jwtauthentication.dto.SignInRequest;
import com.example.springboot3jwtauthentication.dto.SignUpRequest;
import com.example.springboot3jwtauthentication.error.InvalidCredentialsException;
import com.example.springboot3jwtauthentication.models.Role;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.EMAIL_ALREADY_EXISTS_ERROR_MESSAGE;
import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.INVALID_CREDENTIALS_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_createsNewUserSuccessfully() {
        SignUpRequest request = new SignUpRequest("username", "firstName", "lastName", "email@example.com", "password");
        User user = User.builder()
                .userSortName(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        ResponseEntity<?> response = authenticationService.signup(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwtToken", ((JwtAuthenticationResponse) Objects.requireNonNull(response.getBody())).getToken());
    }

    @Test
    void signup_throwsEmailAlreadyExistsException() {
        SignUpRequest request = new SignUpRequest("username", "firstName", "lastName", "email@example.com", "password");
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = authenticationService.signup(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(EMAIL_ALREADY_EXISTS_ERROR_MESSAGE, response.getBody());
    }

    @Test
    void signup_throwsUnexpectedException() {
        SignUpRequest request = new SignUpRequest("username", "firstName", "lastName", "email@example.com", "password");
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenThrow(new RuntimeException("Unexpected error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authenticationService.signup(request));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason()).contains("Unexpected error"));
    }

    @Test
    void signin_authenticatesUserSuccessfully() {
        SignInRequest request = new SignInRequest("email@example.com", "password");
        User user = User.builder().email(request.getEmail()).build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        JwtAuthenticationResponse response = authenticationService.signin(request);

        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void signin_throwsInvalidCredentialsException() {
        SignInRequest request = new SignInRequest("email@example.com", "password");
        doThrow(new InvalidCredentialsException(INVALID_CREDENTIALS_ERROR_MESSAGE)).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authenticationService.signin(request));

        assertEquals(INVALID_CREDENTIALS_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void signin_throwsInvalidCredentialsExceptionWhenUserNotFound() {
        SignInRequest request = new SignInRequest("email@example.com", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authenticationService.signin(request));

        assertEquals(INVALID_CREDENTIALS_ERROR_MESSAGE, exception.getMessage());
    }
}