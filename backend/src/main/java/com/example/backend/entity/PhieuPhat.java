package com.example.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * US 3.2.3 - Phiếu phạt do trả sách quá hạn hoặc làm hỏng/mất sách.
 */
@Entity
@Table(name = "PhieuPhat")
public class PhieuPhat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phieu_muon_id", nullable = false)
    private Integer phieuMuonId;

    @Column(name = "doc_gia_id", nullable = false)
    private Integer docGiaId;

    @Column(name = "so_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    @Column(name = "ly_do", nullable = false, length = 255)
    private String lyDo;

    @Column(name = "trang_thai_thanh_toan", nullable = false, length = 50)
    private String trangThaiThanhToan;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_muon_id", insertable = false, updatable = false)
    private PhieuMuon phieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_gia_id", insertable = false, updatable = false)
    private DocGia docGia;

    public PhieuPhat() {
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

    public PhieuMuon getPhieuMuon() {
        return phieuMuon;
    }

    public void setPhieuMuon(PhieuMuon phieuMuon) {
        this.phieuMuon = phieuMuon;
    }

    public DocGia getDocGia() {
        return docGia;
    }

    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }
}
