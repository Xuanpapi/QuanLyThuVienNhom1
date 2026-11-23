package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * US 3.2.3 - Yêu cầu ghi nhận một phiếu phạt.
 * docGiaId không nhận từ client, backend tự lấy theo phiếu mượn.
 */
public class TaoPhieuPhatRequest {

    @NotNull(message = "Vui lòng chọn phiếu mượn cần ghi phạt")
    private Integer phieuMuonId;

    @NotNull(message = "Số tiền phạt không được để trống")
    @DecimalMin(value = "1", message = "Số tiền phạt phải lớn hơn 0")
    private BigDecimal soTien;

    @NotBlank(message = "Vui lòng nhập lý do phạt")
    @Size(max = 255, message = "Lý do phạt không được vượt quá 255 ký tự")
    private String lyDo;

    public TaoPhieuPhatRequest() {
    }

    public Integer getPhieuMuonId() {
        return phieuMuonId;
    }

    public void setPhieuMuonId(Integer phieuMuonId) {
        this.phieuMuonId = phieuMuonId;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }
}
