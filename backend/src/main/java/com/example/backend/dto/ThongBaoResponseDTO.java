package com.example.backend.dto;

import java.time.LocalDate;

/**
 * Một thông báo/cảnh báo hiển thị cho người dùng.
 */
public class ThongBaoResponseDTO {
    private String id;
    private String loai;
    private String tieuDe;
    private String noiDung;
    private String mucDo;
    private LocalDate ngay;
    private String thamChieu;

    public ThongBaoResponseDTO() {
    }

    public ThongBaoResponseDTO(String id, String loai, String tieuDe, String noiDung,
                               String mucDo, LocalDate ngay, String thamChieu) {
        this.id = id;
        this.loai = loai;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.mucDo = mucDo;
        this.ngay = ngay;
        this.thamChieu = thamChieu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getMucDo() {
        return mucDo;
    }

    public void setMucDo(String mucDo) {
        this.mucDo = mucDo;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public String getThamChieu() {
        return thamChieu;
    }

    public void setThamChieu(String thamChieu) {
        this.thamChieu = thamChieu;
    }
}
