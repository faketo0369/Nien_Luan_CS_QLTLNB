package com.qltnb.security;

import com.qltnb.entity.NguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final NguoiDung nguoiDung;

    public CustomUserDetails(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = nguoiDung.getVaiTro() != null ? nguoiDung.getVaiTro().getVT_ten() : "NHAN_VIEN";
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return nguoiDung.getND_matKhau();
    }

    @Override
    public String getUsername() {
        return nguoiDung.getND_taiKhoan();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"KHOA".equalsIgnoreCase(nguoiDung.getTrangThai());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return nguoiDung.getND_trangThaiTK() == null || nguoiDung.getND_trangThaiTK();
    }
}
