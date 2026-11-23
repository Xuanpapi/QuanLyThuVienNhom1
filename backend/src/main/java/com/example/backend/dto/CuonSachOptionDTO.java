package com.example.backend.dto;

import com.example.backend.entity.CuonSach;

public class CuonSachOptionDTO {
    private Integer id;
    private String maCuonSach;
    private String tinhTrang;
    private String trangThai;
    private Integer sachId;
    private String maSach;
    private String tenSach;

    public CuonSachOptionDTO() {}

    public CuonSachOptionDTO(CuonSach cuonSach) {
        this.id = cuonSach.getId();
        this.maCuonSach = cuonSach.getMaCuonSach();
        this.tinhTrang = cuonSach.getTinhTrang();
        this.trangThai = cuonSach.getTrangThai();
        this.sachId = cuonSach.getSachId();
        if (cuonSach.getSach() != null) {
            this.maSach = cuonSach.getSach().getMaSach();
            this.tenSach = cuonSach.getSach().getTenSach();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getSachId() {
        return sachId;
    }

    public void setSachId(Integer sachId) {
        this.sachId = sachId;
    }

    public String getMaSach() {
        return maSach;
    }

    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
}
