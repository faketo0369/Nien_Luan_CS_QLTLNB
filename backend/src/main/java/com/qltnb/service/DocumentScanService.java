package com.qltnb.service;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentScanService {

    private static final Logger log = LoggerFactory.getLogger(DocumentScanService.class);

    // Lớp chứa kết quả quét tạm thời của từng file để chuyển giao cho ImportService
    public static class ScanResult {
        public String fileName;
        public String relativePath; // Dạng: documents/dat-dai/luat-dat-dai-2024.html
        public long fileSizeBytes;
        public String textContent;
        public String status; // FILE_RONG, OK

        public ScanResult(String fileName, String relativePath, long fileSizeBytes, String textContent, String status) {
            this.fileName = fileName;
            this.relativePath = relativePath;
            this.fileSizeBytes = fileSizeBytes;
            this.textContent = textContent;
            this.status = status;
        }
    }

    public List<ScanResult> scanDocuments() {
        List<ScanResult> results = new ArrayList<>();
        
        // Hỗ trợ cả 2 môi trường: /app/documents (Docker) hoặc documents (Local Windows gốc dự án)
        File docDir = new File("/app/documents");
        if (!docDir.exists()) {
            docDir = new File("../documents"); // Trỏ ra thư mục documents chung từ folder backend
        }

        log.info("=== BẮT ĐẦU QUÉT THƯ MỤC TÀI LIỆU TẠI: {} ===", docDir.getAbsolutePath());

        if (docDir.exists() && docDir.isDirectory()) {
            scanDirectoryRecursive(docDir, docDir.getParentFile(), results);
        } else {
            log.error("KHÔNG TÌM THẤY THƯ MỤC TÀI LIỆU DỮ LIỆU CRAWL!");
        }

        return results;
    }

    private void scanDirectoryRecursive(File currentDir, File rootDir, List<ScanResult> results) {
        File[] files = currentDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectoryRecursive(file, rootDir, results);
            } else if (file.getName().endsWith(".html")) {
                try {
                    // 1. Tính toán đường dẫn tương đối chuẩn hóa cấu trúc lưu trữ
                    String relativePath = rootDir.toURI().relativize(file.toURI()).getPath();
                    
                    // 2. Đọc và bóc tách HTML lấy Text thuần bằng Jsoup
                    String htmlContent = Jsoup.parse(file, StandardCharsets.UTF_8.name()).html();
                    String textPure = Jsoup.parse(htmlContent).text();
                    
                    long sizeBytes = file.length();
                    String status = "OK";

                    // 3. In log xác nhận tiến độ và kiểm tra điều kiện file rỗng (< 100 ký tự)
                    log.info("FILE_QUET: {} | Ký tự text thuần: {}", file.getName(), textPure.length());

                    if (textPure.trim().length() < 100) {
                        status = "FILE_RONG";
                        log.warn("FILE_RONG: {}", file.getName());
                    }

                    results.add(new ScanResult(file.getName(), relativePath, sizeBytes, textPure, status));

                } catch (IOException e) {
                    log.error("LỖI ĐỌC FILE: {} - {}", file.getAbsolutePath(), e.getMessage());
                }
            }
        }
    }
}
