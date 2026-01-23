package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.Court;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CourtRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    // Lấy user hiện tại đang đăng nhập
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập lại"));
    }

    @Transactional
    public Booking createBooking(Long courtId, LocalDateTime startTime, LocalDateTime endTime) {
        User user = getCurrentUser();
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sân"));

        // --- VALIDATION 1: Logic thời gian cơ bản ---
        if (startTime.isAfter(endTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ kết thúc phải sau giờ bắt đầu!");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể đặt sân ở thời gian trong quá khứ!");
        }

        // --- VALIDATION 2: Giờ mở cửa (05:00 - 23:00) ---
        LocalTime openTime = LocalTime.of(5, 0);
        LocalTime closeTime = LocalTime.of(23, 0);
        
        LocalTime bookingStart = startTime.toLocalTime();
        LocalTime bookingEnd = endTime.toLocalTime();

        // Kiểm tra: Nếu giờ bắt đầu sớm hơn 5h sáng HOẶC giờ kết thúc muộn hơn 23h đêm
        if (bookingStart.isBefore(openTime) || bookingEnd.isAfter(closeTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Sân chỉ hoạt động từ 05:00 đến 23:00. Vui lòng chọn giờ khác!");
        }

        // --- VALIDATION 3: Check trùng lịch ---
        boolean isConflict = bookingRepository.existsConflictingBooking(courtId, startTime, endTime);
        if (isConflict) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Khung giờ này đã có người đặt! Vui lòng chọn khung giờ khác.");
        }

        // --- TÍNH TIỀN ---
        long minutes = Duration.between(startTime, endTime).toMinutes();
        BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
        
        // Tối thiểu tính 1 giờ
        if (hours.compareTo(BigDecimal.ONE) < 0) hours = BigDecimal.ONE;

        BigDecimal totalPrice = hours.multiply(court.getPricePerHour());

        // Tạo Booking
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
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn"));

        // 1. Kiểm tra quyền sở hữu
        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        String currentRole = String.valueOf(currentUser.getRole()).toUpperCase();
        boolean isAdmin = currentRole.contains("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy đơn này.");
        }

        // 2. LOGIC CHẶN HỦY KHI ĐÃ THANH TOÁN (User thường)
        if (!isAdmin) {
            // Nếu trạng thái là WAITING (Đã bấm thanh toán/Chờ duyệt)
            if ("WAITING".equals(booking.getStatus())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                     "Đơn đang chờ duyệt thanh toán, bạn không thể hủy lúc này!");
            }
            
            // Nếu trạng thái là CONFIRMED (Đã thanh toán xong)
            if ("CONFIRMED".equals(booking.getStatus())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                     "Đơn đã thanh toán thành công, không thể hủy!");
            }

            // Nếu đơn đã Hoàn thành hoặc đã Hủy từ trước
            if ("CANCELLED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn này không thể hủy được nữa.");
            }
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Transactional
    public void confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn"));
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }
    
    // User bấm "Đã chuyển khoản" -> Gọi hàm này
    @Transactional
    public void requestPaymentConfirmation(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn"));
        
        // Đặt trạng thái mới: WAITING (Chờ Admin duyệt)
        booking.setStatus("WAITING"); 
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