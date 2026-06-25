package com.qltnb.service;

import com.qltnb.dto.ActivityLogResponse;
import com.qltnb.entity.LichSuHoatDong;
import com.qltnb.repository.LichSuHoatDongRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final LichSuHoatDongRepository repository;

    @Transactional
    public void log(Long nguoiDungId, Long taiLieuId, String loaiHanhDong, String moTa) {
        LichSuHoatDong entity = new LichSuHoatDong();
        entity.setNguoiDungId(nguoiDungId);
        entity.setTaiLieuId(taiLieuId);
        entity.setLoaiHanhDong(loaiHanhDong != null ? loaiHanhDong.toUpperCase() : null);
        entity.setMoTa(moTa);
        entity.setTimeLog(LocalDateTime.now());
        repository.save(entity);
    }

    public Page<ActivityLogResponse> getLogs(Long nguoiDungId, Long taiLieuId, String loaiHanhDong, 
                                            LocalDate tuNgay, LocalDate denNgay, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timeLog").descending());

        Specification<LichSuHoatDong> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nguoiDungId != null) {
                predicates.add(cb.equal(root.get("nguoiDungId"), nguoiDungId));
            }
            if (taiLieuId != null) {
                predicates.add(cb.equal(root.get("taiLieuId"), taiLieuId));
            }
            if (loaiHanhDong != null && !loaiHanhDong.isBlank()) {
                predicates.add(cb.equal(root.get("loaiHanhDong"), loaiHanhDong.trim().toUpperCase()));
            }
            if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timeLog"), LocalDateTime.of(tuNgay, LocalTime.MIN)));
            }
            if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timeLog"), LocalDateTime.of(denNgay, LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable).map(this::mapToDto);
    }

    private ActivityLogResponse mapToDto(LichSuHoatDong entity) {
        ActivityLogResponse dto = new ActivityLogResponse();
        dto.setId(entity.getId());
        dto.setNguoiDungId(entity.getNguoiDungId());
        dto.setTaiLieuId(entity.getTaiLieuId());
        dto.setLoaiHanhDong(entity.getLoaiHanhDong());
        dto.setMoTa(entity.getMoTa());
        dto.setTimeLog(entity.getTimeLog());
        dto.setTenNguoiDung("Người dùng ID: " + entity.getNguoiDungId());
        dto.setTenTaiLieu(entity.getTaiLieuId() != null ? "Tài liệu văn bản ID: " + entity.getTaiLieuId() : "Không đính kèm");
        return dto;
    }
}
