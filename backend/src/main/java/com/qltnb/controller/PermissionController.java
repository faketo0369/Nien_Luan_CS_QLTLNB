package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.PermissionRequest;
import com.qltnb.dto.PermissionResponse;
import com.qltnb.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents/{documentId}/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> grant(
            @PathVariable Long documentId, 
            @RequestBody PermissionRequest request) {
        PermissionResponse data = permissionService.grantPermission(documentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getList(@PathVariable Long documentId) {
        List<PermissionResponse> data = permissionService.getPermissions(documentId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> revoke(
            @PathVariable Long documentId, 
            @PathVariable Long id) {
        permissionService.revokePermission(id);
        return ResponseEntity.ok(ApiResponse.success("Thu hồi quyền truy cập của tài liệu thành công."));
    }
}
