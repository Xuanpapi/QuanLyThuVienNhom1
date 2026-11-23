package com.example.backend.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Sach")
public class Sach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_sach", unique = true, length = 30)
    private String maSach;

    @Column(name = "ten_sach", nullable = false, length = 255)
    private String tenSach;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "nha_xuat_ban_id")
    private Integer nhaXuatBanId;

    @Column(name = "danh_muc_id")
    private Integer danhMucId;

    @ManyToMany
    @JoinTable(
        name = "Sach_TacGia",
        joinColumns = @JoinColumn(name = "sach_id"),
        inverseJoinColumns = @JoinColumn(name = "tac_gia_id")
    )
    private Set<TacGia> tacGias = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "Sach_TheLoai",
        joinColumns = @JoinColumn(name = "sach_id"),
        inverseJoinColumns = @JoinColumn(name = "the_loai_id")
    )
    private Set<TheLoai> theLoais = new HashSet<>();

    // Field tạm dùng để nhận dữ liệu NXB / Danh mục mới từ client (không map xuống DB)
    @Transient
    private NhaXuatBan nhaXuatBan;

    @Transient
    private DanhMuc danhMuc;

    public Sach() {}

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

    public NhaXuatBan getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(NhaXuatBan nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }
}
