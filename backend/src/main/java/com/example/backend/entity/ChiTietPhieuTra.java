package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ChiTietPhieuTra")
public class ChiTietPhieuTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phieu_tra_id", nullable = false)
    private Integer phieuTraId;

    @Column(name = "cuon_sach_id", nullable = false)
    private Integer cuonSachId;

    @Column(name = "tinh_trang_khi_tra", nullable = false, length = 50)
    private String tinhTrangKhiTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_tra_id", insertable = false, updatable = false)
    private PhieuTra phieuTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuon_sach_id", insertable = false, updatable = false)
    private CuonSach cuonSach;

    public ChiTietPhieuTra() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhieuTraId() {
        return phieuTraId;
    }

    public void setPhieuTraId(Integer phieuTraId) {
        this.phieuTraId = phieuTraId;
    }

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

    public PhieuTra getPhieuTra() {
        return phieuTra;
    }

    public void setPhieuTra(PhieuTra phieuTra) {
        this.phieuTra = phieuTra;
    }

    public CuonSach getCuonSach() {
        return cuonSach;
    }

    public void setCuonSach(CuonSach cuonSach) {
        this.cuonSach = cuonSach;
    }
}
