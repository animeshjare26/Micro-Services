package com.creativeshouse.post_service.controllers;

import com.creativeshouse.post_service.dtos.request.PostRequest;
import com.creativeshouse.post_service.dtos.response.PageResponse;
import com.creativeshouse.post_service.dtos.response.PostResponse;
import com.creativeshouse.post_service.service.PostService;
import com.creativeshouse.post_service.utils.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private Long extractUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            throw new RuntimeException("Unauthorized");
        }
        return (Long) userIdObj;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<PostResponse>> createPost(
            @Valid @RequestBody PostRequest postRequest, HttpServletRequest request) {
        Long userId = extractUserId(request);
        PostResponse response = postService.createPost(postRequest, userId);
        return ResponseEntity.ok(GenericResponse.success(response, "Post created successfully"));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<GenericResponse<PostResponse>> updatePost(
            @PathVariable UUID postId, @Valid @RequestBody PostRequest postRequest, HttpServletRequest request) {
        Long userId = extractUserId(request);
        PostResponse response = postService.updatePost(postId, postRequest, userId);
        return ResponseEntity.ok(GenericResponse.success(response, "Post updated successfully"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<GenericResponse<Void>> deletePost(
            @PathVariable UUID postId, HttpServletRequest request) {
        Long userId = extractUserId(request);
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(GenericResponse.success(null, "Post deleted successfully"));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponse<PostResponse>> getPostById(@PathVariable UUID postId) {
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.ok(GenericResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<GenericResponse<PageResponse<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<PostResponse> response = postService.getAllPosts(page, size, sortBy, sortDir);
        return ResponseEntity.ok(GenericResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GenericResponse<PageResponse<PostResponse>>> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<PostResponse> response = postService.getPostsByUserId(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(GenericResponse.success(response));
    }
}
