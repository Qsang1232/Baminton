package com.example.demo.service;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Court;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repository.CourtRepository;
import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourtRepository courtRepository;
    private final UserService userService; // Tận dụng lại UserService đã viết

    public List<Review> getReviewsForCourt(Long courtId) {
        return reviewRepository.findByCourtId(courtId);
    }

    public Review createReview(ReviewRequest request, String username) {
        User user = userService.getUserByUsername(username);
        
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với ID: " + request.getCourtId()));

        Review review = Review.builder()
                .user(user)
                .court(court)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewRepository.save(review);
    }
}