package com.example.backend.controller;

import com.example.backend.dto.CuonSachOptionDTO;
import com.example.backend.dto.SachResponseDTO;
import com.example.backend.dto.SachSearchResponse;
import com.example.backend.entity.Sach;
import com.example.backend.service.SachService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sach")
public class SachController {
    private final SachService sachService;

    public SachController(SachService sachService) {
        this.sachService = sachService;
    }

    // API Tra cứu / Tìm kiếm sách (US 2.1.1 & 2.1.2)
    @GetMapping
    public ResponseEntity<List<SachSearchResponse>> getAllOrSearchBooks(@RequestParam(required = false) String keyword) {
        List<SachSearchResponse> books = (keyword == null || keyword.trim().isEmpty()) 
                ? sachService.getAllBooks() 
                : sachService.searchBooks(keyword.trim());
        return ResponseEntity.ok(books);
    }

    // API Xem chi tiết sách
    @GetMapping("/{id}")
    public ResponseEntity<SachResponseDTO> getBookById(@PathVariable Integer id) {
        SachResponseDTO sach = sachService.getBookById(id);
        return ResponseEntity.ok(sach);
    }

    // API lấy danh sách cuốn sách vật lý của một đầu sách (dùng cho lập phiếu mượn)
    @GetMapping("/{id}/cuon-sach")
    public ResponseEntity<List<CuonSachOptionDTO>> getCuonSach(
            @PathVariable Integer id,
            @RequestParam(name = "khaDung", defaultValue = "false") boolean khaDung) {
        return ResponseEntity.ok(sachService.getCuonSachBySachId(id, khaDung));
    }

    // API Thêm mới sách (Thủ thư - US 2.2.1)
    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Sach sach) {
        try {
            SachResponseDTO newSach = sachService.createBook(sach);
            return ResponseEntity.ok(Map.of("message", "Thêm sách thành công!", "data", newSach));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // API Cập nhật sách
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @Valid @RequestBody Sach sachDetails) {
        try {
            SachResponseDTO updatedSach = sachService.updateBook(id, sachDetails);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin sách thành công!", "data", updatedSach));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // API Xóa sách
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        try {
            sachService.deleteBook(id);
            return ResponseEntity.ok(Map.of("message", "Xóa sách thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
