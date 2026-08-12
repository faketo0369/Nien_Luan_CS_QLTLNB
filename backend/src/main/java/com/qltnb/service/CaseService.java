package com.qltnb.service;

import com.qltnb.dto.CaseRequest;
import com.qltnb.dto.CaseResponse;
import com.qltnb.dto.DocumentResponse;
import com.qltnb.entity.KhachHang;
import com.qltnb.entity.NguoiDung;
import com.qltnb.entity.VuViec;
import com.qltnb.repository.TaiLieuRepository;
import com.qltnb.repository.VuViecRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final VuViecRepository vuViecRepository;
    private final TaiLieuRepository taiLieuRepository;
    private final DocumentService documentService;
    private final NotificationService notificationService;

    @Transactional
    public CaseResponse createCase(CaseRequest request) {
        VuViec vv = new VuViec();
        mapDtoToEntity(request, vv);
        VuViec saved = vuViecRepository.save(vv);
        notificationService.createNotificationForAll("VU_VIEC", "Vụ việc mới", "Vụ việc '" + saved.getVV_ten() + "' đã được tạo mới.");
        return mapEntityToDto(saved);
    }

    public List<CaseResponse> getAllCases(String trangThai, String loai, Long nguoiPhuTrachId) {
        Specification<VuViec> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (trangThai != null && !trangThai.isBlank()) {
                predicates.add(cb.equal(root.get("VV_trangThai"), trangThai.trim()));
            }
            if (loai != null && !loai.isBlank()) {
                predicates.add(cb.equal(root.get("VV_loai"), loai.trim()));
            }
            if (nguoiPhuTrachId != null) {
                predicates.add(cb.equal(root.get("nguoiPhuTrach").get("ND_id"), nguoiPhuTrachId.intValue()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return vuViecRepository.findAll(spec).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public CaseResponse getCaseDetail(Long id) {
        VuViec vv = vuViecRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vụ việc tố tụng yêu cầu."));
        
        CaseResponse response = mapEntityToDto(vv);
        
        // Tích hợp danh sách tài liệu thuộc vụ việc bằng cách tái sử dụng DocumentService
        List<DocumentResponse> documents = documentService.getByVuViecId(id);
        response.setDsTaiLieu(documents);
        
        return response;
    }

    @Transactional
    public CaseResponse updateCase(Long id, CaseRequest request) {
        VuViec vv = vuViecRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vụ việc cần chỉnh sửa thông tin."));
        mapDtoToEntity(request, vv);
        VuViec saved = vuViecRepository.save(vv);
        notificationService.createNotificationForAll("VU_VIEC", "Cập nhật vụ việc", "Vụ việc '" + saved.getVV_ten() + "' đã được chỉnh sửa thông tin.");
        return mapEntityToDto(saved);
    }

    @Transactional
    public void deleteCase(Long id) {
        VuViec vv = vuViecRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Vụ việc không tồn tại trên hệ thống."));
        // Thực hiện xóa vật lý vì bảng VU_VIEC không hỗ trợ soft delete (không có cột VV_daXoa)
        vuViecRepository.delete(vv);
        notificationService.createNotificationForAll("VU_VIEC", "Xóa vụ việc", "Vụ việc '" + vv.getVV_ten() + "' đã bị gỡ bỏ khỏi hệ thống.");
    }

    private void mapDtoToEntity(CaseRequest dto, VuViec entity) {
        entity.setVV_ten(dto.getTen());
        entity.setVV_loai(dto.getLoai());
        entity.setVV_trangThai(dto.getTrangThai());
        entity.setVV_ngayMo(dto.getNgayMo() != null ? dto.getNgayMo().atStartOfDay() : null);
        entity.setVV_ngayDong(dto.getNgayDong() != null ? dto.getNgayDong().atStartOfDay() : null);
        entity.setVV_ghiChu(dto.getGhiChu());
        
        if (dto.getKhachHangId() != null) { 
            KhachHang kh = new KhachHang();
            kh.setKH_id(dto.getKhachHangId().intValue());
            entity.setKhachHang(kh); 
        } else {
            entity.setKhachHang(null);
        }
        
        if (dto.getNguoiPhuTrachId() != null) { 
            NguoiDung nd = new NguoiDung();
            nd.setND_id(dto.getNguoiPhuTrachId().intValue());
            entity.setNguoiPhuTrach(nd); 
        } else {
            entity.setNguoiPhuTrach(null);
        }
    }

    private CaseResponse mapEntityToDto(VuViec entity) {
        CaseResponse res = new CaseResponse();
        res.setId(entity.getVV_id() != null ? entity.getVV_id().longValue() : null);
        res.setTen(entity.getVV_ten());
        res.setLoai(entity.getVV_loai());
        res.setTrangThai(entity.getVV_trangThai());
        res.setNgayMo(entity.getVV_ngayMo() != null ? entity.getVV_ngayMo().toLocalDate() : null);
        res.setNgayDong(entity.getVV_ngayDong() != null ? entity.getVV_ngayDong().toLocalDate() : null);
        res.setGhiChu(entity.getVV_ghiChu());

        if (entity.getKhachHang() != null) {
            res.setKhachHang(new CaseResponse.ClientSummary(
                    entity.getKhachHang().getKH_id() != null ? entity.getKhachHang().getKH_id().longValue() : null, 
                    entity.getKhachHang().getKH_ten()));
        }
        if (entity.getNguoiPhuTrach() != null) {
            res.setNguoiPhuTrach(new CaseResponse.UserSummary(
                    entity.getNguoiPhuTrach().getND_id() != null ? entity.getNguoiPhuTrach().getND_id().longValue() : null, 
                    entity.getNguoiPhuTrach().getND_hoTen()));
        }

        // Đếm số lượng tài liệu thuộc về vụ việc luật này
        long docCount = entity.getVV_id() != null ? taiLieuRepository.countByVuViecId(entity.getVV_id()) : 0L;
        res.setSoTaiLieu(docCount);

        return res;
    }
}
