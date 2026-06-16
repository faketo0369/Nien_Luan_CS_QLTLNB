package com.qltnb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "VAI_TRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer VT_id;

    @Column(name = "VT_ten", length = 100)
    private String VT_ten;

    @Column(name = "VT_moTa", columnDefinition = "TEXT")
    private String VT_moTa;

    @OneToMany(mappedBy = "vaiTro", fetch = FetchType.LAZY)
    private List<NguoiDung> danhSachNguoiDung;
}