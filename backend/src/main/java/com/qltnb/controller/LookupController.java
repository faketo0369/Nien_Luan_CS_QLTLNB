package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.DocumentResponse.RelationSummary;
import com.qltnb.dto.LoginResponse;
import com.qltnb.entity.BoPhan;
import com.qltnb.entity.DanhMuc;
import com.qltnb.entity.LoaiTaiLieuPhapLy;
import com.qltnb.entity.NguoiDung;
import com.qltnb.repository.BoPhanRepository;
import com.qltnb.repository.DanhMucRepository;
import com.qltnb.repository.LoaiTaiLieuPhapLyRepository;
import com.qltnb.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LookupController {

    private final DanhMucRepository danhMucRepository;
    private final LoaiTaiLieuPhapLyRepository loaiTaiLieuPhapLyRepository;
    private final BoPhanRepository boPhanRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<RelationSummary>>> getCategories() {
        List<RelationSummary> list = danhMucRepository.findAll().stream()
                .map(dm -> new RelationSummary(
                        dm.getDM_id() != null ? dm.getDM_id().longValue() : null,
                        dm.getDM_ten()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/doc-types")
    public ResponseEntity<ApiResponse<List<RelationSummary>>> getDocTypes() {
        List<RelationSummary> list = loaiTaiLieuPhapLyRepository.findAll().stream()
                .map(lt -> new RelationSummary(
                        lt.getLTLPL_id() != null ? lt.getLTLPL_id().longValue() : null,
                        lt.getLTLPL_ten()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<RelationSummary>>> getDepartments() {
        List<RelationSummary> list = boPhanRepository.findAll().stream()
                .map(bp -> new RelationSummary(
                        bp.getBP_id() != null ? bp.getBP_id().longValue() : null,
                        bp.getBP_ten()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<LoginResponse>>> getUsers() {
        List<LoginResponse> list = nguoiDungRepository.findAll().stream()
                .map(u -> new LoginResponse(
                        null, // Token is not needed for lookup
                        u.getId(),
                        u.getHoTen(),
                        u.getTaiKhoan(),
                        u.getND_email(),
                        u.getVaiTro() != null ? u.getVaiTro().getVT_ten() : null,
                        u.getBoPhan() != null ? u.getBoPhan().getBP_ten() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
