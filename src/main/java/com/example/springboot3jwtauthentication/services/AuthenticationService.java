package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.error.InvalidCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springboot3jwtauthentication.dto.JwtAuthenticationResponse;
import com.example.springboot3jwtauthentication.dto.SignInRequest;
import com.example.springboot3jwtauthentication.dto.SignUpRequest;
import com.example.springboot3jwtauthentication.models.Role;
import com.example.springboot3jwtauthentication.models.User;
import com.example.springboot3jwtauthentication.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.EMAIL_ALREADY_EXISTS_ERROR_MESSAGE;
import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.INVALID_CREDENTIALS_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;


    public ResponseEntity<?> signup(SignUpRequest request) {
        try {
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ROLE_USER)
                    .build();

            user = userService.save(user);
            var jwt = jwtService.generateToken(user);

            return ResponseEntity.ok(JwtAuthenticationResponse.builder().token(jwt).build());
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EMAIL_ALREADY_EXISTS_ERROR_MESSAGE);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
        }
    }




    public JwtAuthenticationResponse signin(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception ex) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_ERROR_MESSAGE);
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_ERROR_MESSAGE));

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
