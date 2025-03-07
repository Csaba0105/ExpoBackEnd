package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.NotificationDto;
import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return  userService.getUserById(userId);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDTO>> getUserFollowers(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<UserDTO>> getUserFollowings(@PathVariable Long userId) {
        return null;
    }

    @PutMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(@AuthenticationPrincipal User user, @PathVariable("id") Long userId) {
    return null;
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<?> unfollowUser(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        return null;
    }

    @GetMapping("/{userId}/notifications")
    public ResponseEntity<List<NotificationDto>> listNotifications(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> search(@RequestParam(required = true, name = "name") String userName,
                                                @PageableDefault(sort = "name", direction = Sort.Direction.ASC, page = 0, size = 10)
                                                Pageable pageable) {
        //Page<User> users = userRepository.findByNameContainingIgnoreCase(userName, pageable);
        //Page<UserDto> usersDto = UserDto.toDtoPage(users);
        //return ResponseEntity.status(200).body(usersDto);
        return null;
    }
}