package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuTra")
public class PhieuTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phieu_muon_id", nullable = false)
    private Integer phieuMuonId;

    @Column(name = "nguoi_lap_id", nullable = false)
    private Integer nguoiLapId;

    @Column(name = "ngay_tra")
    private LocalDateTime ngayTra;

    public PhieuTra() {}

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

    public Integer getNguoiLapId() {
        return nguoiLapId;
    }

    public void setNguoiLapId(Integer nguoiLapId) {
        this.nguoiLapId = nguoiLapId;
    }

    public LocalDateTime getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(LocalDateTime ngayTra) {
        this.ngayTra = ngayTra;
    }
}
