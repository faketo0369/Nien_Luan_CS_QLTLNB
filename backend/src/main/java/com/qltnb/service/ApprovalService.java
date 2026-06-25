package com.qltnb.service;

import com.qltnb.dto.ApprovalHistoryResponse;
import com.qltnb.entity.*;
import com.qltnb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final TaiLieuRepository taiLieuRepository;
    private final ApprovalRepository approvalRepository;
    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LichSuHoatDongRepository repoLog;

    @Transactional
    public void submit(Long documentId, Long actorId) {
        TaiLieu doc = taiLieuRepository.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        
        // Lưu lịch sử phê duyệt hệ thống
        saveApprovalLog(doc, actorId, "GUI_DUYET", "Nhân viên gửi yêu cầu phê duyệt tài liệu văn bản.");

        // Xác định trưởng phòng của bộ phận
        BoPhan boPhan = doc.getNguoiDungNguoiTao() != null ? doc.getNguoiDungNguoiTao().getBoPhan() : null;
        Integer bpId = boPhan != null ? boPhan.getBP_id() : null;
        
        NguoiDung truongPhong = null;
        if (bpId != null) {
            truongPhong = nguoiDungRepository.findTruongPhongByBoPhanId(bpId).stream().findFirst().orElse(null);
        }
        
        if (truongPhong == null) {
            truongPhong = nguoiDungRepository.findByND_taiKhoan("admin").orElse(null);
            if (truongPhong == null) {
                truongPhong = new NguoiDung();
                truongPhong.setND_id(1);
            }
        }
                
        createNotification(truongPhong, "YEU_CAU_DUYET", "Yêu cầu duyệt tài liệu mới", "Tài liệu '" + doc.getTL_ten() + "' đang chờ bạn phê duyệt.");
        writeActivityLog("gui_duyet", documentId, "Gửi phê duyệt tài liệu hành chính thành công.");
    }

    @Transactional
    public void approve(Long documentId, Long actorId) {
        TaiLieu doc = taiLieuRepository.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        
        saveApprovalLog(doc, actorId, "PHE_DUYET", "Phê duyệt thông qua.");

        // Tạo thông báo cho người tạo tài liệu chính
        NguoiDung creator = doc.getNguoiDungNguoiTao();
        if (creator == null) {
            creator = nguoiDungRepository.findByND_taiKhoan("admin").orElse(null);
            if (creator == null) {
                creator = new NguoiDung();
                creator.setND_id(1);
            }
        }
        
        createNotification(creator, "DA_DUYET", "Tài liệu đã được phê duyệt", "Tài liệu '" + doc.getTL_ten() + "' của bạn đã được duyệt thành công.");
        writeActivityLog("phe_duyet", documentId, "Trưởng phòng/Admin phê duyệt thành công văn bản.");
    }

    @Transactional
    public void reject(Long documentId, Long actorId, String ghiChu) {
        TaiLieu doc = taiLieuRepository.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        
        saveApprovalLog(doc, actorId, "TU_CHOI", ghiChu);

        // Tạo thông báo cho người tạo tài liệu kèm lý do
        NguoiDung creator = doc.getNguoiDungNguoiTao();
        if (creator == null) {
            creator = nguoiDungRepository.findByND_taiKhoan("admin").orElse(null);
            if (creator == null) {
                creator = new NguoiDung();
                creator.setND_id(1);
            }
        }
        
        createNotification(creator, "TU_CHOI", "Tài liệu bị từ chối phê duyệt", 
                "Tài liệu '" + doc.getTL_ten() + "' bị từ chối. Lý do: " + ghiChu);
        writeActivityLog("tu_choi", documentId, "Từ chối duyệt tài liệu: " + ghiChu);
    }

    public List<ApprovalHistoryResponse> getHistory(Long documentId) {
        return approvalRepository.findByDocumentIdOrderByTimeApproveDesc(documentId.intValue()).stream()
                .map(a -> {
                    ApprovalHistoryResponse res = new ApprovalHistoryResponse();
                    res.setId(a.getId());
                    res.setHanhDong(a.getHanhDong());
                    res.setGhiChu(a.getGhiChu());
                    res.setTimeApprove(a.getTimeApprove());
                    res.setTenNguoiDuyet(a.getNguoiDuyet() != null ? a.getNguoiDuyet().getND_hoTen() : "Hệ thống");
                    return res;
                }).collect(Collectors.toList());
    }

    private void saveApprovalLog(TaiLieu doc, Long actorId, String hanhDong, String ghiChu) {
        DocumentApproval approval = new DocumentApproval();
        approval.setDocument(doc);
        
        NguoiDung actor = new NguoiDung();
        actor.setND_id(actorId.intValue());
        approval.setNguoiDuyet(actor);
        
        approval.setHanhDong(hanhDong);
        approval.setGhiChu(ghiChu);
        approval.setTimeApprove(LocalDateTime.now());
        approvalRepository.save(approval);
    }

    private void createNotification(NguoiDung user, String loai, String tieuDe, String noiDung) {
        ThongBao tb = new ThongBao();
        tb.setNguoiNhan(user);
        tb.setLoai(loai);
        tb.setTieuDe(tieuDe);
        tb.setNoiDung(noiDung);
        tb.setNgayTao(LocalDateTime.now());
        tb.setDaDoc(false);
        thongBaoRepository.save(tb);
    }

    private void writeActivityLog(String hanhDong, Long docId, String moTa) {
        LichSuHoatDong log = new LichSuHoatDong();
        log.setNguoiDungId(1L);
        log.setTaiLieuId(docId);
        log.setLoaiHanhDong(hanhDong.toUpperCase());
        log.setMoTa(moTa);
        log.setTimeLog(LocalDateTime.now());
        repoLog.save(log);
    }
}
