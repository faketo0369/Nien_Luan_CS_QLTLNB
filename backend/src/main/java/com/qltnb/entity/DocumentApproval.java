package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "duyet_tai_lieu_moi")
@Data
public class DocumentApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_lieu_id", nullable = false)
    private TaiLieu document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet_id")
    private NguoiDung nguoiDuyet;

    @Column(name = "hanh_dong", nullable = false)
    private String hanhDong; // GUI_DUYET, PHE_DUYET, TU_CHOI

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "time_approve", nullable = false)
    private LocalDateTime timeApprove;
}
