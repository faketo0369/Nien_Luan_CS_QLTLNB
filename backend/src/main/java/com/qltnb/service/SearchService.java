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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TaiLieuRepository documentRepository;
    private final KhachHangRepository khachHangRepository;
    private final VuViecRepository vuViecRepository;
    private final ApprovalRepository approvalRepository;

    public Page<DocumentResponse> searchDocuments(String q, Long danhMucId, Long loaiTaiLieuId, Long boPhanId, 
                                                 Long vuViecId, String trangThai, Boolean baoMat, String dinhDang, 
                                                 LocalDate tuNgay, LocalDate denNgay, Pageable pageable) {
        Specification<TaiLieu> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Mặc định lọc ra những tài liệu chưa bị xóa mềm
            predicates.add(cb.equal(root.get("TL_daXoa"), false));

            // Tìm kiếm từ khóa tổng quát trong tên văn bản hoặc số hiệu
            if (q != null && !q.isBlank()) {
                String keyword = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("TL_ten")), keyword),
                        cb.like(cb.lower(root.get("TL_soHieu")), keyword)
                ));
            }
            
            // Các bộ lọc nâng cao chính xác
            if (danhMucId != null) {
                predicates.add(cb.equal(root.get("danhMuc").get("DM_id"), danhMucId.intValue()));
            }
            if (loaiTaiLieuId != null) {
                predicates.add(cb.equal(root.get("loaiTaiLieuPhapLy").get("LTLPL_id"), loaiTaiLieuId.intValue()));
            }
            if (boPhanId != null) {
                predicates.add(cb.equal(root.get("nguoiDungNguoiTao").get("boPhan").get("BP_id"), boPhanId.intValue()));
            }
            if (vuViecId != null) {
                predicates.add(cb.equal(root.get("vuViec").get("VV_id"), vuViecId.intValue()));
            }
            if (baoMat != null) {
                predicates.add(cb.equal(root.get("TL_baoMat"), baoMat ? "MAT" : "NOI_BO"));
            }
            if (dinhDang != null && !dinhDang.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("TL_dinhDang")), dinhDang.trim().toLowerCase()));
            }

            // Lọc theo mốc khoảng thời gian ngày tạo văn bản
            if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("TL_ngayTao"), LocalDateTime.of(tuNgay, LocalTime.MIN)));
            }
            if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("TL_ngayTao"), LocalDateTime.of(denNgay, LocalTime.MAX)));
            }

            // Lọc theo trạng thái duyệt tài liệu từ bảng duyet_tai_lieu_moi
            if (trangThai != null && !trangThai.isBlank()) {
                String statusTrim = trangThai.trim().toLowerCase();
                if ("nhap".equals(statusTrim)) {
                    // Trạng thái nháp: Không có bản ghi duyệt nào trong duyet_tai_lieu_moi
                    Subquery<Long> sub = query.subquery(Long.class);
                    Root<DocumentApproval> subRoot = sub.from(DocumentApproval.class);
                    sub.select(cb.count(subRoot));
                    sub.where(cb.equal(subRoot.get("document"), root));
                    predicates.add(cb.equal(sub, 0L));
                } else {
                    String targetAction = null;
                    if ("cho_duyet".equals(statusTrim)) {
                        targetAction = "GUI_DUYET";
                    } else if ("da_duyet".equals(statusTrim)) {
                        targetAction = "PHE_DUYET";
                    } else if ("tu_choi".equals(statusTrim)) {
                        targetAction = "TU_CHOI";
                    }
                    
                    if (targetAction != null) {
                        // Trạng thái khác nháp: tìm bản ghi duyệt mới nhất có hành động tương ứng
                        Subquery<Long> subMaxId = query.subquery(Long.class);
                        Root<DocumentApproval> subRootMax = subMaxId.from(DocumentApproval.class);
                        subMaxId.select(cb.max(subRootMax.get("id")));
                        subMaxId.where(cb.equal(subRootMax.get("document"), root));

                        Subquery<String> subAction = query.subquery(String.class);
                        Root<DocumentApproval> subRootAction = subAction.from(DocumentApproval.class);
                        subAction.select(subRootAction.get("hanhDong"));
                        subAction.where(cb.equal(subRootAction.get("id"), subMaxId));
                        
                        predicates.add(cb.equal(subAction, targetAction));
                    }
                }
            }

            // Apply Sort
            applySortToQuery(root, query, cb, pageable.getSort(), "TL_ngayTao");

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return documentRepository.findAll(spec, pageableWithoutSort).map(this::mapDocumentToDto);
    }

    public Page<ClientResponse> searchClients(String q, String loai, Pageable pageable) {
        Specification<KhachHang> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String keyword = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("KH_ten")), keyword),
                        cb.like(cb.lower(root.get("KH_CCCD_MST")), keyword),
                        cb.like(cb.lower(root.get("KH_email")), keyword)
                ));
            }
            if (loai != null && !loai.isBlank()) {
                try {
                    predicates.add(cb.equal(root.get("KH_loai"), LoaiKhachHang.valueOf(loai.trim().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    predicates.add(cb.disjunction());
                }
            }

            // Apply Sort
            applySortToQuery(root, query, cb, pageable.getSort(), "KH_id");

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return khachHangRepository.findAll(spec, pageableWithoutSort).map(kh -> {
            ClientResponse res = new ClientResponse();
            res.setId(kh.getKH_id() != null ? kh.getKH_id().longValue() : null);
            res.setTen(kh.getKH_ten());
            res.setLoai(kh.getKH_loai() != null ? kh.getKH_loai().name() : null);
            res.setCccdMst(kh.getKH_CCCD_MST());
            res.setSdt(kh.getKH_sdt());
            res.setDiaChi(kh.getKH_diaChi());
            res.setEmail(kh.getKH_email());
            res.setSoVuViec(kh.getKH_id() != null ? vuViecRepository.countByKhachHangId(kh.getKH_id()) : 0L); 
            return res;
        });
    }

    public Page<CaseResponse> searchCases(String q, String loai, String trangThai, Long nguoiPhuTrachId, Pageable pageable) {
        Specification<VuViec> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String keyword = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("VV_ten")), keyword),
                        cb.like(cb.lower(root.get("VV_ghiChu")), keyword)
                ));
            }
            if (loai != null && !loai.isBlank()) {
                predicates.add(cb.equal(root.get("VV_loai"), loai.trim()));
            }
            if (trangThai != null && !trangThai.isBlank()) {
                predicates.add(cb.equal(root.get("VV_trangThai"), trangThai.trim()));
            }
            if (nguoiPhuTrachId != null) {
                predicates.add(cb.equal(root.get("nguoiPhuTrach").get("ND_id"), nguoiPhuTrachId.intValue()));
            }

            // Apply Sort
            applySortToQuery(root, query, cb, pageable.getSort(), "VV_id");

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return vuViecRepository.findAll(spec, pageableWithoutSort).map(vv -> {
            CaseResponse res = new CaseResponse();
            res.setId(vv.getVV_id() != null ? vv.getVV_id().longValue() : null);
            res.setTen(vv.getVV_ten());
            res.setLoai(vv.getVV_loai());
            res.setTrangThai(vv.getVV_trangThai());
            res.setNgayMo(vv.getVV_ngayMo() != null ? vv.getVV_ngayMo().toLocalDate() : null);
            res.setNgayDong(vv.getVV_ngayDong() != null ? vv.getVV_ngayDong().toLocalDate() : null);
            res.setGhiChu(vv.getVV_ghiChu());
            if (vv.getKhachHang() != null) {
                res.setKhachHang(new CaseResponse.ClientSummary(
                    vv.getKhachHang().getKH_id() != null ? vv.getKhachHang().getKH_id().longValue() : null, 
                    vv.getKhachHang().getKH_ten()
                ));
            }
            if (vv.getNguoiPhuTrach() != null) {
                res.setNguoiPhuTrach(new CaseResponse.UserSummary(
                    vv.getNguoiPhuTrach().getND_id() != null ? vv.getNguoiPhuTrach().getND_id().longValue() : null, 
                    vv.getNguoiPhuTrach().getND_hoTen()
                ));
            }
            res.setSoTaiLieu(vv.getVV_id() != null ? documentRepository.countByVuViecId(vv.getVV_id()) : 0L);
            return res;
        });
    }

    public GlobalSearchResponse globalSearch(String q) {
        Pageable topThree = PageRequest.of(0, 3);

        Page<DocumentResponse> docs = searchDocuments(q, null, null, null, null, null, null, null, null, null, topThree);
        Page<ClientResponse> clients = searchClients(q, null, topThree);
        Page<CaseResponse> cases = searchCases(q, null, null, null, topThree);

        GlobalSearchResponse response = new GlobalSearchResponse();
        response.setDocuments(new GlobalSearchResponse.SearchResultWrapper<>(docs.getContent(), docs.getTotalElements()));
        response.setClients(new GlobalSearchResponse.SearchResultWrapper<>(clients.getContent(), clients.getTotalElements()));
        response.setCases(new GlobalSearchResponse.SearchResultWrapper<>(cases.getContent(), cases.getTotalElements()));

        return response;
    }

    private void applySortToQuery(Root<?> root, jakarta.persistence.criteria.CriteriaQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder cb, Sort sort, String defaultField) {
        Class<?> resultType = query.getResultType();
        // Không áp dụng Order By đối với câu truy vấn Count (trả về Long)
        if (resultType == Long.class || resultType == long.class) {
            return;
        }
        
        List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (order.isDescending()) {
                    orders.add(cb.desc(root.get(property)));
                } else {
                    orders.add(cb.asc(root.get(property)));
                }
            }
        } else if (defaultField != null) {
            orders.add(cb.desc(root.get(defaultField)));
        }
        
        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }
    }

    private DocumentResponse mapDocumentToDto(TaiLieu entity) {
        DocumentResponse res = new DocumentResponse();
        res.setId(entity.getTL_id() != null ? entity.getTL_id().longValue() : null);
        res.setTen(entity.getTL_ten());
        res.setSoHieu(entity.getTL_soHieu());
        res.setMoTa(null); // Không có cột mô tả trong bảng TAI_LIEU thực tế
        res.setDinhDang(entity.getTL_dinhDang());
        res.setDungLuong(entity.getTL_dungLuong());
        res.setNgayTao(entity.getTL_ngayTao());
        res.setNgayCapNhat(entity.getTL_ngayTao()); // Fallback bằng ngày tạo
        res.setBaoMat("MAT".equalsIgnoreCase(entity.getTL_baoMat()));

        String status = "nhap";
        List<DocumentApproval> approvals = approvalRepository.findByDocumentIdOrderByTimeApproveDesc(entity.getTL_id());
        if (approvals != null && !approvals.isEmpty()) {
            String action = approvals.get(0).getHanhDong();
            if ("GUI_DUYET".equalsIgnoreCase(action)) {
                status = "cho_duyet";
            } else if ("PHE_DUYET".equalsIgnoreCase(action)) {
                status = "da_duyet";
            } else if ("TU_CHOI".equalsIgnoreCase(action)) {
                status = "tu_choi";
            }
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
        if (entity.getNguoiDungNguoiTao() != null && entity.getNguoiDungNguoiTao().getBoPhan() != null) {
            BoPhan bp = entity.getNguoiDungNguoiTao().getBoPhan();
            res.setBoPhan(new DocumentResponse.RelationSummary(
                bp.getBP_id() != null ? bp.getBP_id().longValue() : null,
                bp.getBP_ten()
            ));
        }
        if (entity.getVuViec() != null) {
            res.setVuViec(new DocumentResponse.RelationSummary(
                entity.getVuViec().getVV_id() != null ? entity.getVuViec().getVV_id().longValue() : null,
                entity.getVuViec().getVV_ten()
            ));
        }
        return res;
    }
}
