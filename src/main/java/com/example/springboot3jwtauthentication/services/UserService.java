package com.example.springboot3jwtauthentication.services;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.springboot3jwtauthentication.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springboot3jwtauthentication.models.User;
import com.example.springboot3jwtauthentication.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.USER_NOT_FOUND_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${token.secret.key}")
    private String jwtSecret;

  private final UserRepository userRepository;

  public UserDetailsService userDetailsService() {
      return new UserDetailsService() {
          @Override
          public UserDetails loadUserByUsername(String username) {
              return userRepository.findByEmail(username)
                      .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR_MESSAGE));
          }
      };
  }

    public UserDTO getUserProfile(String token) {
        String userEmail = extractUserIdFromToken(token);
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            return new UserDTO(user.get().getId(), user.get().getUserSortName(), user.get().getFirstName(), user.get().getLastName(), user.get().getEmail(), user.get().getImageUrl());
        } else {
            throw new RuntimeException(USER_NOT_FOUND_ERROR_MESSAGE);
        }
    }

  public User save(User newUser) {
    if (newUser.getId() == null) {
      newUser.setCreatedAt(LocalDateTime.now());
    }

    newUser.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(newUser);
  }

    private String extractUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


}
