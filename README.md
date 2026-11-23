# QuanLyThuVienNhom1 — Hệ thống quản lý thư viện

Ứng dụng quản lý thư viện gồm giao diện độc giả (tra cứu, mượn online, xem lịch sử, đổi thông tin cá nhân) và trang thủ thư (quản lý sách, duyệt/trả phiếu mượn, xử lý phiếu phạt, gửi thông báo). Đăng ký, quên mật khẩu và đổi email/SĐT đều xác thực bằng OTP gửi qua Gmail.

## Công nghệ

- **Backend:** Spring Boot 4.1.1, Java 21, Spring Data JPA, Spring Mail
- **Cơ sở dữ liệu:** PostgreSQL 16
- **Frontend:** HTML / CSS / JavaScript thuần, phục vụ qua nginx
- **Hạ tầng:** Docker Compose, Cloudflare Quick Tunnel (tuỳ chọn)

## Chạy nhanh bằng Docker

Yêu cầu: **Docker Desktop** (đã gồm Docker Compose v2).

```powershell
docker compose up -d --build
```

Sau khi các container chạy:

| Mục | URL |
|---|---|
| Giao diện người dùng | http://localhost |
| Trang đăng nhập | http://localhost/giaodienđnhap.html |
| Trang thủ thư | http://localhost:8080/admin |

Xem log / lấy link public (nếu dùng tunnel):

```powershell
docker compose logs -f backend
docker compose logs -f tunnel
```

## Tài khoản demo

| Vai trò | Tài khoản | Mật khẩu |
|---|---|---|
| Quản trị | `ADMIN01` / `admin@library.com` | `123456` |
| Độc giả | `SV001` / `nguyenvana@student.com` | `123456` |
| Độc giả | `SV002` / `tranthib@student.com` | `123456` |

## Tài liệu chi tiết

Xem **[docs/SETUP.md](docs/SETUP.md)** để biết đầy đủ:

- Kiến trúc, sơ đồ luồng và cấu trúc thư mục
- Cấu hình `.env` và cách lấy Gmail App Password để gửi OTP
- Chạy bằng Docker Compose: các lệnh build, dừng, reset dữ liệu, xem link tunnel
- Chạy dev thủ công: nạp SQL, chạy backend bằng Maven Wrapper, phục vụ frontend qua nginx
- Checklist kiểm tra sau khi chạy, lỗi thường gặp và phụ lục API

## Cấu trúc thư mục

```
backend/    # Spring Boot (Java 21) + trang admin
database/   # Script tạo schema và dữ liệu mẫu PostgreSQL
frontend/   # HTML/CSS/JS + nginx.conf
docs/       # Tài liệu hướng dẫn
docker-compose.yml
.env        # Biến môi trường (không commit)
```

> Frontend gọi API bằng đường dẫn tương đối (`/api/...`), nên cần chạy qua nginx — không dùng Live Server thuần. Chi tiết ở [mục 5.3](docs/SETUP.md#53-phục-vụ-frontend-qua-nginx).
