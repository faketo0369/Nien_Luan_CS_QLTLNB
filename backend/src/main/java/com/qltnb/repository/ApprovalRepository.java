package com.qltnb.repository;

import com.qltnb.entity.DocumentApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ApprovalRepository extends JpaRepository<DocumentApproval, Long> {
    @Query("SELECT a FROM DocumentApproval a WHERE a.document.TL_id = :documentId ORDER BY a.timeApprove DESC")
    List<DocumentApproval> findByDocumentIdOrderByTimeApproveDesc(@Param("documentId") Integer documentId);
}
