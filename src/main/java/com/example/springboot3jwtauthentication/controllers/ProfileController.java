package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.PostDTO;
import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.services.PostService;
import com.example.springboot3jwtauthentication.services.UserService;
import com.example.springboot3jwtauthentication.services.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final PostService postService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            UserDTO user = userService.getUserProfile(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestHeader("Authorization") String token,
                                                     @Valid @RequestBody UserDTO updatedUser) {
        try {
            UserDTO user = userService.updateUserProfile(token, updatedUser);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/profile/{userId}/posts")
    public List<PostDTO> getPostsByUser(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        return null;
//        return posts.stream()
//                .map(post -> new PostDTO(
//                        post.getId(),
//                        post.getTitle(),
//                        post.getImages().stream()
//                                .map(Image::getUrl)
//                                .toList(),
//                        post.getLikes().size(),
//                        UserMapper.toDTO(post.getUser()),
//                        postLikeService.isPostLikedByCurrentUser(post.getId(), userId)
//                ))
//                .collect(Collectors.toList());
    }

    @PutMapping("/profile/{userId}/settings")
    public ResponseEntity<UserDTO> updateUserSettings(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userSettingsService.updateSettings(userId, userDTO));
    }
}
