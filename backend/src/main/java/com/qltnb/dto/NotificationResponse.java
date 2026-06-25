package com.qltnb.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String loai; // YEU_CAU_DUYET, DA_DUYET, TU_CHOI, DUOC_CAP_QUYEN, HET_HAN_QUYEN
    private String tieuDe;
    private String noiDung;
    private LocalDateTime ngayTao;
    private Boolean daDoc;
}
