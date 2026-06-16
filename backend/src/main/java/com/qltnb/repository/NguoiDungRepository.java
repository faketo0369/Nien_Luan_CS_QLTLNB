package com.qltnb.repository;
import com.qltnb.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    @Query("SELECT n FROM NguoiDung n WHERE n.ND_taiKhoan = :taiKhoan")
    Optional<NguoiDung> findByND_taiKhoan(@Param("taiKhoan") String taiKhoan);
}