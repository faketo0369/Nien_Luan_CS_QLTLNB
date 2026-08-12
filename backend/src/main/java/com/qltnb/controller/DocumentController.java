package com.qltnb.controller;

import com.qltnb.dto.*;
import com.qltnb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Cho phép kết nối CORS từ frontend Vue 3
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> create(
            @ModelAttribute DocumentRequest request,
            @RequestParam("file") MultipartFile file) {
        DocumentResponse data = documentService.createDocument(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> getAll(DocumentFilterRequest filter) {
        Page<DocumentResponse> data = documentService.getDocuments(filter);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getById(@PathVariable Long id) {
        DocumentResponse data = documentService.getDetail(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> update(
            @PathVariable Long id, 
            @RequestBody DocumentRequest request) {
        DocumentResponse data = documentService.updateDocument(id, request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa mềm và tệp tin liên quan thành công."));
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> changeFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        DocumentResponse data = documentService.replaceFile(id, file);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] fileData = documentService.downloadFile(id);
        DocumentResponse doc = documentService.getDetail(id);
        
        String cleanName = doc.getTen().replaceAll("[^a-zA-Z0-9]", "_") + "." + doc.getDinhDang();
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cleanName + "\"")
                .body(fileData);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> preview(@PathVariable Long id) {
        try {
            byte[] fileData = documentService.downloadFile(id);
            if (fileData == null || fileData.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Tài liệu này chưa có tệp tin vật lý đính kèm."));
            }
            DocumentResponse doc = documentService.getDetail(id);
            
            String dinhDang = doc.getDinhDang() != null ? doc.getDinhDang().toLowerCase() : "";
            String contentType;
            if (dinhDang.equals("pdf")) {
                contentType = "application/pdf";
            } else if (dinhDang.equals("png")) {
                contentType = "image/png";
            } else if (dinhDang.equals("jpg") || dinhDang.equals("jpeg")) {
                contentType = "image/jpeg";
            } else if (dinhDang.equals("gif")) {
                contentType = "image/gif";
            } else if (dinhDang.equals("webp")) {
                contentType = "image/webp";
            } else if (dinhDang.equals("docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (dinhDang.equals("doc")) {
                contentType = "application/msword";
            } else {
                contentType = "application/octet-stream";
            }
            
            String cleanName = doc.getTen().replaceAll("[^a-zA-Z0-9]", "_") + "." + (doc.getDinhDang() != null ? doc.getDinhDang() : "bin");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cleanName + "\"")
                    .body(fileData);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Không thể tải tập tin xem trước: " + ex.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<String>> submit(@PathVariable Long id) {
        documentService.submitForApproval(id);
        return ResponseEntity.ok(ApiResponse.success("Tài liệu đã được chuyển sang trạng thái chờ duyệt."));
    }
}
