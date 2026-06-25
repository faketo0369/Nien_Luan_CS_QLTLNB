package com.qltnb.repository;

import com.qltnb.entity.LichSuHoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuHoatDongRepository extends JpaRepository<LichSuHoatDong, Long>, JpaSpecificationExecutor<LichSuHoatDong> {
}