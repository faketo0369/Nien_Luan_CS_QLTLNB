package com.qltnb.service;

import com.qltnb.dto.*;
import com.qltnb.entity.*;
import com.qltnb.repository.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final TaiLieuRepository taiLieuRepository;
    private final FileStorageService fileStorageService;
    private final LichSuHoatDongRepository repoLog;
    private final VersionService versionService;
    private final NotificationService notificationService;

    @Transactional
    public DocumentResponse createDocument(DocumentRequest request, MultipartFile file) {
        TaiLieu doc = new TaiLieu();
        mapDtoToEntity(request, doc);
        
        // Xác định loại thư mục lưu trữ dựa trên nghiệp vụ vụ việc
        String loaiFolder = request.getVuViecId() != null ? "ho-so-vu-viec" : "van-ban-luat";
        String relativePath = fileStorageService.storeFile(file, loaiFolder, request.getVuViecId());
        
        doc.setTL_duongDan(relativePath);
        doc.setTL_dinhDang(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        doc.setTL_dungLuong(file.getSize());
        doc.setTL_ngayTao(LocalDateTime.now());
        doc.setTL_daXoa(false);
        doc.setTL_nguoiTao("crawler"); // Mặc định người tạo là crawler hoặc có thể phân bổ từ session

        TaiLieu savedDoc = taiLieuRepository.save(doc);
        writeLog("tao_moi", savedDoc.getTL_id(), "Tạo mới tài liệu: " + savedDoc.getTL_ten());
        notificationService.createNotificationForAll("TAI_LIEU", "Tài liệu mới", "Tài liệu '" + savedDoc.getTL_ten() + "' đã được tải lên hệ thống.");
        return mapEntityToDto(savedDoc);
    }

    @Transactional
    public DocumentResponse updateDocument(Long id, DocumentRequest request) {
        TaiLieu doc = taiLieuRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu cần cập nhật."));
        mapDtoToEntity(request, doc);
        TaiLieu updatedDoc = taiLieuRepository.save(doc);
        writeLog("cap_nhat", updatedDoc.getTL_id(), "Cập nhật thông tin tài liệu");
        notificationService.createNotificationForAll("TAI_LIEU", "Cập nhật tài liệu", "Tài liệu '" + updatedDoc.getTL_ten() + "' đã được cập nhật thông tin.");
        return mapEntityToDto(updatedDoc);
    }

    @Transactional
    public void deleteDocument(Long id) {
        TaiLieu doc = taiLieuRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu cần xóa."));
        
        // Xóa file vật lý
        fileStorageService.deleteFile(doc.getTL_duongDan());
        
        // Kích hoạt soft delete
        doc.setTL_daXoa(true);
        taiLieuRepository.save(doc);
        
        writeLog("xoa", id.intValue(), "Xóa mềm tài liệu khỏi hệ thống");
        notificationService.createNotificationForAll("TAI_LIEU", "Xóa tài liệu", "Tài liệu '" + doc.getTL_ten() + "' đã bị gỡ bỏ khỏi hệ thống.");
    }

    public DocumentResponse getDetail(Long id) {
        TaiLieu doc = taiLieuRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại hoặc đã bị xóa."));
        writeLog("xem_chi_tiet", id.intValue(), "Truy cập xem chi tiết tài liệu");
        return mapEntityToDto(doc);
    }

    public Page<DocumentResponse> getDocuments(DocumentFilterRequest filter) {
        // Xử lý Sort động
        String[] sortParts = filter.getSort().split(",");
        final String sortFieldFinal;
        String sortField = sortParts[0];
        if ("id".equals(sortField)) {
            sortFieldFinal = "TL_id";
        } else if ("ten".equals(sortField)) {
            sortFieldFinal = "TL_ten";
        } else if ("ngayTao".equals(sortField)) {
            sortFieldFinal = "TL_ngayTao";
        } else {
            sortFieldFinal = sortField;
        }
        
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());

        // Xây dựng JPA Specification động
        Specification<TaiLieu> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Mặc định lọc ra những tài liệu chưa bị xóa mềm
            predicates.add(criteriaBuilder.equal(root.get("TL_daXoa"), false));

            if (filter.getTuKhoa() != null && !filter.getTuKhoa().isBlank()) {
                String keyword = "%" + filter.getTuKhoa().trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("TL_ten")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("TL_soHieu")), keyword)
                ));
            }
            if (filter.getDanhMucId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("danhMuc").get("DM_id"), filter.getDanhMucId().intValue()));
            }
            if (filter.getLoaiTaiLieuId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("loaiTaiLieuPhapLy").get("LTLPL_id"), filter.getLoaiTaiLieuId().intValue()));
            }
            if (filter.getBoPhanId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("nguoiDungNguoiTao").get("boPhan").get("BP_id"), filter.getBoPhanId().intValue()));
            }
            if (filter.getVuViecId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("vuViec").get("VV_id"), filter.getVuViecId().intValue()));
            }
            if (filter.getTrangThai() != null && !filter.getTrangThai().isBlank()) {
                String targetStatus = filter.getTrangThai().trim().toLowerCase();
                
                String targetAction = null;
                if ("cho_duyet".equals(targetStatus)) {
                    targetAction = "GUI_DUYET";
                } else if ("da_duyet".equals(targetStatus)) {
                    targetAction = "PHE_DUYET";
                } else if ("tu_choi".equals(targetStatus)) {
                    targetAction = "TU_CHOI";
                }
                
                String oldStatusEnumName = targetStatus.toUpperCase();
                TrangThaiTaiLieu targetEnum = null;
                try {
                    targetEnum = TrangThaiTaiLieu.valueOf(oldStatusEnumName);
                } catch (IllegalArgumentException e) {
                    // Ignore
                }

                List<Predicate> statusPredicates = new ArrayList<>();

                if ("nhap".equals(targetStatus)) {
                    Subquery<Long> subNew = query.subquery(Long.class);
                    Root<DocumentApproval> subRootNew = subNew.from(DocumentApproval.class);
                    subNew.select(criteriaBuilder.count(subRootNew));
                    subNew.where(criteriaBuilder.equal(subRootNew.get("document"), root));

                    Subquery<Long> subOld = query.subquery(Long.class);
                    Root<DuyetTaiLieu> subRootOld = subOld.from(DuyetTaiLieu.class);
                    subOld.select(criteriaBuilder.count(subRootOld));
                    subOld.where(criteriaBuilder.equal(subRootOld.get("taiLieu"), root));

                    statusPredicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(subNew, 0L),
                        criteriaBuilder.equal(subOld, 0L)
                    ));
                    
                    if (targetEnum != null) {
                        Subquery<Long> subOldMaxId = query.subquery(Long.class);
                        Root<DuyetTaiLieu> subRootOldMax = subOldMaxId.from(DuyetTaiLieu.class);
                        subOldMaxId.select(criteriaBuilder.max(subRootOldMax.get("DTL_id")));
                        subOldMaxId.where(criteriaBuilder.equal(subRootOldMax.get("taiLieu"), root));

                        Subquery<TrangThaiTaiLieu> subOldStatus = query.subquery(TrangThaiTaiLieu.class);
                        Root<DuyetTaiLieu> subRootOldStatus = subOldStatus.from(DuyetTaiLieu.class);
                        subOldStatus.select(subRootOldStatus.get("DTL_trangThai"));
                        subOldStatus.where(criteriaBuilder.equal(subRootOldStatus.get("DTL_id"), subOldMaxId));

                        statusPredicates.add(criteriaBuilder.equal(subOldStatus, targetEnum));
                    }
                } else {
                    if (targetAction != null) {
                        Subquery<Long> subNewMaxId = query.subquery(Long.class);
                        Root<DocumentApproval> subRootNewMax = subNewMaxId.from(DocumentApproval.class);
                        subNewMaxId.select(criteriaBuilder.max(subRootNewMax.get("id")));
                        subNewMaxId.where(criteriaBuilder.equal(subRootNewMax.get("document"), root));

                        Subquery<String> subNewAction = query.subquery(String.class);
                        Root<DocumentApproval> subRootNewAction = subNewAction.from(DocumentApproval.class);
                        subNewAction.select(subRootNewAction.get("hanhDong"));
                        subNewAction.where(criteriaBuilder.equal(subRootNewAction.get("id"), subNewMaxId));

                        statusPredicates.add(criteriaBuilder.equal(subNewAction, targetAction));
                    }

                    if (targetEnum != null) {
                        Subquery<Long> subOldMaxId = query.subquery(Long.class);
                        Root<DuyetTaiLieu> subRootOldMax = subOldMaxId.from(DuyetTaiLieu.class);
                        subOldMaxId.select(criteriaBuilder.max(subRootOldMax.get("DTL_id")));
                        subOldMaxId.where(criteriaBuilder.equal(subRootOldMax.get("taiLieu"), root));

                        Subquery<TrangThaiTaiLieu> subOldStatus = query.subquery(TrangThaiTaiLieu.class);
                        Root<DuyetTaiLieu> subRootOldStatus = subOldStatus.from(DuyetTaiLieu.class);
                        subOldStatus.select(subRootOldStatus.get("DTL_trangThai"));
                        subOldStatus.where(criteriaBuilder.equal(subRootOldStatus.get("DTL_id"), subOldMaxId));

                        statusPredicates.add(criteriaBuilder.equal(subOldStatus, targetEnum));
                    }
                }

                predicates.add(criteriaBuilder.or(statusPredicates.toArray(new Predicate[0])));
            }

            // Áp dụng Order By trực tiếp trên CriteriaQuery để bỏ qua cơ chế phân tách thuộc tính (PropertyPath) có dấu gạch dưới (_) của Spring Data
            Class<?> resultType = query.getResultType();
            if (resultType != Long.class && resultType != long.class) {
                if (sortParts[1].equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(root.get(sortFieldFinal)));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get(sortFieldFinal)));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return taiLieuRepository.findAll(spec, pageable).map(this::mapEntityToDto);
    }

    @Transactional
    public DocumentResponse replaceFile(Long id, MultipartFile file) {
        TaiLieu doc = taiLieuRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        
        // Xóa file cũ
        fileStorageService.deleteFile(doc.getTL_duongDan());
        
        // Lưu file mới
        String loaiFolder = doc.getVuViec() != null ? "ho-so-vu-viec" : "van-ban-luat";
        Long vvId = doc.getVuViec() != null ? doc.getVuViec().getVV_id().longValue() : null;
        String newPath = fileStorageService.storeFile(file, loaiFolder, vvId);
        
        doc.setTL_duongDan(newPath);
        doc.setTL_dinhDang(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        doc.setTL_dungLuong(file.getSize());
        
        // Tự động tạo phiên bản mới
        versionService.createVersion(doc, file);
        
        TaiLieu updated = taiLieuRepository.save(doc);
        writeLog("cap_nhat", id.intValue(), "Thay đổi tập tin đính kèm của tài liệu và tạo phiên bản lịch sử");
        return mapEntityToDto(updated);
    }

    public byte[] downloadFile(Long id) {
        TaiLieu doc = taiLieuRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        return fileStorageService.readFile(doc.getTL_duongDan());
    }

    @Transactional
    public void submitForApproval(Long id) {
        TaiLieu doc = taiLieuRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        writeLog("gui_phe_duyet", id.intValue(), "Gửi phê duyệt tài liệu");
    }

    private void writeLog(String hanhDong, Integer docId, String moTa) {
        LichSuHoatDong log = new LichSuHoatDong();
        log.setNguoiDungId(1L);
        log.setTaiLieuId(docId != null ? docId.longValue() : null);
        log.setLoaiHanhDong(hanhDong.toUpperCase());
        log.setMoTa(moTa);
        log.setTimeLog(LocalDateTime.now());
        repoLog.save(log);
    }

    private void mapDtoToEntity(DocumentRequest dto, TaiLieu entity) {
        entity.setTL_ten(dto.getTen());
        entity.setTL_soHieu(dto.getSoHieu());
        entity.setTL_baoMat(dto.getBaoMat() != null ? (dto.getBaoMat() ? "MAT" : "NOI_BO") : "NOI_BO");
        
        if (dto.getDanhMucId() != null) {
            DanhMuc dm = new DanhMuc();
            dm.setDM_id(dto.getDanhMucId().intValue());
            entity.setDanhMuc(dm);
        } else {
            entity.setDanhMuc(null);
        }
        
        if (dto.getLoaiTaiLieuId() != null) {
            LoaiTaiLieuPhapLy ltl = new LoaiTaiLieuPhapLy();
            ltl.setLTLPL_id(dto.getLoaiTaiLieuId().intValue());
            entity.setLoaiTaiLieuPhapLy(ltl);
        } else {
            entity.setLoaiTaiLieuPhapLy(null);
        }
        
        if (dto.getVuViecId() != null) {
            VuViec vv = new VuViec();
            vv.setVV_id(dto.getVuViecId().intValue());
            entity.setVuViec(vv);
        } else {
            entity.setVuViec(null);
        }
    }

    private DocumentResponse mapEntityToDto(TaiLieu entity) {
        DocumentResponse res = new DocumentResponse();
        res.setId(entity.getTL_id() != null ? entity.getTL_id().longValue() : null);
        res.setTen(entity.getTL_ten());
        res.setSoHieu(entity.getTL_soHieu());
        res.setMoTa(null); // Cột mô tả không tồn tại trong database thực tế
        res.setDinhDang(entity.getTL_dinhDang());
        res.setDungLuong(entity.getTL_dungLuong());
        res.setNgayTao(entity.getTL_ngayTao());
        res.setNgayCapNhat(entity.getTL_ngayTao()); // Fallback bằng ngày tạo
        res.setBaoMat("MAT".equalsIgnoreCase(entity.getTL_baoMat()));
        
        // Tính trạng thái duyệt tài liệu từ lịch sử mới hoặc cũ
        String status = "nhap";
        if (entity.getApprovals() != null && !entity.getApprovals().isEmpty()) {
            String act = entity.getApprovals().get(entity.getApprovals().size() - 1).getHanhDong();
            if ("GUI_DUYET".equalsIgnoreCase(act)) {
                status = "cho_duyet";
            } else if ("PHE_DUYET".equalsIgnoreCase(act)) {
                status = "da_duyet";
            } else if ("TU_CHOI".equalsIgnoreCase(act)) {
                status = "tu_choi";
            }
        } else if (entity.getDanhSachDuyet() != null && !entity.getDanhSachDuyet().isEmpty()) {
            status = entity.getDanhSachDuyet().get(entity.getDanhSachDuyet().size() - 1).getDTL_trangThai().name().toLowerCase();
        }
        res.setTrangThai(status);
        
        if (entity.getDanhMuc() != null) {
            res.setDanhMuc(new DocumentResponse.RelationSummary(
                entity.getDanhMuc().getDM_id() != null ? entity.getDanhMuc().getDM_id().longValue() : null,
                entity.getDanhMuc().getDM_ten()
            ));
        }
        if (entity.getLoaiTaiLieuPhapLy() != null) {
            res.setLoaiTaiLieu(new DocumentResponse.RelationSummary(
                entity.getLoaiTaiLieuPhapLy().getLTLPL_id() != null ? entity.getLoaiTaiLieuPhapLy().getLTLPL_id().longValue() : null,
                entity.getLoaiTaiLieuPhapLy().getLTLPL_ten()
            ));
        }
        if (entity.getVuViec() != null) {
            res.setVuViec(new DocumentResponse.RelationSummary(
                entity.getVuViec().getVV_id() != null ? entity.getVuViec().getVV_id().longValue() : null,
                entity.getVuViec().getVV_ten()
            ));
        }
        if (entity.getNguoiDungNguoiTao() != null && entity.getNguoiDungNguoiTao().getBoPhan() != null) {
            BoPhan bp = entity.getNguoiDungNguoiTao().getBoPhan();
            res.setBoPhan(new DocumentResponse.RelationSummary(
                bp.getBP_id() != null ? bp.getBP_id().longValue() : null,
                bp.getBP_ten()
            ));
        }
        
        return res;
    }

    public List<DocumentResponse> getByVuViecId(Long vvId) {
        if (vvId == null) {
            return new ArrayList<>();
        }
        List<TaiLieu> documents = taiLieuRepository.findByVuViecId(vvId.intValue());
        List<DocumentResponse> result = new ArrayList<>();
        for (TaiLieu doc : documents) {
            result.add(mapEntityToDto(doc));
        }
        return result;
    }
}
