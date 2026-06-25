package com.qltnb.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClientResponse {
    private Long id;
    private String ten;
    private String loai;
    private String cccdMst;
    private String sdt;
    private String diaChi;
    private String email;
    private Long soVuViec;
    private List<CaseSummary> dsVuViec; // Danh sách vụ việc phục vụ API chi tiết khách hàng

    @Data
    public static class CaseSummary {
        private Long id;
        private String ten;
        private String trangThai;

        public CaseSummary(Long id, String ten, String trangThai) {
            this.id = id;
            this.ten = ten;
            this.trangThai = trangThai;
        }
    }
}
