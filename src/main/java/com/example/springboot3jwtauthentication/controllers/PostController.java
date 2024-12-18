package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.AddPostCommentDTO;
import com.example.springboot3jwtauthentication.dto.PostCommentDTO;
import com.example.springboot3jwtauthentication.dto.PostDTO;
import com.example.springboot3jwtauthentication.dto.UserDTO;
import com.example.springboot3jwtauthentication.models.Comment;
import com.example.springboot3jwtauthentication.models.Image;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.services.CommentService;
import com.example.springboot3jwtauthentication.services.PostLikeService;
import com.example.springboot3jwtauthentication.services.PostService;
import com.example.springboot3jwtauthentication.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

  private final PostService postService;
  private final UserService userService;
  private final PostLikeService postLikeService;
  private final CommentService commentService;

  @GetMapping
  public List<PostDTO> getAllPosts(@RequestHeader("Authorization") String authToken) {
    log.debug("Fetching all posts for user with token: {}", authToken);

    try {
      UserDTO user = userService.getUserProfile(authToken);
      log.info("User profile retrieved successfully: {}", user.getId());

      List<Post> posts = postService.getAllPosts();
      log.info("Retrieved {} posts from the database", posts.size());

      List<PostDTO> postDTOs = posts.stream()
              .map(post -> new PostDTO(
                      post.getId(),
                      post.getTitle(),
                      post.getContent(),
                      post.getImages().stream()
                              .map(Image::getUrl)
                              .toList(),
                      new UserDTO(
                              post.getUser().getId(),
                              post.getUser().getUserSortName(),
                              post.getUser().getFirstName(),
                              post.getUser().getLastName(),
                              post.getUser().getEmail(),
                              post.getUser().getImageUrl()
                      ),
                      postLikeService.isPostLikedByUser(post.getId(), user.getId())
              ))
              .collect(Collectors.toList());

      log.info("Successfully transformed posts to DTOs for user {}", user.getId());
      return postDTOs;

    } catch (Exception e) {
      log.error("Error occurred while fetching posts: {}", e.getMessage(), e);
      throw e; // Vagy kezelheted megfelelő válasz státusszal
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Post> getPostById(@PathVariable Long id) {
    log.info("Fetching post with id: {}", id);
    postService.getPostById(id);
    return null;
  }

  @PostMapping
  public ResponseEntity<Post> addPost(@RequestBody PostDTO postDTO) {
    log.info("Attempting to add a new post with title: {}", postDTO.getTitle());

    try {
      Post post = new Post();
      post.setTitle(postDTO.getTitle());
      post.setContent(postDTO.getContent());

      Post savedPost = postService.savePost(post);
      log.info("Successfully created a new post with ID: {} and title: {}", savedPost.getId(), savedPost.getTitle());

      return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    } catch (Exception e) {
      log.error("Error occurred while adding a new post with title: {}: {}", postDTO.getTitle(), e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }


  // Like hozzáadása vagy eltávolítása
  @PostMapping("/{postId}/like")
  public ResponseEntity<?> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
    log.info("User with ID {} is toggling like for Post with ID {}", userId, postId);
    try {
      String message = postLikeService.toggleLike(postId, userId);
      log.debug("Successfully toggled like: {}", message);
      return ResponseEntity.ok().body(message);
    } catch (Exception e) {
      log.error("Error occurred while toggling like for Post ID {} by User ID {}: {}", postId, userId, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
    }
  }

  @GetMapping("/{postId}/likes/status")
  public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long postId, @RequestParam Long userId) {
    log.info("Fetching like status for Post ID {} and User ID {}", postId, userId);

    try {
      boolean liked = postLikeService.hasUserLiked(postId, userId);
      Long likeCount = postLikeService.getLikeCount(postId);
      log.debug("Like status for Post ID {} and User ID {}: liked={}, likeCount={}", postId, userId, liked, likeCount);
      Map<String, Object> response = new HashMap<>();
      response.put("liked", liked);
      response.put("likeCount", likeCount);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error fetching like status for Post ID {} and User ID {}: {}", postId, userId, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }


  @GetMapping("/{postId}/comments")
  public ResponseEntity<List<PostCommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
    log.info("Fetching comments for Post ID {}", postId);
    try {
      List<PostCommentDTO> comments = commentService.getCommentsByPostId(postId);
      log.debug("Retrieved {} comments for Post ID {}", comments.size(), postId);
      return ResponseEntity.ok(comments);
    } catch (Exception e) {
      log.error("Error fetching comments for Post ID {}: {}", postId, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping("/{postId}/comments/count")
  public ResponseEntity<Map<String, Object>> getCommentCount(@PathVariable Long postId) {
    try {
      Long commentCount = commentService.getCommentCount(postId);
      Map<String, Object> response = new HashMap<>();
      response.put("commentCount", commentCount);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error fetching comment count for Post ID {} : {}", postId, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

  }

  // 2. Új komment hozzáadása egy adott posthoz
  @PostMapping("/{postId}/comments")
  public ResponseEntity<PostCommentDTO> addComment(
          @PathVariable Long postId,
          @RequestBody AddPostCommentDTO request) {

    Comment createdComment = commentService.addComment(postId, request.getUserId(), request.getText());

    PostCommentDTO response = new PostCommentDTO(
            createdComment.getId(),
            createdComment.getUser().getUserSortName(),
            createdComment.getUser().getImageUrl(),
            createdComment.getText(),
            createdComment.getCreatedAt()

    );

    return ResponseEntity.ok(response);
  }


  // 3. Komment lekérdezése ID alapján
  @GetMapping("/{postId}/comments/{id}")
  public ResponseEntity<PostCommentDTO> getCommentById(@PathVariable Long postId, @PathVariable Long id) {
    log.info("Fetching comment with ID {} for Post ID {}", id, postId);
    try {
      PostCommentDTO comment = commentService.getCommentById(postId, id);
      return ResponseEntity.ok(comment);
    } catch (Exception e) {
      log.error("Error fetching comment ID {} for Post ID {}: {}", id, postId, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  // 4. Komment szerkesztése ID alapján
  @PutMapping("/{postId}/comments/{id}")
  public ResponseEntity<PostCommentDTO> editCommentById(@PathVariable Long postId, @PathVariable Long id, @RequestBody PostCommentDTO updatedComment) {
    log.info("Editing comment with ID {} for Post ID {}", id, postId);
    try {
      PostCommentDTO editedComment = commentService.editComment(postId, id, updatedComment);
      return ResponseEntity.ok(editedComment);
    } catch (Exception e) {
      log.error("Error editing comment ID {} for Post ID {}: {}", id, postId, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 5. Komment törlése ID alapján
  @DeleteMapping("/{postId}/comments/{id}")
  public ResponseEntity<?> deleteCommentById(@PathVariable Long postId, @PathVariable Long id) {
    log.info("Deleting comment with ID {} for Post ID {}", id, postId);
    try {
      commentService.deleteComment(postId, id);
      log.info("Successfully deleted comment ID {} for Post ID {}", id, postId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Error deleting comment ID {} for Post ID {}: {}", id, postId, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to delete comment");
    }
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
