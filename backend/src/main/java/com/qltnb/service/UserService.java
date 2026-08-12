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

    public List<UserResponse> getAllUsers() {
        return nguoiDungRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserDetail(Long id) {
        NguoiDung u = nguoiDungRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
        return mapToResponse(u);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (nguoiDungRepository.findByTaiKhoan(request.getTaiKhoan()).isPresent()) {
            throw new RuntimeException("Tên tài khoản đã tồn tại.");
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
    public UserResponse updateUser(Long id, UserRequest request) {
        NguoiDung u = nguoiDungRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

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
    public void deleteUser(Long id) {
        NguoiDung u = nguoiDungRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
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
