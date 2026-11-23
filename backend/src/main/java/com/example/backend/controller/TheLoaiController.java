package com.example.backend.controller;

import com.example.backend.entity.TheLoai;
import com.example.backend.service.TheLoaiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * US 2.2.3 - CRUD thể loại sách.
 */
@RestController
@RequestMapping("/api/the-loai")
public class TheLoaiController {

    private final TheLoaiService theLoaiService;

    public TheLoaiController(TheLoaiService theLoaiService) {
        this.theLoaiService = theLoaiService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(theLoaiService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TheLoai request) {
        try {
            TheLoai created = theLoaiService.create(request.getTenTheLoai());
            return ResponseEntity.ok(Map.of("message", "Thêm thể loại thành công!", "data", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody TheLoai request) {
        try {
            TheLoai updated = theLoaiService.update(id, request.getTenTheLoai());
            return ResponseEntity.ok(Map.of("message", "Cập nhật thể loại thành công!", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            theLoaiService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Xóa thể loại thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
