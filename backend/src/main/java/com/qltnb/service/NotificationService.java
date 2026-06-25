package com.qltnb.service;

import com.qltnb.dto.NotificationResponse;
import com.qltnb.entity.NguoiDung;
import com.qltnb.entity.ThongBao;
import com.qltnb.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ThongBaoRepository thongBaoRepository;

    @Transactional
    public void createNotification(Long nguoiNhanId, String loai, String tieuDe, String noiDung) {
        ThongBao tb = new ThongBao();
        NguoiDung user = new NguoiDung();
        user.setND_id(nguoiNhanId.intValue());
        tb.setNguoiNhan(user);
        tb.setLoai(loai.toUpperCase());
        tb.setTieuDe(tieuDe);
        tb.setNoiDung(noiDung);
        tb.setNgayTao(LocalDateTime.now());
        tb.setDaDoc(false);
        thongBaoRepository.save(tb);
    }

    public List<NotificationResponse> getMyNotifications(Long userId) {
        Pageable limitTwenty = PageRequest.of(0, 20, Sort.by("daDoc").ascending().and(Sort.by("ngayTao").descending()));
        
        return thongBaoRepository.findByNguoiNhanId(userId.intValue(), limitTwenty).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long id) {
        ThongBao tb = thongBaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thông báo không tồn tại."));
        tb.setDaDoc(true);
        thongBaoRepository.save(tb);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        Pageable pageable = PageRequest.of(0, 100);
        List<ThongBao> unreadList = thongBaoRepository.findByNguoiNhanId(userId.intValue(), pageable);
        unreadList.forEach(tb -> tb.setDaDoc(true));
        thongBaoRepository.saveAll(unreadList);
    }

    public long countUnread(Long userId) {
        return thongBaoRepository.countByNguoiNhanIdAndDaDocFalse(userId.intValue());
    }

    private NotificationResponse mapToDto(ThongBao entity) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(entity.getId());
        dto.setLoai(entity.getLoai());
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setNgayTao(entity.getNgayTao());
        dto.setDaDoc(entity.getDaDoc());
        return dto;
    }
}
