package com.qltnb.service;

import com.qltnb.entity.TaiLieu;
import com.qltnb.repository.TaiLieuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DocumentImportService {

    private static final Logger log = LoggerFactory.getLogger(DocumentImportService.class);

    @Autowired
    private TaiLieuRepository taiLieuRepository;

    @Transactional
    public Map<String, Object> importScannedData(List<DocumentScanService.ScanResult> scanResults) {
        int daCapNhat = 0;
        int taoMoi = 0;
        int fileRong = 0;
        List<Map<String, String>> chiTiet = new ArrayList<>();

        for (DocumentScanService.ScanResult scan : scanResults) {
            if ("FILE_RONG".equals(scan.status)) {
                fileRong++;
            }

            // Tìm tài liệu đã tồn tại trong DB theo cột đường dẫn tương đối
            Optional<TaiLieu> existingDoc = taiLieuRepository.findByTL_duongDan(scan.relativePath);
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("tenFile", scan.fileName);

            if (existingDoc.isPresent()) {
                TaiLieu doc = existingDoc.get();
                // Cập nhật thuộc tính kích thước file thực tế (bytes)
                doc.setTL_dungLuong(scan.fileSizeBytes);
                taiLieuRepository.save(doc);
                
                daCapNhat++;
                fileInfo.put("trangThai", "DA_CAP_NHAT");
                log.info("DA_CAP_NHAT: {}", scan.fileName);
            } else {
                // Nếu chưa có trong DB -> Khởi tạo bản ghi thô mới
                TaiLieu newDoc = new TaiLieu();
                newDoc.setTL_ten(scan.fileName.replace(".html", ""));
                newDoc.setTL_duongDan(scan.relativePath);
                newDoc.setTL_dinhDang("html");
                newDoc.setTL_dungLuong(scan.fileSizeBytes);
                newDoc.setTL_ngayTao(LocalDateTime.now());
                newDoc.setTL_daXoa(false);
                newDoc.setTL_baoMat("NOI_BO");
                
                taiLieuRepository.save(newDoc);
                
                taoMoi++;
                fileInfo.put("trangThai", "TAO_MOI");
                log.info("TAO_MOI: {}", scan.fileName);
            }
            chiTiet.add(fileInfo);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("tongFile", scanResults.size());
        report.put("fileRong", fileRong);
        report.put("daCapNhat", daCapNhat);
        report.put("taoMoi", taoMoi);
        report.put("chiTiet", chiTiet);

        return report;
    }
}
