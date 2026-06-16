package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "QUYEN_TRUY_CAP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuyenTruyCap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer QTC_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TL_id")
    private TaiLieu taiLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_id")
    private NguoiDung nguoiDung;

    @Column(name = "QTC_loaiQuyen", columnDefinition = "TEXT")
    private String QTC_loaiQuyen;

    @Column(name = "QTC_thoiHan")
    private LocalDateTime QTC_thoiHan;
}