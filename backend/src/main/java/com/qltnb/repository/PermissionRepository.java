package com.qltnb.repository;

import com.qltnb.entity.DocumentPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PermissionRepository extends JpaRepository<DocumentPermission, Long> {
    @Query("SELECT p FROM DocumentPermission p WHERE p.document.TL_id = :documentId")
    List<DocumentPermission> findByDocumentId(@Param("documentId") Integer documentId);
}
