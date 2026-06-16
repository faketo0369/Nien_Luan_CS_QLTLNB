package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "THONG_BAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer TB_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_id")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TL_id")
    private TaiLieu taiLieu;

    @Column(name = "TB_tieuDe", columnDefinition = "TEXT")
    private String TB_tieuDe;

    @Column(name = "TB_noiDung", columnDefinition = "TEXT")
    private String TB_noiDung;

    @Column(name = "TB_trangThaiDoc")
    private Boolean TB_trangThaiDoc;
}