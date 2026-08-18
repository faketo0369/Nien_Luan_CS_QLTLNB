package com.qltnb.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
public class VersionResponse {
    private Long id;
    private String soPhienBan;
    private RelationSummary nguoiCapNhat;
    private LocalDateTime timeUpdate;
    private String ghiChu;
    private Long kichCo;
    private String dinhDang;

    private String tenFileGoc;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelationSummary {
        private Long id;
        private String ten;
    }
}
