package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // API tạo đánh giá
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Review savedReview = reviewService.createReview(request, username);
        
        return new ResponseEntity<>(ApiResponse.builder()
                .success(true)
                .message("Đánh giá thành công!")
                .data(savedReview)
                .build(), HttpStatus.CREATED);
    }

    // API xem đánh giá theo sân
    @GetMapping("/court/{courtId}")
    public ResponseEntity<?> getReviewsByCourt(@PathVariable Long courtId) {
        List<Review> reviews = reviewService.getReviewsForCourt(courtId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .data(reviews)
                .build());
    }
}