package com.example.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.service.NguoiDungService;
import com.example.backend.dto.DangKyRequest;
import com.example.backend.entity.NguoiDung;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
public class NguoiDungController {
    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    // API đăng ký tài khoản
    @PostMapping("/dangky")
    public NguoiDung dangKy(@RequestBody DangKyRequest request) {
        return nguoiDungService.dangky(request);
    }
}
