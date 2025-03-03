package com.example.springboot3jwtauthentication.controllers;

import com.example.springboot3jwtauthentication.dto.*;
import com.example.springboot3jwtauthentication.dto.post.PostLikeResponseDto;
import com.example.springboot3jwtauthentication.mapper.PostMapper;
import com.example.springboot3jwtauthentication.models.Image;
import com.example.springboot3jwtauthentication.models.post.Comment;
import com.example.springboot3jwtauthentication.models.post.Post;
import com.example.springboot3jwtauthentication.models.user.User;
import com.example.springboot3jwtauthentication.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

  private final PostService postService;
  private final CommentService commentService;

  @GetMapping
  public ResponseEntity<List<PostDTO>> getAllPosts(@AuthenticationPrincipal User user) {
    try {
      List<PostDTO> postDTO = postService.getAllPosts(user.getId());
      return ResponseEntity.ok(postDTO);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<PostDTO>> getAllPostsByUserId(@PathVariable Long userId) {
        try {
            List<PostDTO> postDTO = postService.getAllPosts(userId);
            return ResponseEntity.ok(postDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

  @GetMapping("/{id}")
  public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
    try {
      PostDTO postDTO = postService.getPostById(id);
      return ResponseEntity.ok(postDTO);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping()
  public ResponseEntity<PostDTO> addPost(@AuthenticationPrincipal User user, @RequestBody PostDTO postDTO) {
    try {
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

      PostDTO responseDTO = PostMapper.toDTO(savedPost, false);
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

  @PostMapping("/{postId}/like")
  public ResponseEntity<PostLikeResponseDto> likePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
    boolean liked = postService.toggleLike(postId, user);
    int likes = postService.getPostById(postId).getLikes();
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

  @PostMapping("/{postId}/comments")
  public ResponseEntity<PostCommentDTO> addComment(@PathVariable Long postId, @RequestBody AddPostCommentDTO request) {
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

  @GetMapping("/{postId}/comments/{id}")
  public ResponseEntity<PostCommentDTO> getCommentById(@PathVariable Long postId, @PathVariable Long id) {
    try {
      PostCommentDTO comment = commentService.getCommentById(id);
      return ResponseEntity.ok(comment);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PutMapping("/{postId}/comments/{id}")
  public ResponseEntity<PostCommentDTO> editCommentById(@PathVariable Long postId, @PathVariable Long id, @AuthenticationPrincipal User user, @RequestBody PostCommentDTO updatedComment) {
    try {
      PostCommentDTO existingComment = commentService.getCommentById(id);

      if (!existingComment.getUserId().equals(user.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
      }

      PostCommentDTO editedComment = commentService.editComment(postId, id, updatedComment);
      return ResponseEntity.ok(editedComment);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @DeleteMapping("/{postId}/comments/{id}")
  public ResponseEntity<?> deleteCommentById(@PathVariable Long postId, @PathVariable Long id, @AuthenticationPrincipal User user) {
    try {
      if (!commentService.isCommentOwner(id, user.getId())) {
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
