package com.example.springboot3jwtauthentication.services;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_ERROR_MESSAGE));

        return UserMapper.toDTO(user);
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


    public ResponseEntity<UserDTO> getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> ResponseEntity.ok(UserMapper.toDTO(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public UserDTO updateUserProfile(String token, UserDTO updatedUser) {
        System.out.println(updatedUser);
        String userEmail = extractUserIdFromToken(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_ERROR_MESSAGE));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setImageUrl(updatedUser.getImageUrl());
        user.setBackgroundUrl(updatedUser.getBackgroundUrl());

        return UserMapper.toDTO(userRepository.save(user));
    }
}
