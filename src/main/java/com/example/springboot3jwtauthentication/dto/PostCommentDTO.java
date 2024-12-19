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
    private Long userId; // Felhasználó id
    private String userSortName;   // Felhasználó neve
    private String userImage;  // Felhasználói profilkép URL-je
    private String text;       // Komment szövege
    private LocalDateTime createdAt;  // Létrehozás időpontja
}
