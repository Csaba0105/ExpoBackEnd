package com.example.springboot3jwtauthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeDataDto {
    private Long postId;
    private int likeCount;
}
