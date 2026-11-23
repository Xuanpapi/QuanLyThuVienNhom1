package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request khóa / mở khóa tài khoản (US 1.2.2).
 * nguoiThucHienId dùng để chặn thủ thư tự khóa tài khoản của chính mình.
 */
public class CapNhatTrangThaiRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean trangThai;

    private Integer nguoiThucHienId;

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getNguoiThucHienId() {
        return nguoiThucHienId;
    }

    public void setNguoiThucHienId(Integer nguoiThucHienId) {
        this.nguoiThucHienId = nguoiThucHienId;
    }
}
