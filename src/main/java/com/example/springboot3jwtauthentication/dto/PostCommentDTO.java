package com.example.springboot3jwtauthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentDTO {
    private Long id;
    private String userSortName;   // Felhasználó neve
    private String userImage;  // Felhasználói profilkép URL-je
    private String text;       // Komment szövege
    private String createdAt;  // Létrehozás időpontja
}
