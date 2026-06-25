package com.qltnb.repository;
import com.qltnb.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    @Query("SELECT n FROM NguoiDung n WHERE n.ND_taiKhoan = :taiKhoan")
    Optional<NguoiDung> findByND_taiKhoan(@Param("taiKhoan") String taiKhoan);

    @Query("SELECT n FROM NguoiDung n WHERE n.ND_taiKhoan = :taiKhoan")
    Optional<NguoiDung> findByTaiKhoan(@Param("taiKhoan") String taiKhoan);

    @Query("SELECT n FROM NguoiDung n LEFT JOIN FETCH n.vaiTro LEFT JOIN FETCH n.boPhan WHERE n.ND_taiKhoan = :taiKhoan")
    Optional<NguoiDung> findByTaiKhoanWithVaiTro(@Param("taiKhoan") String taiKhoan);

    @Query("SELECT n FROM NguoiDung n WHERE n.boPhan.BP_id = :boPhanId AND n.vaiTro.VT_ten = 'TRUONG_PHONG'")
    java.util.List<NguoiDung> findTruongPhongByBoPhanId(@Param("boPhanId") Integer boPhanId);
}