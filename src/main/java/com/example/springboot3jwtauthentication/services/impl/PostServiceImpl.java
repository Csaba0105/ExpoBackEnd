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

import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.POST_NOT_FOUND_ERROR_MESSAGE;

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
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ERROR_MESSAGE + " id: " + id));
    }

    @Override
    public Post savePost(Post post) {
        try {
            return postRepository.save(post);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the post: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    public void deletePost(Long id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }
}
