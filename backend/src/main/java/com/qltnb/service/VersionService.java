package com.qltnb.service;

import com.qltnb.dto.VersionResponse;
import com.qltnb.entity.NguoiDung;
import com.qltnb.entity.PhienBanTaiLieu;
import com.qltnb.entity.TaiLieu;
import com.qltnb.repository.NguoiDungRepository;
import com.qltnb.repository.PhienBanTaiLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionService {

    private final PhienBanTaiLieuRepository phienBanTaiLieuRepository;
    private final NguoiDungRepository nguoiDungRepository;

    private Path getUploadsRoot() {
        Path path = Paths.get("/app/uploads");
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = Paths.get("./uploads");
        }
        return path;
    }

    @Transactional
    public VersionResponse createVersion(TaiLieu doc, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Tập tin phiên bản tải lên không được để trống.");
        }

        Integer tlId = doc.getTL_id();
        long currentCount = phienBanTaiLieuRepository.countByDocumentId(tlId);
        String soPhienBan = "v" + (currentCount + 1);

        String originalFilename = file.getOriginalFilename();
        String cleanName = removeAccentAndSpaces(originalFilename);
        String extension = getFileExtension(originalFilename);

        // Path folder: /app/uploads/versions/<TL_id>/
        String relativeFolder = "versions/" + tlId;
        Path targetFolder = getUploadsRoot().resolve(relativeFolder);

        try {
            Files.createDirectories(targetFolder);
            // File name: <so_phien_ban>_<tenfile>
            String storedFilename = soPhienBan + "_" + cleanName;
            Path targetPath = targetFolder.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            NguoiDung creator = nguoiDungRepository.findByND_taiKhoan("admin").orElse(null);

            PhienBanTaiLieu pb = new PhienBanTaiLieu();
            pb.setTaiLieu(doc);
            pb.setNguoiCapNhat(creator);
            pb.setPBTL_maPhienBan(soPhienBan);
            pb.setPBTL_nguoiUpdate(creator != null ? creator.getND_hoTen() : "admin");
            pb.setPBTL_timeUpdate(LocalDateTime.now());
            pb.setPBTL_ghiChu("Cập nhật phiên bản " + soPhienBan);
            pb.setPBTL_duongDan(relativeFolder + "/" + storedFilename);
            pb.setPBTL_kichCo(file.getSize());
            pb.setPBTL_dinhDang(extension);

            PhienBanTaiLieu saved = phienBanTaiLieuRepository.save(pb);
            return mapToResponse(saved);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu trữ tập tin phiên bản vật lý. Lỗi: " + e.getMessage());
        }
    }

    public List<VersionResponse> getVersionsByDocumentId(Long documentId) {
        return phienBanTaiLieuRepository.findByDocumentId(documentId.intValue()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VersionResponse getVersionDetail(Long versionId) {
        PhienBanTaiLieu pb = phienBanTaiLieuRepository.findById(versionId.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản với ID: " + versionId));
        return mapToResponse(pb);
    }

    public byte[] downloadVersionFile(Long versionId) {
        PhienBanTaiLieu pb = phienBanTaiLieuRepository.findById(versionId.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản với ID: " + versionId));
        
        Path filePath = getUploadsRoot().resolve(pb.getPBTL_duongDan());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc tập tin phiên bản hoặc tệp không tồn tại.");
        }
    }

    private VersionResponse mapToResponse(PhienBanTaiLieu pb) {
        VersionResponse res = new VersionResponse();
        res.setId(pb.getPBTL_id() != null ? pb.getPBTL_id().longValue() : null);
        res.setSoPhienBan(pb.getPBTL_maPhienBan());

        if (pb.getNguoiCapNhat() != null) {
            res.setNguoiCapNhat(new VersionResponse.RelationSummary(
                    pb.getNguoiCapNhat().getND_id() != null ? pb.getNguoiCapNhat().getND_id().longValue() : null,
                    pb.getNguoiCapNhat().getND_hoTen()
            ));
        } else {
            res.setNguoiCapNhat(new VersionResponse.RelationSummary(null, pb.getPBTL_nguoiUpdate()));
        }

        res.setTimeUpdate(pb.getPBTL_timeUpdate());
        res.setGhiChu(pb.getPBTL_ghiChu());
        res.setKichCo(pb.getPBTL_kichCo());
        res.setDinhDang(pb.getPBTL_dinhDang());

        // Trích xuất tên file gốc từ đường dẫn lưu trữ (bỏ prefix phiên bản)
        String duongDan = pb.getPBTL_duongDan();
        if (duongDan != null && !duongDan.isBlank()) {
            String fileName = duongDan.contains("/") ? duongDan.substring(duongDan.lastIndexOf("/") + 1) : duongDan;
            // Bỏ prefix "v1_", "v2_" ... để lấy tên gốc
            if (fileName.matches("^v\\d+_.*")) {
                fileName = fileName.substring(fileName.indexOf("_") + 1);
            }
            res.setTenFileGoc(fileName);
        }

        return res;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String removeAccentAndSpaces(String str) {
        if (str == null) return "";
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("")
                .replace('đ', 'd').replace('Đ', 'd')
                .replaceAll("[^a-zA-Z0-9.]", "_");
    }
}
