package com.qltnb.security;

import com.qltnb.entity.NguoiDung;
import com.qltnb.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung user = nguoiDungRepository.findByTaiKhoanWithVaiTro(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với tài khoản: " + username));
        return new CustomUserDetails(user);
    }
}
