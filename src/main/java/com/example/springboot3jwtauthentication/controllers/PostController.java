package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.PostDTO;
import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.models.Image;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.services.PostLikeService;
import com.example.springboot3jwtauthentication.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

  private final PostService postService;
  private final PostLikeService postLikeService;


  @GetMapping
  public List<PostDTO> getAllPosts() {
    List<Post> posts = postService.getAllPosts();
    return posts.stream()
            .map(post -> new PostDTO(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImages().stream()
                            .map(Image::getUrl)
                            .toList(),
                    new UserDTO(
                            post.getUser().getId(),
                            post.getUser().getFirstName(),
                            post.getUser().getLastName(),
                            post.getUser().getEmail(),
                            post.getUser().getImageUrl()
                    )
            ))
            .collect(Collectors.toList());
  }



  @GetMapping("/{id}")
  public ResponseEntity<Post> getPostById(@PathVariable String id) {
    System.out.println(id);
    return null;
  }

  @PostMapping()
  public ResponseEntity<Post> addPost(@RequestBody PostDTO postDTO) {
    try {
      Post post = new Post();
      post.setTitle(postDTO.getTitle());
      post.setContent(postDTO.getContent());

      Post savedPost = postService.savePost(post);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // Like hozzáadása vagy eltávolítása
  @PostMapping("/{postId}/like")
  public ResponseEntity<?> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
    String message = postLikeService.toggleLike(postId, userId);
    return ResponseEntity.ok().body(message);
  }

  // Like-ok számának lekérdezése
  @GetMapping("/{postId}/likes/count")
  public ResponseEntity<?> getLikeCount(@PathVariable Long postId) {
    Long likeCount = postLikeService.getLikeCount(postId);
    return ResponseEntity.ok().body(likeCount);
  }

  @GetMapping("/anon")
  public String anonEndPoint() {
      return "everyone can see this";
  }

  @GetMapping("/users")
  @PreAuthorize("hasRole('USER')")
  public String usersEndPoint() {
    return "ONLY users can see this";
  }

  @GetMapping("/admins")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminsEndPoint() {
    return "ONLY admins can see this";
  }
}
