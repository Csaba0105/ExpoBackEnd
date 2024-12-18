package com.example.springboot3jwtauthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPostCommentDTO {
    private Long postId; // A hozzászólás posztjának azonosítója
    private Long userId; // A felhasználó azonosítója
    private String text; // A hozzászólás szövege
}
