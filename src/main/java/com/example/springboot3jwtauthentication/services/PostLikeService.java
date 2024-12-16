package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.models.PostLike;
import com.example.springboot3jwtauthentication.models.User;
import com.example.springboot3jwtauthentication.repositories.PostLikeRepository;
import com.example.springboot3jwtauthentication.repositories.PostRepository;
import com.example.springboot3jwtauthentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostLikeService {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isPostLikedByUser(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    // Like hozzáadása vagy eltávolítása
    public String toggleLike(Long postId, Long userId) {
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return "Like removed";
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);
            return "Like added";
        }
    }

    // Like-ok számlálása
    public Long getLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    /**
     * Ellenőrzi, hogy egy adott felhasználó lájkolta-e az adott posztot.
     * @param postId a poszt azonosítója
     * @param userId a felhasználó azonosítója
     * @return true, ha a felhasználó lájkolta a posztot, különben false
     */
    public boolean hasUserLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}

