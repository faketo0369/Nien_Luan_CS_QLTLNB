package com.qltnb.controller;

import com.qltnb.dto.ApiResponse;
import com.qltnb.dto.ClientRequest;
import com.qltnb.dto.ClientResponse;
import com.qltnb.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(@RequestBody ClientRequest request) {
        ClientResponse data = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAll(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) String cccdMst) {
        List<ClientResponse> data = clientService.getAllClients(ten, loai, cccdMst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getById(@PathVariable Long id) {
        ClientResponse data = clientService.getClientDetail(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> update(
            @PathVariable Long id, 
            @RequestBody ClientRequest request) {
        ClientResponse data = clientService.updateClient(id, request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa mềm dữ liệu khách hàng thành công."));
    }
}
