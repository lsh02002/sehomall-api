package com.example.sehomallapi.web.controller.review;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.review.ReviewService;
import com.example.sehomallapi.web.dto.review.ReviewRequest;
import com.example.sehomallapi.web.dto.review.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllReviews(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Page<ReviewResponse>> getReviewByItemId(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByItemId(id, pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReviewResponse>> getAllReviewsByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByUserEmail(customUserDetails.getEmail(), pageable));
    }

    @PostMapping
    public ResponseEntity<Boolean> createReview(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ReviewRequest reviewRequest, @RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(reviewService.createReview(customUserDetails.getId(), reviewRequest, files));
    }

    @PutMapping
    public ResponseEntity<Boolean> updateReview(@AuthenticationPrincipal CustomUserDetails customUserDetails, Long reviewId, @RequestBody ReviewRequest reviewRequest, @RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(reviewService.updateReview(customUserDetails.getId(), reviewId, reviewRequest, files));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteReview(@AuthenticationPrincipal CustomUserDetails customUserDetails, Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(customUserDetails.getId(), reviewId));
    }
}
