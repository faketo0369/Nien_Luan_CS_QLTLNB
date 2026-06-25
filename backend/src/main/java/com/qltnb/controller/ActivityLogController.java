package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.ActivityLogResponse;
import com.qltnb.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ActivityLogResponse>>> getLogs(
            @RequestParam(required = false) Long nguoiDungId,
            @RequestParam(required = false) Long taiLieuId,
            @RequestParam(required = false) String loaiHanhDong,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
                
        Page<ActivityLogResponse> data = activityLogService.getLogs(nguoiDungId, taiLieuId, loaiHanhDong, tuNgay, denNgay, page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
