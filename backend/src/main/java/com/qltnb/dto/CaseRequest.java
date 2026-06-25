package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CaseRequest {
    private String ten;
    private String loai; // HON_NHAN, DAT_DAI, LAO_DONG, HOP_DONG, DAN_SU_KHAC
    private String trangThai;
    private Long khachHangId;
    private Long nguoiPhuTrachId;
    private LocalDate ngayMo;
    private LocalDate ngayDong;
    private String ghiChu;
}
