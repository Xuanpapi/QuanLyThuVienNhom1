package com.example.backend.controller;

import com.example.backend.service.ThongBaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API cảnh báo/thông báo phía người dùng.
 */
@RestController
@RequestMapping("/api/thong-bao")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    public ThongBaoController(ThongBaoService thongBaoService) {
        this.thongBaoService = thongBaoService;
    }

    @GetMapping("/nguoi-dung/{nguoiDungId}")
    public ResponseEntity<?> getThongBao(@PathVariable Integer nguoiDungId) {
        try {
            return ResponseEntity.ok(thongBaoService.getThongBao(nguoiDungId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
