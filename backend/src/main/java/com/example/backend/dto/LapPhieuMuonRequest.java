package com.example.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class LapPhieuMuonRequest {

    @NotNull(message = "ID độc giả không được để trống")
    private Integer docGiaId;

    @NotEmpty(message = "Danh sách cuốn sách mượn không được để trống")
    private List<Integer> danhSachCuonSachId;

    @NotNull(message = "Hạn trả không được để trống")
    @FutureOrPresent(message = "Hạn trả không được nhỏ hơn ngày hôm nay")
    private LocalDate hanTra;

    @NotNull(message = "ID người lập phiếu không được để trống")
    private Integer nguoiLapId;

    public LapPhieuMuonRequest() {}

    public Integer getDocGiaId() {
        return docGiaId;
    }

    public void setDocGiaId(Integer docGiaId) {
        this.docGiaId = docGiaId;
    }

    public List<Integer> getDanhSachCuonSachId() {
        return danhSachCuonSachId;
    }

    public void setDanhSachCuonSachId(List<Integer> danhSachCuonSachId) {
        this.danhSachCuonSachId = danhSachCuonSachId;
    }

    public LocalDate getHanTra() {
        return hanTra;
    }

    public void setHanTra(LocalDate hanTra) {
        this.hanTra = hanTra;
    }

    public Integer getNguoiLapId() {
        return nguoiLapId;
    }

    public void setNguoiLapId(Integer nguoiLapId) {
        this.nguoiLapId = nguoiLapId;
    }
}
