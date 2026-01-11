package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.Court;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CourtRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
    }

    @Transactional
    public Booking createBooking(Long courtId, LocalDateTime startTime, LocalDateTime endTime) {
        User user = getCurrentUser();
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với ID: " + courtId));

        // 1. Validate thời gian
        if (startTime.isAfter(endTime) || startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian đặt không hợp lệ!");
        }

        // 2. Check trùng
        boolean isConflict = bookingRepository.existsConflictingBooking(courtId, startTime, endTime);
        if (isConflict) {
            throw new RuntimeException("Khung giờ này đã có người đặt rồi! Vui lòng chọn giờ khác.");
        }

        // 3. Tính tiền
        long minutes = Duration.between(startTime, endTime).toMinutes();
        BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
        if (hours.compareTo(BigDecimal.ONE) < 0) hours = BigDecimal.ONE;

        BigDecimal totalPrice = hours.multiply(court.getPricePerHour());

        // 4. Tạo Booking (Dùng Builder chuẩn)
        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .startTime(startTime)
                .endTime(endTime)
                .totalPrice(totalPrice)
                .status("PENDING")
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings() {
        return bookingRepository.findByUserId(getCurrentUser().getId());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public void cancelBooking(Long id) {
        User currentUser = getCurrentUser();
        Booking booking = bookingRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn với ID: " + id));

        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN"); // Giả sử Role là Enum

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền hủy đơn này.");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Transactional
    public void confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn với ID: " + id));
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }

    @Transactional
    public void confirmBookingPayment(Long id) {
        confirmBooking(id);
    }

    public List<Booking> getBookingsByDate(Long courtId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return bookingRepository.findBookingsByCourtAndDate(courtId, startOfDay, endOfDay);
    }
}