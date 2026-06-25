package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentResponse {
    private Long id;
    private String ten;
    private String soHieu;
    private String moTa;
    private String dinhDang;
    private Long dungLuong;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private String trangThai;
    private Boolean baoMat;
    
    private RelationSummary danhMuc;
    private RelationSummary loaiTaiLieu;
    private RelationSummary boPhan;
    private RelationSummary vuViec;

    @Data
    public static class RelationSummary {
        private Long id;
        private String ten;
        
        public RelationSummary(Long id, String ten) {
            this.id = id;
            this.ten = ten;
        }
    }
}
