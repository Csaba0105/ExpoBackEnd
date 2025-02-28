package com.example.springboot3jwtauthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDTO {
    private Long id;
    private Long userId;
    private String userSortName;
    private String userImage;
    private String text;
    private LocalDateTime createdAt;
}
