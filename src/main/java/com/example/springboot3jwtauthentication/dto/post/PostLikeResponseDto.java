package com.example.springboot3jwtauthentication.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponseDto {
    private Long postId;
    private Long userId;
    private boolean isLiked;
    private int likeCount;
}
