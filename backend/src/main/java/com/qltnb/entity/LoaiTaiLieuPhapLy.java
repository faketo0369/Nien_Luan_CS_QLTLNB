package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "LOAI_TAI_LIEU_PHAP_LY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiTaiLieuPhapLy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer LTLPL_id;

    @Column(name = "LTLPL_ten", length = 150)
    private String LTLPL_ten;

    @Column(name = "LTLPL_moTa", columnDefinition = "TEXT")
    private String LTLPL_moTa;

    @OneToMany(mappedBy = "loaiTaiLieuPhapLy", fetch = FetchType.LAZY)
    private List<TaiLieu> danhSachTaiLieu;
}