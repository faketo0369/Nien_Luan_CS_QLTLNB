package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.NotificationResponse;
import com.qltnb.entity.NguoiDung;
import com.qltnb.repository.NguoiDungRepository;
import com.qltnb.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        NguoiDung currentUser = nguoiDungRepository.findByTaiKhoan(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản phiên làm việc hiện tại không hợp lệ."));

        List<NotificationResponse> data = notificationService.getMyNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu thông báo là đã đọc."));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        NguoiDung currentUser = nguoiDungRepository.findByTaiKhoan(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản phiên làm việc hiện tại không hợp lệ."));

        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu tất cả thông báo là đã đọc."));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        NguoiDung currentUser = nguoiDungRepository.findByTaiKhoan(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản phiên làm việc hiện tại không hợp lệ."));

        long count = notificationService.countUnread(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
