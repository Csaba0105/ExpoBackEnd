package com.example.springboot3jwtauthentication.services.impl;

import com.example.springboot3jwtauthentication.dto.PostCommentDTO;
import com.example.springboot3jwtauthentication.models.Comment;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.repositories.CommentRepository;
import com.example.springboot3jwtauthentication.repositories.PostRepository;
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

    @Override
    public List<PostCommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(comment -> new PostCommentDTO(
                        comment.getId(),
                        comment.getUser().getUserSortName(),
                        comment.getUser().getImageUrl(),
                        comment.getText(),
                        comment.getCreatedAt().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public PostCommentDTO addCommentToPost(Long postId, PostCommentDTO commentDTO) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setPost(post);
        // Feltételezhető, hogy van egy bejelentkezett user (példa)
        comment.setUser(post.getUser());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        return new PostCommentDTO(comment.getId(), comment.getUser().getUsername(), comment.getUser().getImageUrl(), comment.getText(), comment.getCreatedAt().toString());
    }

    @Override
    public PostCommentDTO getCommentById(Long postId, Long id) {
        Comment comment = commentRepository.findByPostIdAndId(postId, id).orElseThrow(() -> new RuntimeException("Comment not found"));
        return new PostCommentDTO(comment.getId(), comment.getUser().getUsername(), comment.getUser().getImageUrl(), comment.getText(), comment.getCreatedAt().toString());
    }

    @Override
    public PostCommentDTO editComment(Long postId, Long id, PostCommentDTO updatedComment) {
        Comment comment = commentRepository.findByPostIdAndId(postId, id).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setText(updatedComment.getText());
        commentRepository.save(comment);
        return new PostCommentDTO(comment.getId(), comment.getUser().getUsername(), comment.getUser().getImageUrl(), comment.getText(), comment.getCreatedAt().toString());
    }

    @Override
    public void deleteComment(Long postId, Long id) {
        Comment comment = commentRepository.findByPostIdAndId(postId, id).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }
}
