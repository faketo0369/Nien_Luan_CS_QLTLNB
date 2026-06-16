package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "DANH_MUC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer DM_id;

    @Column(name = "DM_ten", length = 150)
    private String DM_ten;

    @OneToMany(mappedBy = "danhMuc", fetch = FetchType.LAZY)
    private List<TaiLieu> danhSachTaiLieu;
}