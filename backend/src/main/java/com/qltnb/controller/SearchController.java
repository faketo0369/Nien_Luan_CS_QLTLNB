package com.qltnb.controller;

import com.qltnb.dto.*;
import com.qltnb.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/documents")
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> searchDocuments(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long danhMucId,
            @RequestParam(required = false) Long loaiTaiLieuId,
            @RequestParam(required = false) Long boPhanId,
            @RequestParam(required = false) Long vuViecId,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Boolean baoMat,
            @RequestParam(required = false) String dinhDang,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        String sortFieldFinal;
        if ("id".equals(sortField)) {
            sortFieldFinal = "TL_id";
        } else if ("ten".equals(sortField)) {
            sortFieldFinal = "TL_ten";
        } else if ("ngayTao".equals(sortField)) {
            sortFieldFinal = "TL_ngayTao";
        } else {
            sortFieldFinal = sortField;
        }

        Sort sortObj = Sort.by(sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortFieldFinal);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<DocumentResponse> data = searchService.searchDocuments(q, danhMucId, loaiTaiLieuId, boPhanId, vuViecId, trangThai, baoMat, dinhDang, tuNgay, denNgay, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<Page<ClientResponse>>> searchClients(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String loai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("KH_id").descending());
        Page<ClientResponse> data = searchService.searchClients(q, loai, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/cases")
    public ResponseEntity<ApiResponse<Page<CaseResponse>>> searchCases(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Long nguoiPhuTrachId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("VV_id").descending());
        Page<CaseResponse> data = searchService.searchCases(q, loai, trangThai, nguoiPhuTrachId, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<GlobalSearchResponse>> globalSearch(@RequestParam String q) {
        GlobalSearchResponse data = searchService.globalSearch(q);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
