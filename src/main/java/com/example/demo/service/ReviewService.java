package com.example.demo.service;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // Tạo đánh giá mới (Đã thêm logic chặn spam)
    @Transactional
    public Review createReview(ReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt sân không tồn tại"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn hàng của người khác");
        }

        // --- MỚI: KIỂM TRA ĐÃ ĐÁNH GIÁ CHƯA ---
        if (booking.isHasReviewed()) {
            throw new RuntimeException("Đơn hàng này bạn đã đánh giá rồi!");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .court(booking.getCourt())
                .build();

        // --- MỚI: CẬP NHẬT TRẠNG THÁI BOOKING ---
        booking.setHasReviewed(true);
        bookingRepository.save(booking);

        return reviewRepository.save(review);
    }

    // Lấy danh sách đánh giá của 1 sân (Quan trọng: Đừng xóa hàm này)
    public List<Review> getReviewsForCourt(Long courtId) {
        return reviewRepository.findByCourtId(courtId);
    }
}