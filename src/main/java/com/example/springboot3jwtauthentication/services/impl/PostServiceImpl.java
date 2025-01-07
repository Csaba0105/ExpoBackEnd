package com.example.springboot3jwtauthentication.services.impl;

import com.example.springboot3jwtauthentication.error.PostNotFoundException;
import com.example.springboot3jwtauthentication.models.Image;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.repositories.PostRepository;
import com.example.springboot3jwtauthentication.services.PostService;
import com.example.springboot3jwtauthentication.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final StorageService storageService;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }

    @Override
    public Post savePost(Post post, MultipartFile image) {
        try {
            // Upload the file and get its URL
            String fileUrl = storageService.uploadFile(post.getUser().getUserSortName(), image);

            // Create a new Image object and associate it with the post
            Image postImage = new Image();
            postImage.setUrl(fileUrl);
            postImage.setPost(post);

            // Add the image to the post's images list
            post.getImages().add(postImage);

            // Save the post to the database
            return postRepository.save(post);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the post: " + e.getMessage(), e);
        }
    }
}
