package com.qltnb.service;

import com.qltnb.dto.ClientRequest;
import com.qltnb.dto.ClientResponse;
import com.qltnb.entity.KhachHang;
import com.qltnb.entity.LoaiKhachHang;
import com.qltnb.repository.KhachHangRepository;
import com.qltnb.repository.VuViecRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final KhachHangRepository khachHangRepository;
    private final VuViecRepository vuViecRepository;
    private final NotificationService notificationService;

    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        KhachHang kh = new KhachHang();
        mapDtoToEntity(request, kh);
        kh.setKH_ngayTao(LocalDateTime.now());
        KhachHang saved = khachHangRepository.save(kh);
        notificationService.createNotificationForAll("KHACH_HANG", "Khách hàng mới", "Hồ sơ khách hàng '" + saved.getKH_ten() + "' đã được khởi tạo.");
        return mapEntityToDto(saved);
    }

    public List<ClientResponse> getAllClients(String ten, String loai, String cccdMst) {
        Specification<KhachHang> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (ten != null && !ten.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("KH_ten")), "%" + ten.trim().toLowerCase() + "%"));
            }
            if (loai != null && !loai.isBlank()) {
                try {
                    predicates.add(cb.equal(root.get("KH_loai"), LoaiKhachHang.valueOf(loai.trim().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    // Nếu giá trị loai không hợp lệ thì không khớp với bản ghi nào
                    predicates.add(cb.disjunction());
                }
            }
            if (cccdMst != null && !cccdMst.isBlank()) {
                predicates.add(cb.equal(root.get("KH_CCCD_MST"), cccdMst.trim()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return khachHangRepository.findAll(spec).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public ClientResponse getClientDetail(Long id) {
        KhachHang kh = khachHangRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng yêu cầu."));
        
        ClientResponse response = mapEntityToDto(kh);
        
        // Lấy danh sách vụ việc liên quan thực tế
        List<ClientResponse.CaseSummary> cases = vuViecRepository.findByKhachHangId(id.intValue()).stream()
                .map(c -> new ClientResponse.CaseSummary(
                        c.getVV_id() != null ? c.getVV_id().longValue() : null, 
                        c.getVV_ten(), 
                        c.getVV_trangThai()))
                .collect(Collectors.toList());
        response.setDsVuViec(cases);
        
        return response;
    }

    @Transactional
    public ClientResponse updateClient(Long id, ClientRequest request) {
        KhachHang kh = khachHangRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu khách hàng cần sửa."));
        mapDtoToEntity(request, kh);
        KhachHang saved = khachHangRepository.save(kh);
        notificationService.createNotificationForAll("KHACH_HANG", "Cập nhật khách hàng", "Hồ sơ khách hàng '" + saved.getKH_ten() + "' đã được cập nhật thông tin.");
        return mapEntityToDto(saved);
    }

    @Transactional
    public void deleteClient(Long id) {
        KhachHang kh = khachHangRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại."));
        // Thực hiện xóa vật lý vì bảng KHACH_HANG không hỗ trợ soft delete (không có cột KH_daXoa)
        khachHangRepository.delete(kh);
        notificationService.createNotificationForAll("KHACH_HANG", "Xóa khách hàng", "Hồ sơ khách hàng '" + kh.getKH_ten() + "' đã bị gỡ bỏ khỏi hệ thống.");
    }

    private void mapDtoToEntity(ClientRequest dto, KhachHang entity) {
        entity.setKH_ten(dto.getTen());
        if (dto.getLoai() != null && !dto.getLoai().isBlank()) {
            entity.setKH_loai(LoaiKhachHang.valueOf(dto.getLoai().trim().toUpperCase()));
        } else {
            entity.setKH_loai(null);
        }
        entity.setKH_CCCD_MST(dto.getCccdMst());
        entity.setKH_sdt(dto.getSdt());
        entity.setKH_diaChi(dto.getDiaChi());
        entity.setKH_email(dto.getEmail());
    }

    private ClientResponse mapEntityToDto(KhachHang entity) {
        ClientResponse res = new ClientResponse();
        res.setId(entity.getKH_id() != null ? entity.getKH_id().longValue() : null);
        res.setTen(entity.getKH_ten());
        res.setLoai(entity.getKH_loai() != null ? entity.getKH_loai().name() : null);
        res.setCccdMst(entity.getKH_CCCD_MST());
        res.setSdt(entity.getKH_sdt());
        res.setDiaChi(entity.getKH_diaChi());
        res.setEmail(entity.getKH_email());
        
        // Đếm tổng số vụ việc của khách hàng
        res.setSoVuViec(entity.getKH_id() != null ? vuViecRepository.countByKhachHangId(entity.getKH_id()) : 0L);
        return res;
    }
}
