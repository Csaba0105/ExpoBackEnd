package com.example.springboot3jwtauthentication.services;

import com.example.springboot3jwtauthentication.dto.PostCommentDTO;

import java.util.List;

public interface CommentService {
    List<PostCommentDTO> getCommentsByPostId(Long postId);
    PostCommentDTO addCommentToPost(Long postId, PostCommentDTO commentDTO);
    PostCommentDTO getCommentById(Long postId, Long id);
    PostCommentDTO editComment(Long postId, Long id, PostCommentDTO updatedComment);
    void deleteComment(Long postId, Long id);
    Long getCommentCount(Long postId);
}
