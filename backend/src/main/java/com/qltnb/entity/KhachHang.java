package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KHACH_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer KH_id;

    @Column(name = "KH_ten", columnDefinition = "TEXT")
    private String KH_ten;

    @Enumerated(EnumType.STRING)
    @Column(name = "KH_loai", columnDefinition = "TEXT")
    private LoaiKhachHang KH_loai;

    @Column(name = "KH_CCCD_MST", length = 50)
    private String KH_CCCD_MST;

    @Column(name = "KH_sdt", columnDefinition = "TEXT")
    private String KH_sdt;

    @Column(name = "KH_diaChi", columnDefinition = "TEXT")
    private String KH_diaChi;

    @Column(name = "KH_email", columnDefinition = "TEXT")
    private String KH_email;

    @Column(name = "KH_ngayTao")
    private LocalDateTime KH_ngayTao;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<VuViec> danhSachVuViec;
}