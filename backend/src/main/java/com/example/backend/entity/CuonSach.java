package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CuonSach")
public class CuonSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sach_id", nullable = false)
    private Integer sachId;

    @Column(name = "ma_cuon_sach", nullable = false, unique = true, length = 30)
    private String maCuonSach;

    @Column(name = "tinh_trang", length = 50)
    private String tinhTrang;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sach_id", insertable = false, updatable = false)
    private Sach sach;

    public CuonSach() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSachId() {
        return sachId;
    }

    public void setSachId(Integer sachId) {
        this.sachId = sachId;
    }

    public String getMaCuonSach() {
        return maCuonSach;
    }

    public void setMaCuonSach(String maCuonSach) {
        this.maCuonSach = maCuonSach;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Sach getSach() {
        return sach;
    }

    public void setSach(Sach sach) {
        this.sach = sach;
    }
}
