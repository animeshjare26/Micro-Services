package com.creativeshouse.post_service.service.impl;

import com.creativeshouse.post_service.client.UserClient;
import com.creativeshouse.post_service.dtos.request.PostRequest;
import com.creativeshouse.post_service.dtos.response.AuthorSummaryDto;
import com.creativeshouse.post_service.dtos.response.PageResponse;
import com.creativeshouse.post_service.dtos.response.PostResponse;
import com.creativeshouse.post_service.models.Post;
import com.creativeshouse.post_service.repository.PostRepository;
import com.creativeshouse.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserClient userClient;

    @Override
    public PostResponse createPost(PostRequest request, Long userId) {
        Post post = Post.builder()
                .userId(userId)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .build();
        
        post = postRepository.save(post);
        return mapToResponse(post);
    }

    @Override
    public PostResponse updatePost(UUID postId, PostRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this post");
        }

        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        return mapToResponse(postRepository.save(post));
    }

    @Override
    public void deletePost(UUID postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this post");
        }
        
        postRepository.delete(post);
    }

    @Override
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(post);
    }

    @Override
    public PageResponse<PostResponse> getAllPosts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Post> postsPage = postRepository.findAll(pageable);
        List<PostResponse> mapped = postsPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

        return new PageResponse<>(
                mapped, postsPage.getNumber(), postsPage.getSize(), 
                postsPage.getTotalElements(), postsPage.getTotalPages(), postsPage.isLast()
        );
    }

    @Override
    public PageResponse<PostResponse> getPostsByUserId(Long targetUserId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Post> postsPage = postRepository.findByUserId(targetUserId, pageable);
        List<PostResponse> mapped = postsPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

        return new PageResponse<>(
                mapped, postsPage.getNumber(), postsPage.getSize(), 
                postsPage.getTotalElements(), postsPage.getTotalPages(), postsPage.isLast()
        );
    }

    private PostResponse mapToResponse(Post post) {
        AuthorSummaryDto author = null;
        try {
            author = userClient.getUserSummary(post.getUserId()).getData();
        } catch (Exception e) {
            log.error("Failed to fetch author summary for user {}", post.getUserId(), e);
            author = new AuthorSummaryDto(post.getUserId(), "", "Unknown User", "UNKNOWN");
        }

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .createdAt(post.getCreatedAt())
                .author(author)
                .build();
    }
}
