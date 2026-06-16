package com.qltnb.controller;

import com.qltnb.entity.TaiLieu;
import com.qltnb.repository.TaiLieuRepository;
import com.qltnb.service.DocumentScanService;
import com.qltnb.service.DocumentImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*") // Cho phép kết nối CORS từ frontend Vue 3
public class DocumentController {

    @Autowired
    private DocumentScanService documentScanService;

    @Autowired
    private DocumentImportService documentImportService;

    @Autowired
    private TaiLieuRepository taiLieuRepository;

    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scanAndImportDocuments() {
        // Bước 1: Quét toàn bộ thư mục và tách nội dung text
        List<DocumentScanService.ScanResult> scanResults = documentScanService.scanDocuments();
        
        // Bước 2: Đối chiếu lưu DB và tạo báo cáo tổng hợp
        Map<String, Object> responseReport = documentImportService.importScannedData(scanResults);
        
        return ResponseEntity.ok(responseReport);
    }

    @GetMapping("/tai-lieu")
    public ResponseEntity<Map<String, Object>> getDocumentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer danhMuc,
            @RequestParam(required = false) String tuKhoa) {

        // 1. Phân trang, sắp xếp được xử lý cứng trong JPQL để tránh lỗi dấu gạch dưới (_) trong Spring Data Sort
        Pageable pageable = PageRequest.of(page, size);
        Page<TaiLieu> pageResult;

        // 2. Filter động theo từ khóa tìm kiếm hoặc danh mục
        if (danhMuc != null && tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            pageResult = taiLieuRepository.findByDanhMucIdAndTlTenContaining(danhMuc, tuKhoa, pageable);
        } else if (danhMuc != null) {
            pageResult = taiLieuRepository.findByDanhMucId(danhMuc, pageable);
        } else if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            pageResult = taiLieuRepository.findByTlTenContaining(tuKhoa, pageable);
        } else {
            pageResult = taiLieuRepository.findAllOrderByTL_ngayTaoDesc(pageable);
        }

        // 3. Map DTO nhằm ẨN cột đường dẫn máy chủ vật lý (TL_duongDan) bảo mật nội bộ
        List<Map<String, Object>> customContent = pageResult.getContent().stream().map(doc -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", doc.getTL_id());
            item.put("ten", doc.getTL_ten());
            item.put("soHieu", doc.getTL_soHieu());
            item.put("dinhDang", doc.getTL_dinhDang());
            item.put("dungLuong", doc.getTL_dungLuong());
            item.put("ngayTao", doc.getTL_ngayTao());
            
            // Tính trạng thái từ lịch sử duyệt
            String status = "nhap";
            if (doc.getDanhSachDuyet() != null && !doc.getDanhSachDuyet().isEmpty()) {
                status = doc.getDanhSachDuyet().get(doc.getDanhSachDuyet().size() - 1).getDTL_trangThai().name().toLowerCase();
            }
            item.put("trangThai", status);
            
            // Lấy tên quan hệ từ liên kết bảng để hiển thị nhãn đẹp trên giao diện Vue 3
            item.put("danhMuc", doc.getDanhMuc() != null ? doc.getDanhMuc().getDM_ten() : "Chưa phân loại");
            item.put("loaiTaiLieu", doc.getLoaiTaiLieuPhapLy() != null ? doc.getLoaiTaiLieuPhapLy().getLTLPL_ten() : "Bộ luật");
            
            return item;
        }).collect(Collectors.toList());

        // 4. Đóng gói chuẩn cấu hình phân trang Pageable
        Map<String, Object> response = new HashMap<>();
        response.put("content", customContent);
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("currentPage", pageResult.getNumber());

        return ResponseEntity.ok(response);
    }
}
