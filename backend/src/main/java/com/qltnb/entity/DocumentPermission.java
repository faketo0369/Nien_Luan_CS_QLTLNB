package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tai_lieu_quyen_moi")
@Data
public class DocumentPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_lieu_id", nullable = false)
    private TaiLieu document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bo_phan_id")
    private BoPhan boPhan;

    @Column(name = "loai_quyen", nullable = false)
    private String loaiQuyen; // XEM, TAI, SUA, XOA

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;
}
