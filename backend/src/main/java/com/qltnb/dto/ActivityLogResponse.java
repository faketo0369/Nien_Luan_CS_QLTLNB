package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {
    private Long id;
    private Long nguoiDungId;
    private String tenNguoiDung;
    private Long taiLieuId;
    private String tenTaiLieu;
    private String loaiHanhDong; // TAO_MOI, CAP_NHAT, XOA, XEM...
    private String moTa;
    private LocalDateTime timeLog;
}
