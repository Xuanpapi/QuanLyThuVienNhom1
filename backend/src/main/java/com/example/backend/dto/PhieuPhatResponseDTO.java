package com.example.backend.dto;

import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;
import com.example.backend.entity.PhieuMuon;
import com.example.backend.entity.PhieuPhat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * US 3.2.3 - Dữ liệu trả về cho màn quản lý phiếu phạt.
 */
public class PhieuPhatResponseDTO {
    private Integer id;
    private Integer phieuMuonId;
    private Integer docGiaId;
    private String tenDocGia;
    private String maTheDocGia;
    private String emailDocGia;
    private BigDecimal soTien;
    private String lyDo;
    private String trangThaiThanhToan;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayThanhToan;
    private LocalDate hanTra;

    public PhieuPhatResponseDTO() {
    }

    public PhieuPhatResponseDTO(PhieuPhat phieuPhat) {
        this.id = phieuPhat.getId();
        this.phieuMuonId = phieuPhat.getPhieuMuonId();
        this.docGiaId = phieuPhat.getDocGiaId();
        this.soTien = phieuPhat.getSoTien();
        this.lyDo = phieuPhat.getLyDo();
        this.trangThaiThanhToan = phieuPhat.getTrangThaiThanhToan();
        this.ngayTao = phieuPhat.getNgayTao();
        this.ngayThanhToan = phieuPhat.getNgayThanhToan();

        DocGia docGia = phieuPhat.getDocGia();
        if (docGia != null) {
            this.maTheDocGia = docGia.getMaTheDocGia();
            NguoiDung nguoiDung = docGia.getNguoiDung();
            if (nguoiDung != null) {
                this.tenDocGia = nguoiDung.getHo_ten();
                this.emailDocGia = nguoiDung.getEmail();
            }
        }

        PhieuMuon phieuMuon = phieuPhat.getPhieuMuon();
        if (phieuMuon != null) {
            this.hanTra = phieuMuon.getHanTra();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhieuMuonId() {
        return phieuMuonId;
    }

    public void setPhieuMuonId(Integer phieuMuonId) {
        this.phieuMuonId = phieuMuonId;
    }

    public Integer getDocGiaId() {
        return docGiaId;
    }

    public void setDocGiaId(Integer docGiaId) {
        this.docGiaId = docGiaId;
    }

    public String getTenDocGia() {
        return tenDocGia;
    }

    public void setTenDocGia(String tenDocGia) {
        this.tenDocGia = tenDocGia;
    }

    public String getMaTheDocGia() {
        return maTheDocGia;
    }

    public void setMaTheDocGia(String maTheDocGia) {
        this.maTheDocGia = maTheDocGia;
    }

    public String getEmailDocGia() {
        return emailDocGia;
    }

    public void setEmailDocGia(String emailDocGia) {
        this.emailDocGia = emailDocGia;
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

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public LocalDate getHanTra() {
        return hanTra;
    }

    public void setHanTra(LocalDate hanTra) {
        this.hanTra = hanTra;
    }
}
