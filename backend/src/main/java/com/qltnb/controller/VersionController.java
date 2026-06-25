package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.VersionResponse;
import com.qltnb.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents/{documentId}/versions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VersionController {

    private final VersionService versionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VersionResponse>>> getVersions(
            @PathVariable Long documentId) {
        List<VersionResponse> data = versionService.getVersionsByDocumentId(documentId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{versionId}")
    public ResponseEntity<ApiResponse<VersionResponse>> getVersionDetail(
            @PathVariable Long documentId,
            @PathVariable Long versionId) {
        VersionResponse data = versionService.getVersionDetail(versionId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{versionId}/download")
    public ResponseEntity<byte[]> downloadVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId) {
        byte[] fileData = versionService.downloadVersionFile(versionId);
        VersionResponse detail = versionService.getVersionDetail(versionId);
        
        String filename = "document_" + documentId + "_" + detail.getSoPhienBan() + "." + detail.getDinhDang();
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileData);
    }
}
