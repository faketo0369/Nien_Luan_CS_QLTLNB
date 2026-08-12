package com.qltnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String hoTen;
    private String taiKhoan;
    private String email;
    private String vaiTro;
    private String boPhan;
    private String trangThai; // HOAT_DONG, KHOA
    private String chuyenMon;
    private String soChungChi;
}
