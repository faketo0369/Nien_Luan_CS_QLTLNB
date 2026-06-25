package com.qltnb.dto;

import lombok.Data;

@Data
public class DocumentFilterRequest {
    private String tuKhoa;
    private Long danhMucId;
    private Long loaiTaiLieuId;
    private Long boPhanId;
    private String trangThai;
    private Long vuViecId;
    private int page = 0;
    private int size = 10;
    private String sort = "id,desc";
}
