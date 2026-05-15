package com.creativeshouse.post_service.service;

import com.creativeshouse.post_service.dtos.request.PostRequest;
import com.creativeshouse.post_service.dtos.response.PageResponse;
import com.creativeshouse.post_service.dtos.response.PostResponse;

import java.util.UUID;

public interface PostService {
    PostResponse createPost(PostRequest request, Long userId);
    PostResponse updatePost(UUID postId, PostRequest request, Long userId);
    void deletePost(UUID postId, Long userId);
    PostResponse getPostById(UUID postId);
    PageResponse<PostResponse> getAllPosts(int page, int size, String sortBy, String sortDir);
    PageResponse<PostResponse> getPostsByUserId(Long targetUserId, int page, int size, String sortBy, String sortDir);
}
