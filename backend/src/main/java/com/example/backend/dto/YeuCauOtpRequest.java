package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO dùng cho bước 1: yêu cầu gửi OTP.
 * Chỉ cần truyền userId (lấy từ session/local storage phía client).
 */
public class YeuCauOtpRequest {

    private Integer nguoiDungId;

    public Integer getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(Integer nguoiDungId) { this.nguoiDungId = nguoiDungId; }
}
