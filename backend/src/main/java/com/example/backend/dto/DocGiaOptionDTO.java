package com.example.backend.dto;

import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;

public class DocGiaOptionDTO {
    private Integer id;
    private String maTheDocGia;
    private String hoTen;
    private String email;
    private String lop;
    private String khoa;
    private String soDienThoai;

    public DocGiaOptionDTO() {}

    public DocGiaOptionDTO(DocGia docGia) {
        this.id = docGia.getId();
        this.maTheDocGia = docGia.getMaTheDocGia();
        NguoiDung nguoiDung = docGia.getNguoiDung();
        if (nguoiDung != null) {
            this.hoTen = nguoiDung.getHo_ten();
            this.email = nguoiDung.getEmail();
            this.lop = nguoiDung.getLop();
            this.khoa = nguoiDung.getKhoa();
            this.soDienThoai = nguoiDung.getSo_dien_thoai();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaTheDocGia() {
        return maTheDocGia;
    }

    public void setMaTheDocGia(String maTheDocGia) {
        this.maTheDocGia = maTheDocGia;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
