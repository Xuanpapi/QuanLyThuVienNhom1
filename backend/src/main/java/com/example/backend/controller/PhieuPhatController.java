package com.example.backend.controller;

import com.example.backend.dto.TaoPhieuPhatRequest;
import com.example.backend.service.PhieuPhatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * US 3.2.3 - API quản lý phiếu phạt.
 */
@RestController
@RequestMapping("/api/phieu-phat")
public class PhieuPhatController {

    private final PhieuPhatService phieuPhatService;

    public PhieuPhatController(PhieuPhatService phieuPhatService) {
        this.phieuPhatService = phieuPhatService;
    }

    @GetMapping
    public ResponseEntity<?> getDanhSach(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String keyword) {
        try {
            return ResponseEntity.ok(phieuPhatService.getDanhSach(trangThai, keyword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/phieu-muon/{phieuMuonId}")
    public ResponseEntity<?> getTheoPhieuMuon(@PathVariable Integer phieuMuonId) {
        try {
            return ResponseEntity.ok(phieuPhatService.getTheoPhieuMuon(phieuMuonId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> taoPhieuPhat(@Valid @RequestBody TaoPhieuPhatRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return loiValidation(bindingResult);
        }
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "Ghi nhận phiếu phạt thành công!",
                    "data", phieuPhatService.taoPhieuPhat(request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/thanh-toan")
    public ResponseEntity<?> thanhToan(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "Đã xác nhận thanh toán phiếu phạt!",
                    "data", phieuPhatService.thanhToan(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/gui-thong-bao")
    public ResponseEntity<?> guiThongBao(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(phieuPhatService.guiThongBao(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoaPhieuPhat(@PathVariable Integer id) {
        try {
            phieuPhatService.xoaPhieuPhat(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá phiếu phạt!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private ResponseEntity<?> loiValidation(BindingResult bindingResult) {
        String message = bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "Dữ liệu không hợp lệ";
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
