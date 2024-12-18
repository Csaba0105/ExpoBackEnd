package com.example.springboot3jwtauthentication.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text; // A komment szövege

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // Kapcsolat a Post entitással (több komment tartozhat egy poszthoz)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Kapcsolat a User entitással (ki írta a kommentet)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}