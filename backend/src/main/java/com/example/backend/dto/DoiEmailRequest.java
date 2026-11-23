package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** DTO nhận request đổi email (bước 2: xác nhận OTP + email mới). */
public class DoiEmailRequest {

    private Integer nguoiDungId;

    @NotBlank(message = "Vui lòng nhập OTP")
    @Pattern(regexp = "\\d{6}", message = "Mã OTP phải gồm 6 chữ số")
    private String otp;

    @NotBlank(message = "Vui lòng nhập email mới")
    @Email(message = "Email không hợp lệ")
    private String emailMoi;

    public Integer getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(Integer nguoiDungId) { this.nguoiDungId = nguoiDungId; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getEmailMoi() { return emailMoi; }
    public void setEmailMoi(String emailMoi) { this.emailMoi = emailMoi; }
}
