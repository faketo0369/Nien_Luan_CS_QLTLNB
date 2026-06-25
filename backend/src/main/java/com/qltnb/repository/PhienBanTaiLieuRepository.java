package com.qltnb.repository;
import com.qltnb.entity.PhienBanTaiLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PhienBanTaiLieuRepository extends JpaRepository<PhienBanTaiLieu, Integer> {
    @Query("SELECT p FROM PhienBanTaiLieu p WHERE p.taiLieu.TL_id = :tlId ORDER BY p.PBTL_timeUpdate DESC")
    List<PhienBanTaiLieu> findByDocumentId(@Param("tlId") Integer tlId);

    @Query("SELECT COUNT(p) FROM PhienBanTaiLieu p WHERE p.taiLieu.TL_id = :tlId")
    long countByDocumentId(@Param("tlId") Integer tlId);
}