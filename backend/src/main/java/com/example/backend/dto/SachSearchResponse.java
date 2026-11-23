package com.example.backend.dto;

import com.example.backend.entity.Sach;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SachSearchResponse {
    private Integer id;
    private String maSach;
    private String tenSach;
    private Integer namXuatBan;
    private Set<String> tacGias = new HashSet<>();
    private Set<String> theLoais = new HashSet<>();

    public SachSearchResponse() {}

    public SachSearchResponse(Sach sach) {
        this.id = sach.getId();
        this.maSach = sach.getMaSach();
        this.tenSach = sach.getTenSach();
        this.namXuatBan = sach.getNamXuatBan();
        if (sach.getTacGias() != null) {
            this.tacGias = sach.getTacGias().stream()
                    .map(t -> t.getTenTacGia())
                    .collect(Collectors.toSet());
        }
        if (sach.getTheLoais() != null) {
            this.theLoais = sach.getTheLoais().stream()
                    .map(th -> th.getTenTheLoai())
                    .collect(Collectors.toSet());
        }
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

    public Integer getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(Integer namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public Set<String> getTacGias() {
        return tacGias;
    }

    public void setTacGias(Set<String> tacGias) {
        this.tacGias = tacGias;
    }

    public Set<String> getTheLoais() {
        return theLoais;
    }

    public void setTheLoais(Set<String> theLoais) {
        this.theLoais = theLoais;
    }
}
