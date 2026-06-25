package com.qltnb.repository;

import com.qltnb.entity.ThongBao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    @Query("SELECT t FROM ThongBao t WHERE t.nguoiNhan.ND_id = :userId")
    List<ThongBao> findByNguoiNhanId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM ThongBao t WHERE t.nguoiNhan.ND_id = :userId AND t.daDoc = false")
    long countByNguoiNhanIdAndDaDocFalse(@Param("userId") Integer userId);
}