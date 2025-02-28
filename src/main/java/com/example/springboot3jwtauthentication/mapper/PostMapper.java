package com.example.springboot3jwtauthentication.mapper;

import com.example.springboot3jwtauthentication.dto.PostDTO;
import com.example.springboot3jwtauthentication.models.Image;
import com.example.springboot3jwtauthentication.models.Post;

public class PostMapper {

    public static PostDTO toDTO(Post post, boolean likedByCurrentUser) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .imageUrls(post.getImages().stream()
                        .map(Image::getUrl)
                        .toList())
                .likes(post.getLikes().size())
                .user(UserMapper.toDTO(post.getUser()))
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
