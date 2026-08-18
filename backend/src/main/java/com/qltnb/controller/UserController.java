package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.UserRequest;
import com.qltnb.dto.UserResponse;
import com.qltnb.security.CustomUserDetails;
import com.qltnb.entity.NguoiDung;
import com.qltnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @RequestBody UserRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NguoiDung currentUser = userDetails.getNguoiDung();
        UserResponse data = userService.createUser(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NguoiDung currentUser = userDetails.getNguoiDung();
        List<UserResponse> data = userService.getAllUsers(currentUser);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NguoiDung currentUser = userDetails.getNguoiDung();
        UserResponse data = userService.getUserDetail(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id,
            @RequestBody UserRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NguoiDung currentUser = userDetails.getNguoiDung();
        UserResponse data = userService.updateUser(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NguoiDung currentUser = userDetails.getNguoiDung();
        userService.deleteUser(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Xóa tài khoản người dùng thành công."));
    }
}
