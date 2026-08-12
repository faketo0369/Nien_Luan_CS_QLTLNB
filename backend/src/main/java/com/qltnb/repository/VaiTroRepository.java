package com.qltnb.repository;
import com.qltnb.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {
    @Query("SELECT v FROM VaiTro v WHERE v.VT_ten = :ten")
    Optional<VaiTro> findByVT_ten(@Param("ten") String ten);
}