package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_hoat_dong_moi")
@Data
public class LichSuHoatDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nguoi_dung_id")
    private Long nguoiDungId;

    @Column(name = "tai_lieu_id")
    private Long taiLieuId;

    @Column(name = "loai_hanh_dong", nullable = false)
    private String loaiHanhDong; // TAO_MOI, CAP_NHAT, XOA, XEM, UPLOAD, DOWNLOAD, GUI_DUYET...

    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "time_log", nullable = false)
    private LocalDateTime timeLog;
}