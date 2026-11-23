package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TheLoai")
public class TheLoai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_the_loai", nullable = false, unique = true, length = 100)
    private String tenTheLoai;

    public TheLoai() {}

    public TheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }
}
