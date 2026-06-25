package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PermissionResponse {
    private Long id;
    private String loaiQuyen;
    private LocalDate ngayHetHan;
    private SummaryInfo nguoiDung;
    private SummaryInfo boPhan;

    @Data
    public static class SummaryInfo {
        private Long id;
        private String ten;
        public SummaryInfo(Long id, String ten) { this.id = id; this.ten = ten; }
    }
}
