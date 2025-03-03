package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.PostCommentDTO;
import com.example.springboot3jwtauthentication.models.post.Comment;

import java.util.List;

public interface CommentService {
    List<PostCommentDTO> getCommentsByPostId(Long postId);
    Comment addComment(Long postId, Long userId, String text);
    PostCommentDTO getCommentById(Long postId, Long id);
    PostCommentDTO editComment(Long postId, Long id, PostCommentDTO updatedComment);
    void deleteComment(Long postId, Long id);
    Long getCommentCount(Long postId);
    boolean isCommentOwner(Long id, Long currentUserId);
}
