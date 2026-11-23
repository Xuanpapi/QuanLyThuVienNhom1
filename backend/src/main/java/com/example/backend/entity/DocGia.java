package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DocGia")
public class DocGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nguoi_dung_id", nullable = false, unique = true)
    private Integer nguoiDungId;

    @Column(name = "ma_the_doc_gia", nullable = false, unique = true, length = 20)
    private String maTheDocGia;

    @Column(name = "ngay_cap")
    private LocalDate ngayCap;

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", insertable = false, updatable = false)
    private NguoiDung nguoiDung;

    public DocGia() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Integer nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
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

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }
}
