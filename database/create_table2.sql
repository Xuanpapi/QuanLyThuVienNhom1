-- =========================================================
-- 1. BẢNG VAI TRÒ
-- =========================================================

CREATE TABLE VaiTro (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_vai_tro VARCHAR(50) NOT NULL UNIQUE
);
GO

INSERT INTO VaiTro (ten_vai_tro)
VALUES
('QUAN_TRI'),
('THU_THU'),
('DOC_GIA');
GO


-- =========================================================
-- 2. BẢNG NGƯỜI DÙNG
-- =========================================================

CREATE TABLE NguoiDung (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_sv VARCHAR(20) NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    vai_tro_id INT NOT NULL,
	so_dien_thoai VARCHAR(15) NULL,
    lop VARCHAR(50) NULL,
    khoa NVARCHAR(100) NULL;

    CONSTRAINT FK_NguoiDung_VaiTro
        FOREIGN KEY (vai_tro_id)
        REFERENCES VaiTro(id)
);
GO


-- =========================================================
-- 3. LỊCH SỬ ĐĂNG NHẬP
-- =========================================================

CREATE TABLE LichSuDangNhap (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nguoi_dung_id INT NOT NULL,
    thoi_gian_dang_nhap DATETIME2 NOT NULL DEFAULT GETDATE(),
    dia_chi_ip VARCHAR(45) NULL,
    trang_thai BIT NOT NULL,

    CONSTRAINT FK_LichSuDangNhap_NguoiDung
        FOREIGN KEY (nguoi_dung_id)
        REFERENCES NguoiDung(id)
);
GO


-- =========================================================
-- 4. BẢNG ĐỘC GIẢ
-- =========================================================

CREATE TABLE DocGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nguoi_dung_id INT NOT NULL UNIQUE,
    ma_the_doc_gia VARCHAR(20) NOT NULL UNIQUE,
    ngay_cap DATE NULL,
    ngay_het_han DATE NULL,

    CONSTRAINT FK_DocGia_NguoiDung
        FOREIGN KEY (nguoi_dung_id)
        REFERENCES NguoiDung(id)
);
GO


-- =========================================================
-- 5. BẢNG NHÀ XUẤT BẢN
-- =========================================================

CREATE TABLE NhaXuatBan (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_nha_xuat_ban NVARCHAR(150) NOT NULL,
    dia_chi NVARCHAR(255) NULL,
    so_dien_thoai VARCHAR(20) NULL
);
GO


-- =========================================================
-- 6. BẢNG DANH MỤC
-- =========================================================

CREATE TABLE DanhMuc (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_danh_muc NVARCHAR(100) NOT NULL UNIQUE,
    mo_ta NVARCHAR(255) NULL
);
GO


-- =========================================================
-- 7. BẢNG THỂ LOẠI
-- =========================================================

CREATE TABLE TheLoai (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_the_loai NVARCHAR(100) NOT NULL UNIQUE
);
GO


-- =========================================================
-- 8. BẢNG TÁC GIẢ
-- =========================================================

CREATE TABLE TacGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_tac_gia NVARCHAR(150) NOT NULL,
    mo_ta NVARCHAR(255) NULL
);
GO


-- =========================================================
-- 9. BẢNG SÁCH
-- =========================================================

CREATE TABLE Sach (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_sach VARCHAR(30) NOT NULL UNIQUE,
    ten_sach NVARCHAR(255) NOT NULL,
    mo_ta NVARCHAR(MAX) NULL,
    nam_xuat_ban INT NULL,
    nha_xuat_ban_id INT NULL,
    danh_muc_id INT NULL,

    CONSTRAINT FK_Sach_NhaXuatBan
        FOREIGN KEY (nha_xuat_ban_id)
        REFERENCES NhaXuatBan(id),

    CONSTRAINT FK_Sach_DanhMuc
        FOREIGN KEY (danh_muc_id)
        REFERENCES DanhMuc(id)
);
GO


-- =========================================================
-- 10. BẢNG SÁCH - TÁC GIẢ
-- =========================================================

CREATE TABLE Sach_TacGia (
    sach_id INT NOT NULL,
    tac_gia_id INT NOT NULL,

    PRIMARY KEY (sach_id, tac_gia_id),

    CONSTRAINT FK_Sach_TacGia_Sach
        FOREIGN KEY (sach_id)
        REFERENCES Sach(id),

    CONSTRAINT FK_Sach_TacGia_TacGia
        FOREIGN KEY (tac_gia_id)
        REFERENCES TacGia(id)
);
GO


-- =========================================================
-- 11. BẢNG SÁCH - THỂ LOẠI
-- =========================================================

CREATE TABLE Sach_TheLoai (
    sach_id INT NOT NULL,
    the_loai_id INT NOT NULL,

    PRIMARY KEY (sach_id, the_loai_id),

    CONSTRAINT FK_Sach_TheLoai_Sach
        FOREIGN KEY (sach_id)
        REFERENCES Sach(id),

    CONSTRAINT FK_Sach_TheLoai_TheLoai
        FOREIGN KEY (the_loai_id)
        REFERENCES TheLoai(id)
);
GO


-- =========================================================
-- 12. BẢNG CUỐN SÁCH
-- =========================================================

CREATE TABLE CuonSach (
    id INT IDENTITY(1,1) PRIMARY KEY,
    sach_id INT NOT NULL,
    ma_cuon_sach VARCHAR(30) NOT NULL UNIQUE,
    tinh_trang NVARCHAR(50) NOT NULL DEFAULT N'TỐT',
    trang_thai NVARCHAR(50) NOT NULL DEFAULT N'CÓ_SẴN',

    CONSTRAINT FK_CuonSach_Sach
        FOREIGN KEY (sach_id)
        REFERENCES Sach(id)
);
GO


-- =========================================================
-- 13. PHIẾU MƯỢN
-- =========================================================

CREATE TABLE PhieuMuon (
    id INT IDENTITY(1,1) PRIMARY KEY,
    doc_gia_id INT NOT NULL,
    nguoi_lap_id INT NOT NULL,
    ngay_muon DATETIME2 NOT NULL DEFAULT GETDATE(),
    han_tra DATE NOT NULL,
    so_lan_gia_han INT NOT NULL DEFAULT 0,
    trang_thai NVARCHAR(50) NOT NULL DEFAULT N'DANG_MUON',

    CONSTRAINT FK_PhieuMuon_DocGia
        FOREIGN KEY (doc_gia_id)
        REFERENCES DocGia(id),

    CONSTRAINT FK_PhieuMuon_NguoiLap
        FOREIGN KEY (nguoi_lap_id)
        REFERENCES NguoiDung(id)
);
GO


-- =========================================================
-- 14. CHI TIẾT PHIẾU MƯỢN
-- =========================================================

CREATE TABLE ChiTietPhieuMuon (
    id INT IDENTITY(1,1) PRIMARY KEY,
    phieu_muon_id INT NOT NULL,
    cuon_sach_id INT NOT NULL,

    CONSTRAINT FK_ChiTietPhieuMuon_PhieuMuon
        FOREIGN KEY (phieu_muon_id)
        REFERENCES PhieuMuon(id),

    CONSTRAINT FK_ChiTietPhieuMuon_CuonSach
        FOREIGN KEY (cuon_sach_id)
        REFERENCES CuonSach(id),

    CONSTRAINT UQ_ChiTietPhieuMuon
        UNIQUE (phieu_muon_id, cuon_sach_id)
);
GO


-- =========================================================
-- 15. PHIẾU TRẢ
-- =========================================================

CREATE TABLE PhieuTra (
    id INT IDENTITY(1,1) PRIMARY KEY,
    phieu_muon_id INT NOT NULL,
    nguoi_lap_id INT NOT NULL,
    ngay_tra DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_PhieuTra_PhieuMuon
        FOREIGN KEY (phieu_muon_id)
        REFERENCES PhieuMuon(id),

    CONSTRAINT FK_PhieuTra_NguoiLap
        FOREIGN KEY (nguoi_lap_id)
        REFERENCES NguoiDung(id)
);
GO


-- =========================================================
-- 16. CHI TIẾT PHIẾU TRẢ
-- =========================================================

CREATE TABLE ChiTietPhieuTra (
    id INT IDENTITY(1,1) PRIMARY KEY,
    phieu_tra_id INT NOT NULL,
    cuon_sach_id INT NOT NULL,
    tinh_trang_khi_tra NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_ChiTietPhieuTra_PhieuTra
        FOREIGN KEY (phieu_tra_id)
        REFERENCES PhieuTra(id),

    CONSTRAINT FK_ChiTietPhieuTra_CuonSach
        FOREIGN KEY (cuon_sach_id)
        REFERENCES CuonSach(id)
);
GO


-- =========================================================
-- 17. PHIẾU PHẠT
-- =========================================================

CREATE TABLE PhieuPhat (
    id INT IDENTITY(1,1) PRIMARY KEY,
    phieu_muon_id INT NOT NULL,
    doc_gia_id INT NOT NULL,
    so_tien DECIMAL(12,2) NOT NULL,
    ly_do NVARCHAR(255) NOT NULL,
    trang_thai_thanh_toan NVARCHAR(50) NOT NULL DEFAULT N'CHUA_THANH_TOAN',
    ngay_tao DATETIME2 NOT NULL DEFAULT GETDATE(),
    ngay_thanh_toan DATETIME2 NULL,

    CONSTRAINT FK_PhieuPhat_PhieuMuon
        FOREIGN KEY (phieu_muon_id)
        REFERENCES PhieuMuon(id),

    CONSTRAINT FK_PhieuPhat_DocGia
        FOREIGN KEY (doc_gia_id)
        REFERENCES DocGia(id)
);
GO