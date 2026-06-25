package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao_moi")
@Data
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_nhan_id", nullable = false)
    private NguoiDung nguoiNhan;

    @Column(name = "loai", nullable = false)
    private String loai; // YEU_CAU_DUYET, DA_DUYET, TU_CHOI, DUOC_CAP_QUYEN, HET_HAN_QUYEN

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, length = 1000)
    private String noiDung;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;
}