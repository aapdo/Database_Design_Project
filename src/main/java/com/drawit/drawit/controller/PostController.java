package com.drawit.drawit.controller;


import com.drawit.drawit.dto.PostDto;
import com.drawit.drawit.dto.request.RequestPostDto;
import com.drawit.drawit.entity.Post;
import com.drawit.drawit.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> writeNewPost(@Valid @RequestBody RequestPostDto requestPostDto) {
        try {
            PostDto postDto = postService.writeNewPost(requestPostDto);
            return ResponseEntity.ok(postDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(" ");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostInfo(@PathVariable("postId") Long postId) {
        log.info(postId.toString());
        return ResponseEntity.ok(" ");
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getPostCommentInfo(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(" ");
    }
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> writePostCommentInfo(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(" ");
    }

    @PostMapping("/delete/{postId}")
    public ResponseEntity<?> deletePostCommentInfo(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(" ");
    }

    private Long getUserIdFromAuthentication() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
