package com.example.backend.dto;

import com.example.backend.common.TrangThaiPhieuMuon;
import com.example.backend.entity.ChiTietPhieuMuon;
import com.example.backend.entity.ChiTietPhieuTra;
import com.example.backend.entity.PhieuMuon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PhieuMuonResponseDTO {
    private Integer phieuMuonId;
    private Integer docGiaId;
    private String maTheDocGia;
    private String tenDocGia;
    private String emailDocGia;
    private LocalDateTime ngayMuon;
    private LocalDate hanTra;
    private LocalDateTime ngayTraThucTe;
    private Integer soLanGiaHan;
    private String trangThai;
    private Integer soNgayQuaHan;
    private List<ChiTietSachDTO> danhSachSach = new ArrayList<>();

    public PhieuMuonResponseDTO() {}

    public PhieuMuonResponseDTO(PhieuMuon phieuMuon) {
        this(phieuMuon, List.of());
    }

    public PhieuMuonResponseDTO(PhieuMuon phieuMuon, List<ChiTietPhieuTra> danhSachDaTra) {
        this.phieuMuonId = phieuMuon.getId();
        this.docGiaId = phieuMuon.getDocGiaId();
        this.ngayMuon = phieuMuon.getNgayMuon();
        this.hanTra = phieuMuon.getHanTra();
        this.soLanGiaHan = phieuMuon.getSoLanGiaHan();
        this.trangThai = phieuMuon.getTrangThai();

        if (phieuMuon.getDocGia() != null) {
            this.maTheDocGia = phieuMuon.getDocGia().getMaTheDocGia();
            if (phieuMuon.getDocGia().getNguoiDung() != null) {
                this.tenDocGia = phieuMuon.getDocGia().getNguoiDung().getHo_ten();
                this.emailDocGia = phieuMuon.getDocGia().getNguoiDung().getEmail();
            }
        }

        Map<Integer, ChiTietPhieuTra> daTraTheoCuon = new HashMap<>();
        LocalDateTime ngayTraThucTe = null;
        if (danhSachDaTra != null) {
            for (ChiTietPhieuTra chiTietTra : danhSachDaTra) {
                if (chiTietTra.getCuonSachId() != null) {
                    daTraTheoCuon.putIfAbsent(chiTietTra.getCuonSachId(), chiTietTra);
                }
                if (chiTietTra.getPhieuTra() != null && chiTietTra.getPhieuTra().getNgayTra() != null) {
                    LocalDateTime ngayTra = chiTietTra.getPhieuTra().getNgayTra();
                    if (ngayTraThucTe == null || ngayTra.isAfter(ngayTraThucTe)) {
                        ngayTraThucTe = ngayTra;
                    }
                }
            }
        }
        this.ngayTraThucTe = ngayTraThucTe;

        // US 3.2.2 - số ngày quá hạn (0 nếu chưa quá hạn hoặc phiếu không còn đang mượn)
        if (TrangThaiPhieuMuon.DANG_MUON.equals(phieuMuon.getTrangThai())
                && phieuMuon.getHanTra() != null
                && phieuMuon.getHanTra().isBefore(LocalDate.now())) {
            this.soNgayQuaHan = (int) ChronoUnit.DAYS.between(phieuMuon.getHanTra(), LocalDate.now());
        } else {
            this.soNgayQuaHan = 0;
        }

        if (phieuMuon.getChiTietPhieuMuons() != null) {
            List<ChiTietPhieuMuon> dsChiTiet = phieuMuon.getChiTietPhieuMuons().stream()
                    .sorted(Comparator.comparing(ct -> ct.getCuonSach() != null && ct.getCuonSach().getMaCuonSach() != null
                            ? ct.getCuonSach().getMaCuonSach() : ""))
                    .collect(Collectors.toList());

            for (ChiTietPhieuMuon chiTiet : dsChiTiet) {
                ChiTietSachDTO dto = new ChiTietSachDTO(
                        chiTiet.getCuonSach() != null ? chiTiet.getCuonSach().getMaCuonSach() : null,
                        chiTiet.getCuonSach() != null && chiTiet.getCuonSach().getSach() != null
                                ? chiTiet.getCuonSach().getSach().getMaSach() : null,
                        chiTiet.getCuonSach() != null && chiTiet.getCuonSach().getSach() != null
                                ? chiTiet.getCuonSach().getSach().getTenSach() : null
                );
                dto.setCuonSachId(chiTiet.getCuonSachId());
                if (chiTiet.getCuonSach() != null) {
                    dto.setTinhTrang(chiTiet.getCuonSach().getTinhTrang());
                    dto.setTrangThaiCuon(chiTiet.getCuonSach().getTrangThai());
                }

                ChiTietPhieuTra phieuTra = daTraTheoCuon.get(chiTiet.getCuonSachId());
                if (phieuTra != null) {
                    dto.setDaTra(true);
                    dto.setTinhTrangKhiTra(phieuTra.getTinhTrangKhiTra());
                    if (phieuTra.getPhieuTra() != null) {
                        dto.setNgayTra(phieuTra.getPhieuTra().getNgayTra());
                    }
                }
                this.danhSachSach.add(dto);
            }
        }
    }

    public static class ChiTietSachDTO {
        private Integer cuonSachId;
        private String maCuonSach;
        private String maSach;
        private String tenSach;
        private String tinhTrang;
        private String trangThaiCuon;
        private boolean daTra;
        private LocalDateTime ngayTra;
        private String tinhTrangKhiTra;

        public ChiTietSachDTO(String maCuonSach, String maSach, String tenSach) {
            this.maCuonSach = maCuonSach;
            this.maSach = maSach;
            this.tenSach = tenSach;
        }

        public Integer getCuonSachId() {
            return cuonSachId;
        }

        public void setCuonSachId(Integer cuonSachId) {
            this.cuonSachId = cuonSachId;
        }

        public String getMaCuonSach() {
            return maCuonSach;
        }

        public void setMaCuonSach(String maCuonSach) {
            this.maCuonSach = maCuonSach;
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

        public String getTinhTrang() {
            return tinhTrang;
        }

        public void setTinhTrang(String tinhTrang) {
            this.tinhTrang = tinhTrang;
        }

        public String getTrangThaiCuon() {
            return trangThaiCuon;
        }

        public void setTrangThaiCuon(String trangThaiCuon) {
            this.trangThaiCuon = trangThaiCuon;
        }

        public boolean isDaTra() {
            return daTra;
        }

        public void setDaTra(boolean daTra) {
            this.daTra = daTra;
        }

        public LocalDateTime getNgayTra() {
            return ngayTra;
        }

        public void setNgayTra(LocalDateTime ngayTra) {
            this.ngayTra = ngayTra;
        }

        public String getTinhTrangKhiTra() {
            return tinhTrangKhiTra;
        }

        public void setTinhTrangKhiTra(String tinhTrangKhiTra) {
            this.tinhTrangKhiTra = tinhTrangKhiTra;
        }
    }

    public Integer getPhieuMuonId() {
        return phieuMuonId;
    }

    public void setPhieuMuonId(Integer phieuMuonId) {
        this.phieuMuonId = phieuMuonId;
    }

    public Integer getDocGiaId() {
        return docGiaId;
    }

    public void setDocGiaId(Integer docGiaId) {
        this.docGiaId = docGiaId;
    }

    public String getMaTheDocGia() {
        return maTheDocGia;
    }

    public void setMaTheDocGia(String maTheDocGia) {
        this.maTheDocGia = maTheDocGia;
    }

    public String getTenDocGia() {
        return tenDocGia;
    }

    public void setTenDocGia(String tenDocGia) {
        this.tenDocGia = tenDocGia;
    }

    public String getEmailDocGia() {
        return emailDocGia;
    }

    public void setEmailDocGia(String emailDocGia) {
        this.emailDocGia = emailDocGia;
    }

    public LocalDateTime getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(LocalDateTime ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public LocalDate getHanTra() {
        return hanTra;
    }

    public void setHanTra(LocalDate hanTra) {
        this.hanTra = hanTra;
    }

    public LocalDateTime getNgayTraThucTe() {
        return ngayTraThucTe;
    }

    public void setNgayTraThucTe(LocalDateTime ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }

    public Integer getSoLanGiaHan() {
        return soLanGiaHan;
    }

    public void setSoLanGiaHan(Integer soLanGiaHan) {
        this.soLanGiaHan = soLanGiaHan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getSoNgayQuaHan() {
        return soNgayQuaHan;
    }

    public void setSoNgayQuaHan(Integer soNgayQuaHan) {
        this.soNgayQuaHan = soNgayQuaHan;
    }

    public List<ChiTietSachDTO> getDanhSachSach() {
        return danhSachSach;
    }

    public void setDanhSachSach(List<ChiTietSachDTO> danhSachSach) {
        this.danhSachSach = danhSachSach;
    }
}
