package com.example.springboot3jwtauthentication.repositories;

import com.example.springboot3jwtauthentication.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long> {
}
