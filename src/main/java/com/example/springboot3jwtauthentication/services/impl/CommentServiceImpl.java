package com.example.springboot3jwtauthentication.services.impl;

import com.example.springboot3jwtauthentication.dto.PostCommentDTO;
import com.example.springboot3jwtauthentication.models.Comment;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.repositories.CommentRepository;
import com.example.springboot3jwtauthentication.repositories.PostRepository;
import com.example.springboot3jwtauthentication.repositories.UserRepository;
import com.example.springboot3jwtauthentication.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<PostCommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(comment -> new PostCommentDTO(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getUserSortName(),
                        comment.getUser().getImageUrl(),
                        comment.getText(),
                        comment.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public Comment addComment(Long postId, Long userId, String text) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Comment létrehozása
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());

        // Mentés
        return commentRepository.save(comment); // Visszatérés a létrehozott kommenttel
    }


    @Override
    public PostCommentDTO getCommentById(Long postId, Long id) {
        Comment comment = commentRepository.findByPostIdAndId(postId, id).orElseThrow(() -> new RuntimeException("Comment not found"));
        return new PostCommentDTO(comment.getId(), comment.getUser().getId(), comment.getUser().getUsername(), comment.getUser().getImageUrl(), comment.getText(), comment.getCreatedAt());
    }

    @Override
    public PostCommentDTO editComment(Long postId, Long id, PostCommentDTO updatedComment) {
        Comment comment = commentRepository.findByPostIdAndId(postId, id).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setText(updatedComment.getText());
        commentRepository.save(comment);
        return new PostCommentDTO(comment.getId(), comment.getUser().getId(), comment.getUser().getUsername(), comment.getUser().getImageUrl(), comment.getText(), comment.getCreatedAt());
    }

    @Override
    public void deleteComment(Long postId, Long id) {
        Comment comment = commentRepository.findByPostIdAndId(postId, id).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    //TODO itt nem userId van hanem user email azt majd javítani!
    @Override
    public boolean isCommentOwner(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return comment.getUser().getEmail().equals(userId);
    }
}
