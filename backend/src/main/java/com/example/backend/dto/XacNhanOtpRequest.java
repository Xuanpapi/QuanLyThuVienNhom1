package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** DTO xác nhận OTP cho đổi email (dùng chung cho bước xác nhận email cũ và email mới). */
public class XacNhanOtpRequest {

    @NotBlank(message = "Vui lòng nhập OTP")
    @Pattern(regexp = "\\d{6}", message = "Mã OTP phải gồm 6 chữ số")
    private String otp;

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
