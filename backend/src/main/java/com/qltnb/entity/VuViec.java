package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "VU_VIEC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VuViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer VV_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KH_id")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_phuTrach_id")
    private NguoiDung nguoiPhuTrach;

    @Column(name = "VV_ten", length = 200)
    private String VV_ten;

    @Column(name = "VV_loai", columnDefinition = "TEXT")
    private String VV_loai;

    @Column(name = "VV_trangThai", columnDefinition = "TEXT")
    private String VV_trangThai;

    @Column(name = "VV_ngayMo")
    private LocalDateTime VV_ngayMo;

    @Column(name = "VV_ngayDong")
    private LocalDateTime VV_ngayDong;

    @Column(name = "VV_ghiChu", columnDefinition = "TEXT")
    private String VV_ghiChu;

    @OneToMany(mappedBy = "vuViec", fetch = FetchType.LAZY)
    private List<TaiLieu> danhSachTaiLieu;
}