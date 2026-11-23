package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTO nhận request đặt lại mật khẩu qua OTP (quên mật khẩu). */
public class DatLaiMatKhauRequest {

    @NotBlank(message = "Vui lòng nhập email")
    private String email;

    @NotBlank(message = "Vui lòng nhập mã OTP")
    @Pattern(regexp = "\\d{6}", message = "Mã OTP phải gồm 6 chữ số")
    private String otp;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String matKhauMoi;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    private String xacNhanMatKhauMoi;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getMatKhauMoi() { return matKhauMoi; }
    public void setMatKhauMoi(String matKhauMoi) { this.matKhauMoi = matKhauMoi; }

    public String getXacNhanMatKhauMoi() { return xacNhanMatKhauMoi; }
    public void setXacNhanMatKhauMoi(String xacNhanMatKhauMoi) { this.xacNhanMatKhauMoi = xacNhanMatKhauMoi; }
}
