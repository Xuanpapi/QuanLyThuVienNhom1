package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** DTO nhận request đổi số điện thoại (bước 2: xác nhận OTP + SĐT mới). */
public class DoiSdtRequest {

    private Integer nguoiDungId;

    @NotBlank(message = "Vui lòng nhập OTP")
    @Pattern(regexp = "\\d{6}", message = "Mã OTP phải gồm 6 chữ số")
    private String otp;

    @NotBlank(message = "Vui lòng nhập số điện thoại mới")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String sdtMoi;

    public Integer getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(Integer nguoiDungId) { this.nguoiDungId = nguoiDungId; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getSdtMoi() { return sdtMoi; }
    public void setSdtMoi(String sdtMoi) { this.sdtMoi = sdtMoi; }
}
