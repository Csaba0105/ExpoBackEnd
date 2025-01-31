package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.models.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post getPostById(Long id);
    Post savePost(Post post);
    List<Post> getPostsByUserId(Long userId);
    void deletePost(Long id);
}
