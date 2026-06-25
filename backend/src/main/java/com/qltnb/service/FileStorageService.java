package com.qltnb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FileStorageService {

    private final Path rootLocation;
    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx", "xlsx", "html");
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    public FileStorageService() {
        Path path = Paths.get("/app/uploads");
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = Paths.get("./uploads");
        }
        this.rootLocation = path;
    }

    public String storeFile(MultipartFile file, String loai, Long vuViecId) {
        if (file.isEmpty()) {
            throw new RuntimeException("Tập tin tải lên không được để trống.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Dung lượng tập tin vượt quá giới hạn cho phép (50MB).");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException("Định dạng tập tin không được hỗ trợ.");
        }

        // Định dạng đường dẫn thư mục: /app/uploads/<loai>/<vuViecId>/
        String subFolder = loai + "/" + (vuViecId != null ? vuViecId : "chung");
        Path targetFolder = this.rootLocation.resolve(subFolder);

        try {
            Files.createDirectories(targetFolder);
            String cleanName = removeAccentAndSpaces(originalFilename);
            String storedFilename = System.currentTimeMillis() + "_" + cleanName;
            Path targetPath = targetFolder.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối để lưu vào DB
            return subFolder + "/" + storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu trữ tập tin vật lý. Lỗi: " + e.getMessage());
        }
    }

    public byte[] readFile(String relativePath) {
        try {
            Path filePath = this.rootLocation.resolve(relativePath);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc tập tin hoặc tệp không tồn tại.");
        }
    }

    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path filePath = this.rootLocation.resolve(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Cảnh báo: Không thể xóa file vật lý tại đường dẫn: " + relativePath);
        }
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
