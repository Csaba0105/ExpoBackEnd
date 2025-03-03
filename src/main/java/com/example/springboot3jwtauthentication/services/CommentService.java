package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.PostCommentDTO;
import com.example.springboot3jwtauthentication.models.post.Comment;
import com.example.springboot3jwtauthentication.models.user.User;

import java.util.List;

public interface CommentService {
    List<PostCommentDTO> getCommentsByPostId(Long postId);
    Comment addComment(Long postId, Long userId, String text);
    PostCommentDTO getCommentById(Long id);
    PostCommentDTO editComment(Long postId, Long id, PostCommentDTO updatedComment);
    void deleteComment(Long postId, Long id);
    boolean isCommentOwner(Long id, Long currentUserId);
}
