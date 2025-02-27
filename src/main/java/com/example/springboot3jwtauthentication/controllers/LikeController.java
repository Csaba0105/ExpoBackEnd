package com.example.springboot3jwtauthentication.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

    // Ezt a WebSocket végpontot akkor hívják meg, ha a frontend a "getLikeCount" üzenetet küldi
    @MessageMapping("/getLikeCount")
    @SendTo("/topic/likes")
    public LikeResponse getLikeCount(LikeRequest request) {
        // Lekérjük az adatbázisból a likeCount értéket
        int likeCount = getLikeCountFromDatabase(request.getPostId());
        return new LikeResponse(request.getPostId(), likeCount);
    }

    // Az adatbázis lekérdezésének szimulálása
    private int getLikeCountFromDatabase(String postId) {
        // Itt történik az adatbázis lekérdezés
        int likeCount = 100; // Példa érték
        return likeCount;
    }

    // WebSocket üzenet, amely a like frissítést kezeli
    @MessageMapping("/like")
    @SendTo("/topic/likes")
    public LikeResponse handleLike(LikeRequest request) {
        System.out.println(request);
        int updatedLikes = updateLikesInDatabase(request.getPostId());
        System.out.println("Updated likes: " + updatedLikes);

        // Minden klienst értesítünk a változásról
        return new LikeResponse(request.getPostId(), updatedLikes);
    }


    private int updateLikesInDatabase(String postId) {
        // Itt írd meg az adatbázis frissítést
        int newLikeCount = 99; // Az új like szám (adatbázis frissítése után)
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

    public LikeResponse(String postId, int likeCount) {
        this.postId = postId;
        this.likeCount = likeCount;
    }
    // getter-setter, konstruktor
}
