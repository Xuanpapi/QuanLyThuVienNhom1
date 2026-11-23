package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TacGia")
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_tac_gia", nullable = false, length = 150)
    private String tenTacGia;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    public TacGia() {}

    public TacGia(String tenTacGia, String moTa) {
        this.tenTacGia = tenTacGia;
        this.moTa = moTa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenTacGia() {
        return tenTacGia;
    }

    public void setTenTacGia(String tenTacGia) {
        this.tenTacGia = tenTacGia;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
