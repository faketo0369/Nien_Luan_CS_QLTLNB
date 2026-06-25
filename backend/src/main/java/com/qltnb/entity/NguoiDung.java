package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "TAI_KHOAN_NGUOI_DUNG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ND_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BP_id")
    private BoPhan boPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VT_id")
    private VaiTro vaiTro;

    @Column(name = "ND_hoTen", columnDefinition = "TEXT")
    private String ND_hoTen;

    @Column(name = "ND_taiKhoan", length = 100)
    private String ND_taiKhoan;

    @Column(name = "ND_matKhau", columnDefinition = "TEXT")
    private String ND_matKhau;

    @Column(name = "ND_email", length = 150)
    private String ND_email;

    @Column(name = "ND_soLanSai")
    private Integer ND_soLanSai;

    @Column(name = "ND_trangThaiTK")
    private Boolean ND_trangThaiTK;

    @Column(name = "ND_chuyenMon", columnDefinition = "TEXT")
    private String ND_chuyenMon;

    @Column(name = "ND_chungChi", columnDefinition = "TEXT")
    private String ND_chungChi;

    @OneToMany(mappedBy = "nguoiPhuTrach", fetch = FetchType.LAZY)
    private List<VuViec> danhSachVuViec;

    @OneToMany(mappedBy = "nguoiDuyet", fetch = FetchType.LAZY)
    private List<DuyetTaiLieu> danhSachDuyetTaiLieu;



    @OneToMany(mappedBy = "nguoiNhan", fetch = FetchType.LAZY)
    private List<ThongBao> danhSachThongBao;

    public Long getId() {
        return ND_id != null ? ND_id.longValue() : null;
    }

    public String getTaiKhoan() {
        return this.ND_taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.ND_taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return this.ND_matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.ND_matKhau = matKhau;
    }

    public String getHoTen() {
        return this.ND_hoTen;
    }

    public Integer getSoLanSai() {
        return this.ND_soLanSai;
    }

    public void setSoLanSai(Integer soLanSai) {
        this.ND_soLanSai = soLanSai;
    }

    public String getTrangThai() {
        return (ND_trangThaiTK == null || ND_trangThaiTK) ? "HOAT_DONG" : "KHOA";
    }

    public void setTrangThai(String trangThai) {
        if ("KHOA".equalsIgnoreCase(trangThai)) {
            this.ND_trangThaiTK = false;
        } else {
            this.ND_trangThaiTK = true;
        }
    }
}