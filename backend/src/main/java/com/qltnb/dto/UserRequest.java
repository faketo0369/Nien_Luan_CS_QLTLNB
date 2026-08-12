package com.qltnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String hoTen;
    private String taiKhoan;
    private String matKhau;
    private String email;
    private String vaiTro; // ADMIN, TRUONG_PHONG, NHAN_VIEN
    private Integer boPhanId;
    private String chuyenMon;
    private String soChungChi;
}
