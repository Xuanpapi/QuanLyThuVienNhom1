package com.example.backend.dto;

import com.example.backend.entity.DocGia;
import com.example.backend.entity.NguoiDung;

import java.time.LocalDate;

/**
 * DTO trả về cho màn hình Quản lý người dùng (US 1.2.1).
 * Không chứa mat_khau để tránh rò rỉ thông tin.
 */
public class NguoiDungAdminResponseDTO {
    private Integer id;
    private String maSv;
    private String email;
    private String hoTen;
    private Integer vaiTroId;
    private String tenVaiTro;
    private Boolean trangThai;
    private String soDienThoai;
    private String lop;
    private String khoa;
    private String maTheDocGia;
    private LocalDate ngayCapThe;
    private LocalDate ngayHetHanThe;

    public NguoiDungAdminResponseDTO(NguoiDung nguoiDung, DocGia docGia) {
        this.id = nguoiDung.getId();
        this.maSv = nguoiDung.getMasv();
        this.email = nguoiDung.getEmail();
        this.hoTen = nguoiDung.getHo_ten();
        this.trangThai = nguoiDung.getTrang_thai();
        this.soDienThoai = nguoiDung.getSo_dien_thoai();
        this.lop = nguoiDung.getLop();
        this.khoa = nguoiDung.getKhoa();
        if (nguoiDung.getVai_tro_id() != null) {
            this.vaiTroId = nguoiDung.getVai_tro_id().getId();
            this.tenVaiTro = nguoiDung.getVai_tro_id().getTenVaiTro();
        }
        if (docGia != null) {
            this.maTheDocGia = docGia.getMaTheDocGia();
            this.ngayCapThe = docGia.getNgayCap();
            this.ngayHetHanThe = docGia.getNgayHetHan();
        }
    }

    public Integer getId() {
        return id;
    }

    public String getMaSv() {
        return maSv;
    }

    public String getEmail() {
        return email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public Integer getVaiTroId() {
        return vaiTroId;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getLop() {
        return lop;
    }

    public String getKhoa() {
        return khoa;
    }

    public String getMaTheDocGia() {
        return maTheDocGia;
    }

    public LocalDate getNgayCapThe() {
        return ngayCapThe;
    }

    public LocalDate getNgayHetHanThe() {
        return ngayHetHanThe;
    }
}
