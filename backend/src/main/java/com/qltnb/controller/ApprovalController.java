package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.ApprovalHistoryResponse;
import com.qltnb.dto.RejectRequest;
import com.qltnb.entity.NguoiDung;
import com.qltnb.repository.NguoiDungRepository;
import com.qltnb.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents/{documentId}/approval")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final NguoiDungRepository nguoiDungRepository;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<String>> submit(
            @PathVariable Long documentId, 
            @AuthenticationPrincipal UserDetails userDetails) {
        NguoiDung currentUser = nguoiDungRepository.findByTaiKhoan(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản phiên làm việc hiện tại không hợp lệ."));

        approvalService.submit(documentId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Gửi yêu cầu phê duyệt văn bản thành công."));
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<String>> approve(
            @PathVariable Long documentId, 
            @AuthenticationPrincipal UserDetails userDetails) {
        NguoiDung currentUser = nguoiDungRepository.findByTaiKhoan(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản phiên làm việc hiện tại không hợp lệ."));

        approvalService.approve(documentId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Phê duyệt tài liệu thành công. Trạng thái đổi thành DA_DUYET."));
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<String>> reject(
            @PathVariable Long documentId, 
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RejectRequest request) {
        NguoiDung currentUser = nguoiDungRepository.findByTaiKhoan(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản phiên làm việc hiện tại không hợp lệ."));

        approvalService.reject(documentId, currentUser.getId(), request.getGhiChu());
        return ResponseEntity.ok(ApiResponse.success("Từ chối phê duyệt tài liệu."));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ApprovalHistoryResponse>>> getHistory(@PathVariable Long documentId) {
        List<ApprovalHistoryResponse> data = approvalService.getHistory(documentId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
