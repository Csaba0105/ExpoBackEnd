package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.models.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post getPostById(Long id);
    Post savePost(Post post);
}
