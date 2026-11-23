package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class DangNhapRequest {
    // Cho phép nhập Mã sinh viên hoặc Email
    @NotBlank(message = "Vui lòng nhập mã sinh viên hoặc email")
    private String tai_khoan;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String mat_khau;

    public DangNhapRequest() {
    }

    public DangNhapRequest(String tai_khoan, String mat_khau) {
        this.tai_khoan = tai_khoan;
        this.mat_khau = mat_khau;
    }

    public String getTai_khoan() {
        return tai_khoan;
    }

    public void setTai_khoan(String tai_khoan) {
        this.tai_khoan = tai_khoan;
    }

    public String getMat_khau() {
        return mat_khau;
    }

    public void setMat_khau(String mat_khau) {
        this.mat_khau = mat_khau;
    }
}
