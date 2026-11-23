-- =========================================================
-- TEST DATA FOR PostgreSQL
-- Chạy sau create_postSQL.sql
-- =========================================================

-- =========================================================
-- 1. VAI TRÒ
-- =========================================================

INSERT INTO "VaiTro" (ten_vai_tro)
VALUES
    ('QUAN_TRI'),
    ('DOC_GIA')
ON CONFLICT (ten_vai_tro) DO NOTHING;

-- =========================================================
-- 2. NHÀ XUẤT BẢN
-- =========================================================

INSERT INTO "NhaXuatBan"
    (ten_nha_xuat_ban, dia_chi, so_dien_thoai)
VALUES
    ('Nhà xuất bản Kim Đồng', 'Hà Nội', '0241234567'),
    ('Nhà xuất bản Giáo dục Việt Nam', 'Hà Nội', '0242345678'),
    ('Nhà xuất bản Trẻ', 'TP. Hồ Chí Minh', '0283456789'),
    ('Nhà xuất bản Khoa học và Kỹ thuật', 'Hà Nội', '0244567890'),
    ('Nhà xuất bản Thông tin và Truyền thông', 'Hà Nội', '0245678901');

-- =========================================================
-- 3. DANH MỤC
-- =========================================================

INSERT INTO "DanhMuc"
    (ten_danh_muc, mo_ta)
VALUES
    ('Công nghệ thông tin', 'Sách về công nghệ thông tin và máy tính'),
    ('Khoa học', 'Sách về khoa học tự nhiên và khoa học ứng dụng'),
    ('Văn học', 'Tác phẩm văn học Việt Nam và thế giới'),
    ('Kinh tế', 'Sách về kinh tế, tài chính và quản trị'),
    ('Kỹ năng', 'Sách phát triển kỹ năng và tư duy');

-- =========================================================
-- 4. THỂ LOẠI
-- =========================================================

INSERT INTO "TheLoai" (ten_the_loai)
VALUES
    ('Lập trình'),
    ('Cơ sở dữ liệu'),
    ('Mạng máy tính'),
    ('An toàn thông tin'),
    ('Trí tuệ nhân tạo'),
    ('Tiểu thuyết'),
    ('Truyện ngắn'),
    ('Kinh tế học'),
    ('Quản trị kinh doanh'),
    ('Kỹ năng mềm');

-- =========================================================
-- 5. TÁC GIẢ
-- =========================================================

INSERT INTO "TacGia"
    (ten_tac_gia, mo_ta)
VALUES
    ('Robert C. Martin', 'Tác giả nổi tiếng về kỹ nghệ phần mềm'),
    ('Andrew S. Tanenbaum', 'Tác giả nhiều sách về hệ điều hành và mạng máy tính'),
    ('Abraham Silberschatz', 'Tác giả sách về hệ quản trị cơ sở dữ liệu'),
    ('Kevin Mitnick', 'Tác giả và chuyên gia về an toàn thông tin'),
    ('George Orwell', 'Tác giả văn học nổi tiếng'),
    ('Nguyễn Nhật Ánh', 'Nhà văn Việt Nam'),
    ('Daniel Kahneman', 'Tác giả nghiên cứu về kinh tế học hành vi'),
    ('Dale Carnegie', 'Tác giả sách phát triển kỹ năng');

-- =========================================================
-- 6. SÁCH
-- =========================================================

INSERT INTO "Sach"
    (ma_sach, ten_sach, mo_ta, nam_xuat_ban, nha_xuat_ban_id, danh_muc_id)
VALUES
    ('S001', 'Clean Code',
     'Các nguyên tắc và thực hành viết mã nguồn sạch, dễ đọc và dễ bảo trì.',
     2020, 4, 1),
    ('S002', 'Computer Networks',
     'Kiến thức nền tảng về mạng máy tính, giao thức và kiến trúc mạng.',
     2019, 4, 1),
    ('S003', 'Database System Concepts',
     'Các khái niệm cơ bản và nâng cao về hệ quản trị cơ sở dữ liệu.',
     2021, 2, 1),
    ('S004', 'An toàn thông tin căn bản',
     'Giới thiệu các khái niệm cơ bản về bảo mật và an toàn thông tin.',
     2022, 5, 1),
    ('S005', 'Artificial Intelligence',
     'Nhập môn trí tuệ nhân tạo và các phương pháp giải quyết bài toán.',
     2023, 4, 1),
    ('S006', 'Tuổi trẻ đáng giá bao nhiêu',
     'Những câu chuyện và góc nhìn về tuổi trẻ, học tập và phát triển bản thân.',
     2018, 3, 5),
    ('S007', 'Mắt biếc',
     'Một trong những tiểu thuyết nổi tiếng của Nguyễn Nhật Ánh.',
     2019, 3, 3),
    ('S008', '1984',
     'Tiểu thuyết phản địa đàng nổi tiếng của George Orwell.',
     2020, 1, 3),
    ('S009', 'Tư duy nhanh và chậm',
     'Khám phá cách con người suy nghĩ và đưa ra quyết định.',
     2021, 3, 4),
    ('S010', 'Đắc nhân tâm',
     'Những nguyên tắc giao tiếp và xây dựng mối quan hệ với người khác.',
     2017, 1, 5),
    ('S011', 'Clean Architecture',
     'Các nguyên tắc thiết kế kiến trúc phần mềm có khả năng bảo trì và mở rộng.',
     2022, 4, 1),
    ('S012', 'Operating System Concepts',
     'Kiến thức nền tảng về hệ điều hành và quản lý tài nguyên máy tính.',
     2020, 4, 1);

-- =========================================================
-- 7. SÁCH - TÁC GIẢ
-- =========================================================

INSERT INTO "Sach_TacGia" (sach_id, tac_gia_id)
VALUES
    (1, 1), (2, 2), (3, 3), (4, 4),
    (5, 3), (6, 6), (7, 6), (8, 5),
    (9, 7), (10, 8), (11, 1), (12, 2);

-- =========================================================
-- 8. SÁCH - THỂ LOẠI
-- =========================================================

INSERT INTO "Sach_TheLoai" (sach_id, the_loai_id)
VALUES
    (1, 1), (1, 10),
    (2, 3),
    (3, 2), (3, 1),
    (4, 4),
    (5, 5), (5, 1),
    (6, 10),
    (7, 6),
    (8, 6),
    (9, 8),
    (10, 10),
    (11, 1),
    (12, 1), (12, 3);

-- =========================================================
-- 9. CUỐN SÁCH
-- =========================================================

INSERT INTO "CuonSach"
    (sach_id, ma_cuon_sach, tinh_trang, trang_thai)
VALUES
    (1, 'CS001', 'TỐT', 'CÓ_SẴN'),
    (1, 'CS002', 'TỐT', 'CÓ_SẴN'),
    (1, 'CS003', 'CŨ', 'CÓ_SẴN'),
    (2, 'CS004', 'TỐT', 'ĐANG_MƯỢN'),
    (2, 'CS005', 'TỐT', 'CÓ_SẴN'),
    (3, 'CS006', 'TỐT', 'CÓ_SẴN'),
    (3, 'CS007', 'TỐT', 'CÓ_SẴN'),
    (4, 'CS008', 'TỐT', 'CÓ_SẴN'),
    (4, 'CS009', 'TỐT', 'ĐANG_MƯỢN'),
    (5, 'CS010', 'TỐT', 'CÓ_SẴN'),
    (6, 'CS011', 'TỐT', 'CÓ_SẴN'),
    (6, 'CS012', 'TỐT', 'CÓ_SẴN'),
    (7, 'CS013', 'TỐT', 'CÓ_SẴN'),
    (7, 'CS014', 'CŨ', 'CÓ_SẴN'),
    (8, 'CS015', 'TỐT', 'CÓ_SẴN'),
    (9, 'CS016', 'TỐT', 'CÓ_SẴN'),
    (10, 'CS017', 'TỐT', 'CÓ_SẴN'),
    (10, 'CS018', 'TỐT', 'CÓ_SẴN'),
    (11, 'CS019', 'MỚI', 'CÓ_SẴN'),
    (12, 'CS020', 'TỐT', 'CÓ_SẴN'),
    (12, 'CS021', 'TỐT', 'CÓ_SẴN');

-- =========================================================
-- 10. NGƯỜI DÙNG
-- =========================================================

INSERT INTO "NguoiDung"
    (ma_sv, email, mat_khau, ho_ten, trang_thai, vai_tro_id,
     so_dien_thoai, lop, khoa)
VALUES
    ('ADMIN01', 'admin@library.com', '123456', 'Quản trị viên',
     TRUE, 1, '0901234567', NULL, 'Ban Quản Trị'),
    ('SV001', 'nguyenvana@student.com', '123456', 'Nguyễn Văn A',
     TRUE, 2, '0912345678', 'CNTT01', 'Công nghệ thông tin'),
    ('SV002', 'tranthib@student.com', '123456', 'Trần Thị B',
     TRUE, 2, '0923456789', 'CNTT02', 'Công nghệ thông tin');

-- =========================================================
-- 11. ĐỘC GIẢ
-- =========================================================

INSERT INTO "DocGia"
    (nguoi_dung_id, ma_the_doc_gia, ngay_cap, ngay_het_han)
VALUES
    (2, 'DG001', '2023-09-01', '2025-09-01'),
    (3, 'DG002', '2023-09-01', '2025-09-01');

-- =========================================================
-- 12. LỊCH SỬ ĐĂNG NHẬP
-- Bổ sung dữ liệu mẫu vì insert.sql gốc chưa có phần này.
-- =========================================================

INSERT INTO "LichSuDangNhap"
    (nguoi_dung_id, thoi_gian_dang_nhap, dia_chi_ip, trang_thai)
VALUES
    (1, '2026-03-01 08:00:00', '127.0.0.1', TRUE),
    (2, '2026-03-01 08:15:00', '127.0.0.1', TRUE),
    (3, '2026-03-01 08:20:00', '127.0.0.1', TRUE);

-- =========================================================
-- 13. PHIẾU MƯỢN
-- Dữ liệu mẫu cho Sprint 2:
--   #1 Đã trả (kèm phiếu trả + phiếu phạt mẫu)
--   #2 Đang mượn (test nghiệp vụ trả sách)
--   #3 Đang mượn + quá hạn (chuẩn bị Sprint 3)
--   #4 Chờ duyệt (test duyệt/từ chối phiếu mượn online)
-- =========================================================

INSERT INTO "PhieuMuon"
    (doc_gia_id, nguoi_lap_id, ngay_muon, han_tra, so_lan_gia_han, trang_thai)
VALUES
    (1, 1, '2026-03-01 08:30:00', '2026-03-15', 0, 'DA_TRA'),
    (2, 1, '2026-04-01 09:00:00', '2026-04-15', 0, 'DANG_MUON'),
    (1, 1, '2026-02-01 09:00:00', '2026-02-15', 0, 'DANG_MUON'),
    (2, 3, '2026-04-02 10:00:00', '2026-04-16', 0, 'CHO_DUYET');

-- =========================================================
-- 14. CHI TIẾT PHIẾU MƯỢN
-- =========================================================

INSERT INTO "ChiTietPhieuMuon" (phieu_muon_id, cuon_sach_id)
VALUES
    (1, 2),
    (1, 7),
    (2, 4),
    (3, 9),
    (4, 10);

-- =========================================================
-- 15. PHIẾU TRẢ
-- =========================================================

INSERT INTO "PhieuTra"
    (phieu_muon_id, nguoi_lap_id, ngay_tra)
VALUES
    (1, 1, '2026-03-20 10:00:00');

-- =========================================================
-- 16. CHI TIẾT PHIẾU TRẢ
-- =========================================================

INSERT INTO "ChiTietPhieuTra"
    (phieu_tra_id, cuon_sach_id, tinh_trang_khi_tra)
VALUES
    (1, 2, 'TỐT'),
    (1, 7, 'TỐT');

-- =========================================================
-- 17. PHIẾU PHẠT
-- =========================================================

INSERT INTO "PhieuPhat"
    (phieu_muon_id, doc_gia_id, so_tien, ly_do, trang_thai_thanh_toan,
     ngay_tao, ngay_thanh_toan)
VALUES
    (1, 1, 50000.00, 'Trả sách trễ hạn', 'CHUA_THANH_TOAN',
     '2026-03-16 09:00:00', NULL);
