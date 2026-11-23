package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTO bắt đầu đổi email: nhập email mới trước khi gửi OTP về email hiện tại. */
public class BatDauDoiEmailRequest {

    @NotBlank(message = "Vui lòng nhập email mới")
    @Email(message = "Email không hợp lệ")
    private String emailMoi;

    public String getEmailMoi() { return emailMoi; }
    public void setEmailMoi(String emailMoi) { this.emailMoi = emailMoi; }
}
