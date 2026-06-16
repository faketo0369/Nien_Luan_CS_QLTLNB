package com.qltnb.controller;

import com.qltnb.entity.NguoiDung;
import com.qltnb.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Cho phép Frontend Vue 3 kết nối tự do không bị chặn CORS
public class AuthController {

    @Autowired
    private NguoiDungRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String taiKhoan = credentials.get("taiKhoan");
        String matKhau = credentials.get("matKhau");

        // 1. Tìm tài khoản trong DB
        Optional<NguoiDung> userOpt = userRepository.findByND_taiKhoan(taiKhoan);

        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Tài khoản không tồn tại trên hệ thống!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        NguoiDung user = userOpt.get();

        // 2. Verify mật khẩu bằng BCrypt với phương án dự phòng (fallback) cho dữ liệu giả lập (seed data)
        boolean matches = false;
        try {
            matches = passwordEncoder.matches(matKhau, user.getND_matKhau());
        } catch (Exception e) {
            // Bỏ qua ngoại lệ định dạng BCrypt không hợp lệ để tiếp tục kiểm tra dự phòng
        }

        if (!matches) {
            String dbPass = user.getND_matKhau();
            if ("$2y$demo-admin".equals(dbPass) && ("admin".equals(matKhau) || "demo-admin".equals(matKhau))) {
                matches = true;
            } else if ("$2y$demo-manager".equals(dbPass) && ("manager".equals(matKhau) || "demo-manager".equals(matKhau) || "truongphong.dansu".equals(matKhau))) {
                matches = true;
            } else if ("$2y$demo-staff".equals(dbPass) && ("staff".equals(matKhau) || "demo-staff".equals(matKhau) || "nhanvien.dansu".equals(matKhau))) {
                matches = true;
            } else if (dbPass != null && dbPass.equals(matKhau)) {
                matches = true;
            }
        }

        if (!matches) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Mật khẩu không chính xác!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // 3. Khớp thông tin -> Trả về profile để Frontend test
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getND_id());
        response.put("hoTen", user.getND_hoTen());
        response.put("taiKhoan", user.getND_taiKhoan());
        response.put("email", user.getND_email());
        response.put("vaiTro", user.getVaiTro() != null ? user.getVaiTro().getVT_ten() : null);
        response.put("boPhan", user.getBoPhan() != null ? user.getBoPhan().getBP_ten() : null);

        return ResponseEntity.ok(response);
    }
}
