package com.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TraSachRequest {

    @NotNull(message = "ID người lập phiếu trả không được để trống")
    private Integer nguoiLapId;

    @NotEmpty(message = "Danh sách sách trả không được để trống")
    @Valid
    private List<ChiTietTra> danhSachTra;

    public TraSachRequest() {}

    public Integer getNguoiLapId() {
        return nguoiLapId;
    }

    public void setNguoiLapId(Integer nguoiLapId) {
        this.nguoiLapId = nguoiLapId;
    }

    public List<ChiTietTra> getDanhSachTra() {
        return danhSachTra;
    }

    public void setDanhSachTra(List<ChiTietTra> danhSachTra) {
        this.danhSachTra = danhSachTra;
    }

    public static class ChiTietTra {

        @NotNull(message = "ID cuốn sách không được để trống")
        private Integer cuonSachId;

        @NotNull(message = "Tình trạng khi trả không được để trống")
        private String tinhTrangKhiTra;

        public ChiTietTra() {}

        public Integer getCuonSachId() {
            return cuonSachId;
        }

        public void setCuonSachId(Integer cuonSachId) {
            this.cuonSachId = cuonSachId;
        }

        public String getTinhTrangKhiTra() {
            return tinhTrangKhiTra;
        }

        public void setTinhTrangKhiTra(String tinhTrangKhiTra) {
            this.tinhTrangKhiTra = tinhTrangKhiTra;
        }
    }
}
