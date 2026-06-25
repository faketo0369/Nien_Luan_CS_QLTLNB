package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CaseResponse {
    private Long id;
    private String ten;
    private String loai;
    private String trangThai;
    private LocalDate ngayMo;
    private LocalDate ngayDong;
    private String ghiChu;
    
    private ClientSummary khachHang;
    private UserSummary nguoiPhuTrach;
    private Long soTaiLieu;
    private List<DocumentResponse> dsTaiLieu; // Danh sách tài liệu phục vụ API chi tiết vụ việc

    @Data
    public static class ClientSummary {
        private Long id;
        private String ten;

        public ClientSummary(Long id, String ten) {
            this.id = id;
            this.ten = ten;
        }
    }

    @Data
    public static class UserSummary {
        private Long id;
        private String hoTen;

        public UserSummary(Long id, String hoTen) {
            this.id = id;
            this.hoTen = hoTen;
        }
    }
}
