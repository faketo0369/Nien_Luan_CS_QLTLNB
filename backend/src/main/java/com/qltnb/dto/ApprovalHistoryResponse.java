package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApprovalHistoryResponse {
    private Long id;
    private String hanhDong;
    private String ghiChu;
    private LocalDateTime timeApprove;
    private String tenNguoiDuyet;
}
