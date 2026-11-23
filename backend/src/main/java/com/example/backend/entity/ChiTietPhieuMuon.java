package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ChiTietPhieuMuon")
public class ChiTietPhieuMuon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phieu_muon_id", nullable = false)
    private Integer phieuMuonId;

    @Column(name = "cuon_sach_id", nullable = false)
    private Integer cuonSachId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_muon_id", insertable = false, updatable = false)
    private PhieuMuon phieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuon_sach_id", insertable = false, updatable = false)
    private CuonSach cuonSach;

    public ChiTietPhieuMuon() {}

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

    public Integer getCuonSachId() {
        return cuonSachId;
    }

    public void setCuonSachId(Integer cuonSachId) {
        this.cuonSachId = cuonSachId;
    }

    public PhieuMuon getPhieuMuon() {
        return phieuMuon;
    }

    public void setPhieuMuon(PhieuMuon phieuMuon) {
        this.phieuMuon = phieuMuon;
    }

    public CuonSach getCuonSach() {
        return cuonSach;
    }

    public void setCuonSach(CuonSach cuonSach) {
        this.cuonSach = cuonSach;
    }
}
