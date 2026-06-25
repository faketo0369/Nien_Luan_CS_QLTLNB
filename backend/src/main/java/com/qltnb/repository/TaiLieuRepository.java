package com.qltnb.repository;
import com.qltnb.entity.TaiLieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface TaiLieuRepository extends JpaRepository<TaiLieu, Integer>, JpaSpecificationExecutor<TaiLieu> {
    @Query("SELECT t FROM TaiLieu t WHERE t.TL_duongDan = :duongDan")
    Optional<TaiLieu> findByTL_duongDan(@Param("duongDan") String duongDan);

    @Query("SELECT t FROM TaiLieu t WHERE t.danhMuc.DM_id = :dmId AND t.TL_ten LIKE %:tuKhoa% ORDER BY t.TL_ngayTao DESC")
    Page<TaiLieu> findByDanhMucIdAndTlTenContaining(@Param("dmId") Integer dmId, @Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("SELECT t FROM TaiLieu t WHERE t.danhMuc.DM_id = :dmId ORDER BY t.TL_ngayTao DESC")
    Page<TaiLieu> findByDanhMucId(@Param("dmId") Integer dmId, Pageable pageable);

    @Query("SELECT t FROM TaiLieu t WHERE t.TL_ten LIKE %:tuKhoa% ORDER BY t.TL_ngayTao DESC")
    Page<TaiLieu> findByTlTenContaining(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("SELECT t FROM TaiLieu t ORDER BY t.TL_ngayTao DESC")
    Page<TaiLieu> findAllOrderByTL_ngayTaoDesc(Pageable pageable);

    @Query("SELECT COUNT(t) FROM TaiLieu t WHERE t.vuViec.VV_id = :vvId AND t.TL_daXoa = false")
    long countByVuViecId(@Param("vvId") Integer vvId);

    @Query("SELECT t FROM TaiLieu t WHERE t.vuViec.VV_id = :vvId AND t.TL_daXoa = false")
    java.util.List<TaiLieu> findByVuViecId(@Param("vvId") Integer vvId);
}