package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.CaseRequest;
import com.qltnb.dto.CaseResponse;
import com.qltnb.dto.DocumentResponse;
import com.qltnb.service.CaseService;
import com.qltnb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CaseController {

    private final CaseService caseService;
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CaseResponse>> create(@RequestBody CaseRequest request) {
        CaseResponse data = caseService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CaseResponse>>> getAll(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) Long nguoiPhuTrachId) {
        List<CaseResponse> data = caseService.getAllCases(trangThai, loai, nguoiPhuTrachId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseResponse>> getById(@PathVariable Long id) {
        CaseResponse data = caseService.getCaseDetail(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseResponse>> update(
            @PathVariable Long id, 
            @RequestBody CaseRequest request) {
        CaseResponse data = caseService.updateCase(id, request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        caseService.deleteCase(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa mềm hồ sơ vụ việc thành công."));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getCaseDocuments(@PathVariable Long id) {
        List<DocumentResponse> data = documentService.getByVuViecId(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
