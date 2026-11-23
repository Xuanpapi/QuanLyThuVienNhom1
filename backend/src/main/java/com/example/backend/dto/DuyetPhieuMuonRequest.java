package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public class DuyetPhieuMuonRequest {

    @NotNull(message = "ID người duyệt không được để trống")
    private Integer nguoiDuyetId;

    public DuyetPhieuMuonRequest() {}

    public Integer getNguoiDuyetId() {
        return nguoiDuyetId;
    }

    public void setNguoiDuyetId(Integer nguoiDuyetId) {
        this.nguoiDuyetId = nguoiDuyetId;
    }
}
