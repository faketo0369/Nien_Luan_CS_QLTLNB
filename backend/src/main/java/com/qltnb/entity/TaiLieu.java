package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TAI_LIEU")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer TL_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DM_id")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LTLPL_id")
    private LoaiTaiLieuPhapLy loaiTaiLieuPhapLy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VV_id")
    private VuViec vuViec;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ND_nguoiTao_id")
    private NguoiDung nguoiDungNguoiTao;

    @Column(name = "TL_ten", columnDefinition = "TEXT")
    private String TL_ten;

    @Column(name = "TL_duongDan", columnDefinition = "TEXT")
    private String TL_duongDan;

    @Column(name = "TL_dinhDang", columnDefinition = "TEXT")
    private String TL_dinhDang;

    @Column(name = "TL_dungLuong")
    private Long TL_dungLuong;

    @Column(name = "TL_nguoiTao", columnDefinition = "TEXT")
    private String TL_nguoiTao;

    @Column(name = "TL_ngayTao")
    private LocalDateTime TL_ngayTao;

    @Column(name = "TL_ngayBanHanh")
    private LocalDate TL_ngayBanHanh;

    @Column(name = "TL_daXoa")
    private Boolean TL_daXoa;

    @Column(name = "TL_baoMat", columnDefinition = "TEXT")
    private String TL_baoMat;

    @Column(name = "TL_ngayHetHan")
    private LocalDateTime TL_ngayHetHan;

    @Column(name = "TL_soHieu", length = 100)
    private String TL_soHieu;

    @OneToMany(mappedBy = "taiLieu", fetch = FetchType.LAZY)
    private List<DuyetTaiLieu> danhSachDuyet;

    @OneToMany(mappedBy = "taiLieu", fetch = FetchType.LAZY)
    private List<PhienBanTaiLieu> danhSachPhiênBan;

    @OneToMany(mappedBy = "taiLieu", fetch = FetchType.LAZY)
    private List<QuyenTruyCap> danhSachQuyenTruyCap;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    private List<DocumentApproval> approvals;
}