package com.example.springboot3jwtauthentication.dto;

import com.example.springboot3jwtauthentication.models.post.CommentLike;
import com.example.springboot3jwtauthentication.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
