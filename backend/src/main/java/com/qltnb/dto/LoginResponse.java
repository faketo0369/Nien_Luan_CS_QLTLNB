package com.qltnb.dto;

import com.qltnb.entity.VaiTro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String hoTen;
    private String taiKhoan;
    private String email;
    private String vaiTro;
    private String boPhan;

    public LoginResponse(String token, String taiKhoan, String vaiTro) {
        this.token = token;
        this.taiKhoan = taiKhoan;
        this.vaiTro = vaiTro;
    }

    public LoginResponse(String token, String taiKhoan, VaiTro vaiTroObj) {
        this.token = token;
        this.taiKhoan = taiKhoan;
        this.vaiTro = vaiTroObj != null ? vaiTroObj.getVT_ten() : null;
    }
}
