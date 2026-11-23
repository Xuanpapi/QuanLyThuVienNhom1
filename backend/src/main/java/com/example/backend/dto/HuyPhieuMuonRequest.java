package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public class HuyPhieuMuonRequest {

    @NotNull(message = "ID người dùng không được để trống")
    private Integer nguoiDungId;

    public HuyPhieuMuonRequest() {
    }

    public Integer getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Integer nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }
}
