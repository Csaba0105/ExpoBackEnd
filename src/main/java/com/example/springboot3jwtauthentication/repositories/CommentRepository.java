package com.example.springboot3jwtauthentication.repositories;

import com.example.springboot3jwtauthentication.models.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    Optional<Comment> findByPostIdAndId(Long postId, Long id);
    Long countByPostId(Long postId);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :id AND c.post.id = :postId")
    int deleteByPostIdAndId(@Param("postId") Long postId, @Param("id") Long id);

}

