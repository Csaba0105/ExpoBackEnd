package com.example.springboot3jwtauthentication.repositories;

import com.example.springboot3jwtauthentication.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    Optional<Comment> findByPostIdAndId(Long postId, Long id);
    Long countByPostId(Long postId);
}

