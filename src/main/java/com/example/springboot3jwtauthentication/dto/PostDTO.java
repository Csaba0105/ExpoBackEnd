package com.example.springboot3jwtauthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private List<String> imageUrls;
    private Integer likes;
    private int comments;
    private UserDTO user;
    private boolean likedByCurrentUser;
    private LocalDateTime createdAt;
}
