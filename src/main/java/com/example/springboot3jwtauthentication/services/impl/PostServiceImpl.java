package com.example.springboot3jwtauthentication.services.impl;

import com.example.springboot3jwtauthentication.dto.PostDTO;
import com.example.springboot3jwtauthentication.error.PostNotFoundException;
import com.example.springboot3jwtauthentication.mapper.PostMapper;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.models.PostLike;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.repositories.PostLikeRepository;
import com.example.springboot3jwtauthentication.repositories.PostRepository;
import com.example.springboot3jwtauthentication.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.springboot3jwtauthentication.utils.constants.ResponseMessageConstants.POST_NOT_FOUND_ERROR_MESSAGE;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<PostDTO> getAllPosts(Long userId) {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map(post -> {
            boolean likedByUser = postLikeRepository.existsByPostIdAndUserId(post.getId(), userId);
            return PostMapper.toDTO(post, likedByUser);
        }).collect(Collectors.toList());
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

    @Override
    @Transactional
    public boolean toggleLike(Long postId, User user) {
        boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, user.getId());
        if (exists) {
            postLikeRepository.deleteByPostIdAndUserId(postId, user.getId());
            return false;
        } else {
            Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            postLikeRepository.save(new PostLike(null, post, user));
            return true;
        }
    }
}
