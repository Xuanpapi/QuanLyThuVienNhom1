package com.example.backend.controller;

import com.example.backend.entity.DanhMuc;
import com.example.backend.service.DanhMucService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * US 2.2.3 - CRUD danh mục sách.
 */
@RestController
@RequestMapping("/api/danh-muc")
public class DanhMucController {

    private final DanhMucService danhMucService;

    public DanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(danhMucService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DanhMuc request) {
        try {
            DanhMuc created = danhMucService.create(request.getTenDanhMuc(), request.getMoTa());
            return ResponseEntity.ok(Map.of("message", "Thêm danh mục thành công!", "data", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody DanhMuc request) {
        try {
            DanhMuc updated = danhMucService.update(id, request.getTenDanhMuc(), request.getMoTa());
            return ResponseEntity.ok(Map.of("message", "Cập nhật danh mục thành công!", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            danhMucService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Xóa danh mục thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
