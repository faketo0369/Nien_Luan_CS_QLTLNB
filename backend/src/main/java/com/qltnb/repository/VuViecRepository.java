package com.qltnb.repository;
import com.qltnb.entity.VuViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VuViecRepository extends JpaRepository<VuViec, Integer>, JpaSpecificationExecutor<VuViec> {
    @Query("SELECT COUNT(v) FROM VuViec v WHERE v.khachHang.KH_id = :khId")
    long countByKhachHangId(@Param("khId") Integer khId);

    @Query("SELECT v FROM VuViec v WHERE v.khachHang.KH_id = :khId")
    List<VuViec> findByKhachHangId(@Param("khId") Integer khId);
}