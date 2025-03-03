package com.example.springboot3jwtauthentication.repositories;

import com.example.springboot3jwtauthentication.models.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllPostsOrderedByDate();
    List<Post> findByUserId(Long userId);
}
