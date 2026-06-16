package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DUYET_TAI_LIEU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuyetTaiLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer DTL_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TL_id")
    private TaiLieu taiLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_duyet_id")
    private NguoiDung nguoiDuyet;

    @Enumerated(EnumType.STRING)
    @Column(name = "DTL_trangThai", columnDefinition = "TEXT")
    private TrangThaiTaiLieu DTL_trangThai;

    @Column(name = "DTL_ghiChu", columnDefinition = "TEXT")
    private String DTL_ghiChu;

    @Column(name = "DTL_timeDuyet")
    private LocalDateTime DTL_timeDuyet;
}