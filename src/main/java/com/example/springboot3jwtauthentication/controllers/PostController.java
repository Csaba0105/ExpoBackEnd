package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.*;
import com.example.springboot3jwtauthentication.dto.post.PostLikeResponseDto;
import com.example.springboot3jwtauthentication.models.Comment;
import com.example.springboot3jwtauthentication.models.Image;
import com.example.springboot3jwtauthentication.models.Post;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.springboot3jwtauthentication.models.Role.ROLE_ADMIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

  private final PostService postService;
  private final UserService userService;
  private final CommentService commentService;
  private final SimpMessagingTemplate messagingTemplate;

  @GetMapping
  public ResponseEntity<List<PostDTO>> getAllPosts(@RequestHeader("Authorization") String authToken) {
      UserDTO user = userService.getUserProfile(authToken);
      return ResponseEntity.ok(postService.getAllPosts(user.getId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Post> getPostById(@PathVariable Long id) {
    postService.getPostById(id);
    return null;
  }

  @PostMapping()
  public ResponseEntity<PostDTO> addPost(@RequestHeader("Authorization") String authToken, @RequestBody PostDTO postDTO) {
    try {
      UserDTO userDTO = userService.getUserProfile(authToken);
      User user = new User();
      user.setId(userDTO.getId());
      user.setUserSortName(userDTO.getUserSortName());
      user.setEmail(userDTO.getEmail());
      user.setFirstName(userDTO.getFirstName());
      user.setRole(ROLE_ADMIN); //TODO hiba

      Post post = new Post();
      post.setTitle(postDTO.getTitle());
      post.setImages(postDTO.getImageUrls().stream().map(url -> {
        Image image = new Image();
        image.setUrl(url);
        image.setPost(post);
        return image;
      }).toList());
      post.setUser(user);

      Post savedPost = postService.savePost(post);

      // 🔥 4. DTO konvertálás válaszhoz
      PostDTO responseDTO = PostDTO.builder()
              .id(savedPost.getId())
              .title(savedPost.getTitle())
              .user(userDTO)
              .imageUrls(savedPost.getImages().stream().map(Image::getUrl).toList())
              .build();

      return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


  // Like hozzáadása vagy eltávolítása
  @PostMapping("/{postId}/like")
  public ResponseEntity<PostLikeResponseDto> likePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
    boolean liked = postService.toggleLike(postId, user);
    int likes = postService.getPostById(postId).getLikes().size();
    PostLikeResponseDto  postLikeResponseDto = PostLikeResponseDto.builder()
            .postId(postId)
            .userId(user.getId())
            .isLiked(liked)
            .likeCount(likes)
            .build();
    return ResponseEntity.ok(postLikeResponseDto);
  }


  @GetMapping("/{postId}/comments")
  public ResponseEntity<List<PostCommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
    try {
      List<PostCommentDTO> comments = commentService.getCommentsByPostId(postId);
      return ResponseEntity.ok(comments);
    } catch (Exception e) {
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
            createdComment.getUser().getId(),
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
    try {
      PostCommentDTO comment = commentService.getCommentById(postId, id);
      return ResponseEntity.ok(comment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  // 4. Komment szerkesztése ID alapján
  @PutMapping("/{postId}/comments/{id}")
  public ResponseEntity<PostCommentDTO> editCommentById(@PathVariable Long postId, @PathVariable Long id, @RequestBody PostCommentDTO updatedComment) {
    try {
      PostCommentDTO editedComment = commentService.editComment(postId, id, updatedComment);
      return ResponseEntity.ok(editedComment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{postId}/comments/{id}")
  public ResponseEntity<?> deleteCommentById(@PathVariable Long postId, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    try {
      String currentUserId = userDetails.getUsername();

      if (!commentService.isCommentOwner(id, currentUserId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own comments.");
      }

      commentService.deleteComment(postId, id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
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
