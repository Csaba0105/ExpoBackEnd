package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.PostDTO;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.models.user.User;

import java.util.List;

public interface PostService {
    List<PostDTO> getAllPosts(Long userId);
    PostDTO getPostById(Long id);
    Post savePost(Post post);
    List<PostDTO> getPostsByUserId(Long userId);
    void deletePost(Long id);
    boolean toggleLike(Long postId, User user);
}
