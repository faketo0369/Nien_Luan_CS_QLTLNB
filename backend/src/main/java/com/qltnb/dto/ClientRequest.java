package com.qltnb.dto;

import lombok.Data;

@Data
public class ClientRequest {
    private String ten;
    private String loai; // CA_NHAN hoặc TO_CHUC
    private String cccdMst;
    private String sdt;
    private String diaChi;
    private String email;
}
