package com.example.backend.controller;

import com.example.backend.dto.DocGiaOptionDTO;
import com.example.backend.service.DocGiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doc-gia")
public class DocGiaController {

    private final DocGiaService docGiaService;

    public DocGiaController(DocGiaService docGiaService) {
        this.docGiaService = docGiaService;
    }

    // API lấy danh sách độc giả cho form lập phiếu mượn của thủ thư
    @GetMapping
    public ResponseEntity<List<DocGiaOptionDTO>> getAllDocGia() {
        return ResponseEntity.ok(docGiaService.getAllOptions());
    }
}
