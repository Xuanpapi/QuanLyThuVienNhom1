package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO nhận request đổi mật khẩu. */
public class DoiMatKhauRequest {

    @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
    private String matKhauCu;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String matKhauMoi;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    private String xacNhanMatKhauMoi;

    public String getMatKhauCu() { return matKhauCu; }
    public void setMatKhauCu(String matKhauCu) { this.matKhauCu = matKhauCu; }

    public String getMatKhauMoi() { return matKhauMoi; }
    public void setMatKhauMoi(String matKhauMoi) { this.matKhauMoi = matKhauMoi; }

    public String getXacNhanMatKhauMoi() { return xacNhanMatKhauMoi; }
    public void setXacNhanMatKhauMoi(String xacNhanMatKhauMoi) { this.xacNhanMatKhauMoi = xacNhanMatKhauMoi; }
}
