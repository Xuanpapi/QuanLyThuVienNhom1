package com.example.backend.dto;

import com.example.backend.entity.Sach;
import com.example.backend.entity.TacGia;
import com.example.backend.entity.TheLoai;

import java.util.HashSet;
import java.util.Set;

public class SachResponseDTO {
    private Integer id;
    private String maSach;
    private String tenSach;
    private String moTa;
    private Integer namXuatBan;
    private Integer nhaXuatBanId;
    private Integer danhMucId;
    private Set<TacGia> tacGias = new HashSet<>();
    private Set<TheLoai> theLoais = new HashSet<>();

    public SachResponseDTO() {}

    public SachResponseDTO(Sach sach) {
        this.id = sach.getId();
        this.maSach = sach.getMaSach();
        this.tenSach = sach.getTenSach();
        this.moTa = sach.getMoTa();
        this.namXuatBan = sach.getNamXuatBan();
        this.nhaXuatBanId = sach.getNhaXuatBanId();
        this.danhMucId = sach.getDanhMucId();
        this.tacGias = sach.getTacGias();
        this.theLoais = sach.getTheLoais();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Integer getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(Integer namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public Integer getNhaXuatBanId() {
        return nhaXuatBanId;
    }

    public void setNhaXuatBanId(Integer nhaXuatBanId) {
        this.nhaXuatBanId = nhaXuatBanId;
    }

    public Integer getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(Integer danhMucId) {
        this.danhMucId = danhMucId;
    }

    public Set<TacGia> getTacGias() {
        return tacGias;
    }

    public void setTacGias(Set<TacGia> tacGias) {
        this.tacGias = tacGias;
    }

    public Set<TheLoai> getTheLoais() {
        return theLoais;
    }

    public void setTheLoais(Set<TheLoai> theLoais) {
        this.theLoais = theLoais;
    }
}
