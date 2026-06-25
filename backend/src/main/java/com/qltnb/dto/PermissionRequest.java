package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PermissionRequest {
    private Long nguoiDungId;
    private Long boPhanId;
    private String loaiQuyen; // XEM, TAI, SUA, XOA
    private LocalDate ngayHetHan;
}
