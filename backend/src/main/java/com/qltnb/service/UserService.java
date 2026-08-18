package com.qltnb.service;

import com.qltnb.dto.UserRequest;
import com.qltnb.dto.UserResponse;
import com.qltnb.entity.NguoiDung;
import com.qltnb.repository.BoPhanRepository;
import com.qltnb.repository.NguoiDungRepository;
import com.qltnb.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final NguoiDungRepository nguoiDungRepository;
    private final BoPhanRepository boPhanRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public List<UserResponse> getAllUsers(NguoiDung currentUser) {
        String role = currentUser.getVaiTro() != null ? currentUser.getVaiTro().getVT_ten() : "NHAN_VIEN";
        if ("ADMIN".equals(role)) {
            return nguoiDungRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if ("TRUONG_PHONG".equals(role)) {
            Integer bpId = currentUser.getBoPhan() != null ? currentUser.getBoPhan().getBP_id() : null;
            if (bpId == null) {
                return java.util.Collections.emptyList();
            }
            return nguoiDungRepository.findAll().stream()
                    .filter(u -> u.getBoPhan() != null && u.getBoPhan().getBP_id().equals(bpId))
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    public UserResponse getUserDetail(Long id, NguoiDung currentUser) {
        NguoiDung u = nguoiDungRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
        String role = currentUser.getVaiTro() != null ? currentUser.getVaiTro().getVT_ten() : "NHAN_VIEN";
        if ("TRUONG_PHONG".equals(role)) {
            Integer bpId = currentUser.getBoPhan() != null ? currentUser.getBoPhan().getBP_id() : null;
            if (bpId == null || u.getBoPhan() == null || !u.getBoPhan().getBP_id().equals(bpId)) {
                throw new RuntimeException("Bạn không có quyền truy cập tài khoản của bộ phận khác.");
            }
        }
        return mapToResponse(u);
    }

    @Transactional
    public UserResponse createUser(UserRequest request, NguoiDung currentUser) {
        if (nguoiDungRepository.findByTaiKhoan(request.getTaiKhoan()).isPresent()) {
            throw new RuntimeException("Tên tài khoản đã tồn tại.");
        }
        
        String role = currentUser.getVaiTro() != null ? currentUser.getVaiTro().getVT_ten() : "NHAN_VIEN";
        if ("TRUONG_PHONG".equals(role)) {
            Integer bpId = currentUser.getBoPhan() != null ? currentUser.getBoPhan().getBP_id() : null;
            if (bpId == null) {
                throw new RuntimeException("Tài khoản của bạn chưa được liên kết với bộ phận nào.");
            }
            request.setBoPhanId(bpId);
            request.setVaiTro("NHAN_VIEN");
        }

        NguoiDung u = new NguoiDung();
        u.setTaiKhoan(request.getTaiKhoan());
        u.setND_hoTen(request.getHoTen());
        u.setND_email(request.getEmail());
        u.setND_chuyenMon(request.getChuyenMon());
        u.setND_chungChi(request.getSoChungChi());
        u.setND_trangThaiTK(true);
        u.setND_soLanSai(0);

        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            u.setND_matKhau(passwordEncoder.encode(request.getMatKhau()));
        } else {
            u.setND_matKhau(passwordEncoder.encode("123456"));
        }

        if (request.getBoPhanId() != null) {
            u.setBoPhan(boPhanRepository.findById(request.getBoPhanId()).orElse(null));
        }

        if (request.getVaiTro() != null) {
            u.setVaiTro(vaiTroRepository.findByVT_ten(request.getVaiTro()).orElse(null));
        }

        u = nguoiDungRepository.save(u);
        notificationService.createNotificationForAll("HE_THONG", "Tài khoản mới", "Tài khoản cho " + u.getHoTen() + " (" + u.getTaiKhoan() + ") đã được tạo trên hệ thống.");
        return mapToResponse(u);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request, NguoiDung currentUser) {
        NguoiDung u = nguoiDungRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        String role = currentUser.getVaiTro() != null ? currentUser.getVaiTro().getVT_ten() : "NHAN_VIEN";
        if ("TRUONG_PHONG".equals(role)) {
            Integer bpId = currentUser.getBoPhan() != null ? currentUser.getBoPhan().getBP_id() : null;
            if (bpId == null || u.getBoPhan() == null || !u.getBoPhan().getBP_id().equals(bpId)) {
                throw new RuntimeException("Bạn không có quyền chỉnh sửa thông tin tài khoản thuộc bộ phận khác.");
            }
            request.setBoPhanId(bpId);
            request.setVaiTro("NHAN_VIEN");
        }

        u.setND_hoTen(request.getHoTen());
        u.setND_email(request.getEmail());
        u.setND_chuyenMon(request.getChuyenMon());
        u.setND_chungChi(request.getSoChungChi());

        if (request.getMatKhau() != null && !request.getMatKhau().trim().isEmpty()) {
            u.setND_matKhau(passwordEncoder.encode(request.getMatKhau()));
        }

        if (request.getBoPhanId() != null) {
            u.setBoPhan(boPhanRepository.findById(request.getBoPhanId()).orElse(null));
        } else {
            u.setBoPhan(null);
        }

        if (request.getVaiTro() != null) {
            u.setVaiTro(vaiTroRepository.findByVT_ten(request.getVaiTro()).orElse(null));
        }

        u = nguoiDungRepository.save(u);
        notificationService.createNotificationForAll("HE_THONG", "Cập nhật tài khoản", "Thông tin tài khoản của " + u.getHoTen() + " đã được cập nhật.");
        return mapToResponse(u);
    }

    @Transactional
    public void deleteUser(Long id, NguoiDung currentUser) {
        NguoiDung u = nguoiDungRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
        
        String role = currentUser.getVaiTro() != null ? currentUser.getVaiTro().getVT_ten() : "NHAN_VIEN";
        if ("TRUONG_PHONG".equals(role)) {
            Integer bpId = currentUser.getBoPhan() != null ? currentUser.getBoPhan().getBP_id() : null;
            if (bpId == null || u.getBoPhan() == null || !u.getBoPhan().getBP_id().equals(bpId)) {
                throw new RuntimeException("Bạn không có quyền xóa tài khoản thuộc bộ phận khác.");
            }
        }

        nguoiDungRepository.delete(u);
        notificationService.createNotificationForAll("HE_THONG", "Xóa tài khoản", "Tài khoản của " + u.getHoTen() + " đã bị gỡ bỏ khỏi hệ thống.");
    }

    private UserResponse mapToResponse(NguoiDung u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setHoTen(u.getHoTen());
        r.setTaiKhoan(u.getTaiKhoan());
        r.setEmail(u.getND_email());
        r.setVaiTro(u.getVaiTro() != null ? u.getVaiTro().getVT_ten() : null);
        r.setBoPhan(u.getBoPhan() != null ? u.getBoPhan().getBP_ten() : null);
        r.setTrangThai(u.getTrangThai());
        r.setChuyenMon(u.getND_chuyenMon());
        r.setSoChungChi(u.getND_chungChi());
        return r;
    }
}
