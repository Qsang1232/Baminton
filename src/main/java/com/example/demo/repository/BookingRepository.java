package com.example.demo.repository;

import com.example.demo.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    // 1. Kiểm tra trùng lịch:
    // Logic: Một lịch mới (start, end) sẽ trùng với lịch cũ (b.start, b.end) nếU:
    // start < b.end VÀ end > b.start
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Booking b " +
           "WHERE b.court.id = :courtId " +
           "AND b.status != 'CANCELLED' " +
           "AND :startTime < b.endTime " +
           "AND :endTime > b.startTime")
    boolean existsConflictingBooking(@Param("courtId") Long courtId, 
                                     @Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);

    // 2. Lấy danh sách booking trong ngày để hiển thị lên lịch
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId " +
           "AND b.status != 'CANCELLED' " +
           "AND b.startTime BETWEEN :startOfDay AND :endOfDay")
    List<Booking> findBookingsByCourtAndDate(@Param("courtId") Long courtId, 
                                             @Param("startOfDay") LocalDateTime startOfDay,
                                             @Param("endOfDay") LocalDateTime endOfDay);

    // 3. Các hàm thống kê (Giữ nguyên hoặc tùy chỉnh theo DB của bạn)
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status != 'CANCELLED'")
    Double calculateTotalRevenue();

    // Lưu ý: CAST(... as date) có thể khác nhau tùy database (MySQL, PostgreSQL, H2). 
    // Nếu lỗi cú pháp SQL, hãy thử: "AND function('DATE', b.startTime) = CURRENT_DATE"
    @Query("SELECT COUNT(b) FROM Booking b WHERE CAST(b.startTime AS date) = CURRENT_DATE")
    Long countBookingsToday();
}