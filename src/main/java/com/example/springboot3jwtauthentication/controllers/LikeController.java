package com.example.springboot3jwtauthentication.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LikeController {

    @MessageMapping("/like")
    @SendTo("/topic/likes")
    public LikeResponse handleLike(LikeRequest request) {
        // Itt frissítheted az adatbázist
        int updatedLikes = updateLikesInDatabase(request.getPostId());
        return new LikeResponse(request.getPostId(), updatedLikes);
    }

    private int updateLikesInDatabase(String postId) {
        // Itt írd meg az adatbázis frissítést
        int newLikeCount = 0;
        return newLikeCount;
    }
}

@Getter
@Setter
class LikeRequest {
    private String postId;
    // getter-setter
}

@Getter
@Setter
class LikeResponse {
    private String postId;
    private int likeCount;

    public LikeResponse(String postId, int updatedLikes) {
    }
    // getter-setter, konstruktor
}
