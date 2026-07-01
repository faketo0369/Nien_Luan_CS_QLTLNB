package com.qltnb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "BO_PHAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoPhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer BP_id;

    @Column(name = "BP_ten", length = 150)
    private String BP_ten;

    @Column(name = "BP_moTa", columnDefinition = "TEXT")
    private String BP_moTa;

    @OneToMany(mappedBy = "boPhan", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NguoiDung> danhSachNguoiDung;
}