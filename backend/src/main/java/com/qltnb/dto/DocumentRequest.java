package com.qltnb.dto;

import lombok.Data;

@Data
public class DocumentRequest {
    private String ten;
    private String soHieu;
    private String moTa;
    private Long danhMucId;
    private Long loaiTaiLieuId;
    private Long boPhanId;
    private Long vuViecId;
    private Boolean baoMat;
}
