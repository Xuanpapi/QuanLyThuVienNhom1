package com.example.backend.dto;

import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;

import java.time.LocalDate;

public class NguoiDungProfileDTO {
    private String ma_sv;
    private String email;
    private String ho_ten;
    private String so_dien_thoai;
    private String lop;
    private String khoa;
    private String maTheDocGia;
    private LocalDate ngayCap;
    private LocalDate ngayHetHan;

    public NguoiDungProfileDTO(NguoiDung nguoiDung, DocGia docGia) {
        this.ma_sv = nguoiDung.getMasv();
        this.email = nguoiDung.getEmail();
        this.ho_ten = nguoiDung.getHo_ten();
        this.so_dien_thoai = nguoiDung.getSo_dien_thoai();
        this.lop = nguoiDung.getLop();
        this.khoa = nguoiDung.getKhoa();
        if (docGia != null) {
            this.maTheDocGia = docGia.getMaTheDocGia();
            this.ngayCap = docGia.getNgayCap();
            this.ngayHetHan = docGia.getNgayHetHan();
        }
    }

    public String getMa_sv() {
        return ma_sv;
    }

    public void setMa_sv(String ma_sv) {
        this.ma_sv = ma_sv;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public void setHo_ten(String ho_ten) {
        this.ho_ten = ho_ten;
    }

    public String getSo_dien_thoai() {
        return so_dien_thoai;
    }

    public void setSo_dien_thoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai;
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

    public String getMaTheDocGia() {
        return maTheDocGia;
    }

    public void setMaTheDocGia(String maTheDocGia) {
        this.maTheDocGia = maTheDocGia;
    }

    public LocalDate getNgayCap() {
        return ngayCap;
    }

    public void setNgayCap(LocalDate ngayCap) {
        this.ngayCap = ngayCap;
    }

    public LocalDate getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDate ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }
}
