package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PHIEN_BAN_TAI_LIEU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhienBanTaiLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer PBTL_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TL_id")
    private TaiLieu taiLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_update_id")
    private NguoiDung nguoiCapNhat;

    @Column(name = "PBTL_maPhienBan", columnDefinition = "TEXT")
    private String PBTL_maPhienBan;

    @Column(name = "PBTL_nguoiUpdate", columnDefinition = "TEXT")
    private String PBTL_nguoiUpdate;

    @Column(name = "PBTL_timeUpdate")
    private LocalDateTime PBTL_timeUpdate;

    @Column(name = "PBTL_ghiChu", columnDefinition = "TEXT")
    private String PBTL_ghiChu;

    @Column(name = "PBTL_duongDan", columnDefinition = "TEXT")
    private String PBTL_duongDan;

    @Column(name = "PBTL_kichCo")
    private Long PBTL_kichCo;

    @Column(name = "PBTL_dinhDang", length = 50)
    private String PBTL_dinhDang;
}