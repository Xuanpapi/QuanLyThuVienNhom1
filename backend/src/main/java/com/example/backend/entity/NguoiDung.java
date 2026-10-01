package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name= "NguoiDung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_sv", unique = true, length = 20)
    private String ma_sv;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mat_khau", nullable = false, length = 255)
    private String mat_khau;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String ho_ten;

    @ManyToOne
    @JoinColumn(name = "vai_tro_id", nullable = false)
    private VaiTro vai_tro_id;


    @Column(name = "trang_thai", nullable = false)
    private Boolean trang_thai;

    @Column(name = "so_dien_thoai", length = 15)
    private String so_dien_thoai;

    @Column(name = "lop", length = 50)
    private String lop;

    @Column(name = "khoa", length = 100)
    private String khoa;



    public NguoiDung(){}

    public NguoiDung(String ma_sv, String email, String mat_khau, String ho_ten,String lop,String sdt,String khoa) {
        this.ma_sv = ma_sv;
        this.email = email;
        this.mat_khau = mat_khau;
        this.ho_ten = ho_ten;
        this.lop = lop;
        this.so_dien_thoai = sdt;
        this.khoa = khoa;
    }

    //Getter va Setter
    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getMasv() {
        return ma_sv;
    }

    public void setMasv(String ma_sv) {
        this.ma_sv = ma_sv;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMat_khau() {
        return mat_khau;
    }

    public void setMat_khau(String mat_khau) {
        this.mat_khau = mat_khau;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public void setHo_ten(String ho_ten) {
        this.ho_ten = ho_ten;
    }

    public VaiTro getVai_tro_id() {
        return vai_tro_id;
    }

    public void setVai_tro_id(VaiTro vai_tro_id) {
        this.vai_tro_id = vai_tro_id;
    }

    public Boolean getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(Boolean trang_thai) {
        this.trang_thai = trang_thai;
    }

    // Getter và Setter cho so_dien_thoai
    public String getSo_dien_thoai() {
        return so_dien_thoai;
    }
    public void setSo_dien_thoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai;
    }


    // Getter và Setter cho lop
    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }


    // Getter và Setter cho khoa
    public String getKhoa() {
        return khoa;
    }
    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

}
