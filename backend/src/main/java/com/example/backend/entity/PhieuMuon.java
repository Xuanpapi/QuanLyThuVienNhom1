package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PhieuMuon")
public class PhieuMuon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_gia_id", nullable = false)
    private Integer docGiaId;

    @Column(name = "nguoi_lap_id", nullable = false)
    private Integer nguoiLapId;

    @Column(name = "ngay_muon")
    private LocalDateTime ngayMuon;

    @Column(name = "han_tra", nullable = false)
    private LocalDate hanTra;

    @Column(name = "so_lan_gia_han")
    private Integer soLanGiaHan;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_gia_id", insertable = false, updatable = false)
    private DocGia docGia;

    @OneToMany(mappedBy = "phieuMuon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChiTietPhieuMuon> chiTietPhieuMuons = new HashSet<>();

    public PhieuMuon() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocGiaId() {
        return docGiaId;
    }

    public void setDocGiaId(Integer docGiaId) {
        this.docGiaId = docGiaId;
    }

    public Integer getNguoiLapId() {
        return nguoiLapId;
    }

    public void setNguoiLapId(Integer nguoiLapId) {
        this.nguoiLapId = nguoiLapId;
    }

    public LocalDateTime getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(LocalDateTime ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public LocalDate getHanTra() {
        return hanTra;
    }

    public void setHanTra(LocalDate hanTra) {
        this.hanTra = hanTra;
    }

    public Integer getSoLanGiaHan() {
        return soLanGiaHan;
    }

    public void setSoLanGiaHan(Integer soLanGiaHan) {
        this.soLanGiaHan = soLanGiaHan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public DocGia getDocGia() {
        return docGia;
    }

    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }

    public Set<ChiTietPhieuMuon> getChiTietPhieuMuons() {
        return chiTietPhieuMuons;
    }

    public void setChiTietPhieuMuons(Set<ChiTietPhieuMuon> chiTietPhieuMuons) {
        this.chiTietPhieuMuons = chiTietPhieuMuons;
    }
}
