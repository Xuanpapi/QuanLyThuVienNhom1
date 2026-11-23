package com.example.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MuonOnlineRequest {
    @NotNull(message = "ID người dùng không được để trống")
    private Integer nguoiDungId;

    @NotNull(message = "ID sách không được để trống")
    private Integer sachId;

    // Không bắt buộc: nếu bỏ trống sẽ mặc định mượn 14 ngày.
    @FutureOrPresent(message = "Ngày trả không được nhỏ hơn ngày hôm nay")
    private LocalDate hanTra;

    public MuonOnlineRequest() {}

    public Integer getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Integer nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public Integer getSachId() {
        return sachId;
    }

    public void setSachId(Integer sachId) {
        this.sachId = sachId;
    }

    public LocalDate getHanTra() {
        return hanTra;
    }

    public void setHanTra(LocalDate hanTra) {
        this.hanTra = hanTra;
    }
}
