package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LICH_SU_HOAT_DONG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuHoatDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer LS_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_id")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TL_id")
    private TaiLieu taiLieu;

    @Column(name = "LS_hoatDong", columnDefinition = "TEXT")
    private String LS_hoatDong;

    @Column(name = "LS_thoiGianSua")
    private LocalDateTime LS_thoiGianSua;

    @Column(name = "LS_diaChiIP", columnDefinition = "TEXT")
    private String LS_diaChiIP;
}