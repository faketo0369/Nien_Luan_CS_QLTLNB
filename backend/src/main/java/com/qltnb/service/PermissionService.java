package com.qltnb.service;

import com.qltnb.dto.PermissionRequest;
import com.qltnb.dto.PermissionResponse;
import com.qltnb.entity.TaiLieu;
import com.qltnb.entity.DocumentPermission;
import com.qltnb.entity.NguoiDung;
import com.qltnb.entity.BoPhan;
import com.qltnb.repository.TaiLieuRepository;
import com.qltnb.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final TaiLieuRepository taiLieuRepository;

    @Transactional
    public PermissionResponse grantPermission(Long documentId, PermissionRequest request) {
        if (request.getNguoiDungId() == null && request.getBoPhanId() == null) {
            throw new RuntimeException("Phải chỉ định ít nhất một Người dùng hoặc một Bộ phận để cấp quyền.");
        }

        TaiLieu doc = taiLieuRepository.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));

        DocumentPermission perm = new DocumentPermission();
        perm.setDocument(doc);
        perm.setLoaiQuyen(request.getLoaiQuyen() != null ? request.getLoaiQuyen().toUpperCase() : null);
        perm.setNgayHetHan(request.getNgayHetHan());

        if (request.getNguoiDungId() != null) {
            NguoiDung nd = new NguoiDung();
            nd.setND_id(request.getNguoiDungId().intValue());
            perm.setNguoiDung(nd);
        }
        if (request.getBoPhanId() != null) {
            BoPhan bp = new BoPhan();
            bp.setBP_id(request.getBoPhanId().intValue());
            perm.setBoPhan(bp);
        }

        return mapEntityToDto(permissionRepository.save(perm));
    }

    public List<PermissionResponse> getPermissions(Long documentId) {
        return permissionRepository.findByDocumentId(documentId.intValue()).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Quyền thu hồi không tồn tại.");
        }
        permissionRepository.deleteById(id);
    }

    private PermissionResponse mapEntityToDto(DocumentPermission entity) {
        PermissionResponse res = new PermissionResponse();
        res.setId(entity.getId());
        res.setLoaiQuyen(entity.getLoaiQuyen());
        res.setNgayHetHan(entity.getNgayHetHan());
        
        if (entity.getNguoiDung() != null) {
            res.setNguoiDung(new PermissionResponse.SummaryInfo(
                entity.getNguoiDung().getND_id() != null ? entity.getNguoiDung().getND_id().longValue() : null, 
                entity.getNguoiDung().getND_hoTen()
            ));
        }
        if (entity.getBoPhan() != null) {
            res.setBoPhan(new PermissionResponse.SummaryInfo(
                entity.getBoPhan().getBP_id() != null ? entity.getBoPhan().getBP_id().longValue() : null, 
                entity.getBoPhan().getBP_ten()
            ));
        }
        return res;
    }
}
