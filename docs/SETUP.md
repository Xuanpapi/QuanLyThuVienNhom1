# Hướng dẫn cài đặt & chạy dự án Quản lý Thư viện

Tài liệu này hướng dẫn từ đầu đến cuối cách dựng và chạy dự án **Quản lý Thư viện** trên máy cá nhân, gồm 2 cách:

- **Cách 1 — Docker Compose (khuyến nghị):** chạy toàn bộ hệ thống bằng 1 lệnh, không cần cài Java/PostgreSQL.
- **Cách 2 — Dev thủ công:** chạy backend trực tiếp bằng Maven để sửa code, frontend phục vụ qua nginx.

> Nếu chỉ cần chạy thử để demo/chấm bài, hãy dùng **Cách 1**.

---

## Mục lục

1. [Giới thiệu & kiến trúc](#1-giới-thiệu--kiến-trúc)
2. [Yêu cầu môi trường](#2-yêu-cầu-môi-trường)
3. [Cấu hình biến môi trường (.env)](#3-cấu-hình-biến-môi-trường-env)
4. [Cách 1 — Chạy bằng Docker Compose](#4-cách-1--chạy-bằng-docker-compose)
5. [Cách 2 — Chạy dev thủ công](#5-cách-2--chạy-dev-thủ-công)
6. [Tài khoản demo](#6-tài-khoản-demo)
7. [Checklist sau khi chạy](#7-checklist-sau-khi-chạy)
8. [Lỗi thường gặp](#8-lỗi-thường-gặp)
9. [Phụ lục: API & sao lưu dữ liệu](#9-phụ-lục-api--sao-lưu-dữ-liệu)

---

## 1. Giới thiệu & kiến trúc

### Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Backend | Spring Boot **4.1.1**, Java **21**, Spring Data JPA, Spring Mail |
| Cơ sở dữ liệu | PostgreSQL **16** |
| Frontend | HTML / CSS / JavaScript thuần (không build tool) |
| Web server / Reverse proxy | nginx (alpine) |
| Đóng gói | Docker Compose |
| Đưa web ra Internet | Cloudflare Quick Tunnel (tuỳ chọn) |
| Xác thực | Session cookie (`JSESSIONID`) + OTP gửi qua Gmail SMTP |

### Sơ đồ luồng

```
Trình duyệt
    │
    ▼
nginx :80  ──►  file tĩnh (frontend/*.html)
    │
    ├── /api/   ──►  backend :8080  ──►  PostgreSQL :5432
    └── /admin  ──►  backend :8080 (chỉ vai trò QUAN_TRI)
```

- Frontend **không** gọi thẳng `localhost:8080`; mã nguồn dùng đường dẫn tương đối `/api/...`.
  Vì vậy frontend **phải được phục vụ qua nginx** (hoặc một reverse proxy tương đương).
- Trang `/admin` do backend sinh ra và kiểm tra session + vai trò trước khi trả HTML/JS.

### Cấu trúc thư mục

```
Quan_ly_ThuVien/
├── backend/                  # Spring Boot (Java 21)
│   ├── src/main/java/        # controller, service, repository, entity, dto...
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── static/           # ảnh bìa sách
│   │   └── private/admin.html
│   ├── Dockerfile
│   └── mvnw.cmd / mvnw
├── database/
│   ├── create_postSQL.sql    # tạo schema PostgreSQL
│   ├── insert_postgreSQL.sql # dữ liệu mẫu
│   ├── Dockerfile.init       # ⚠ di sản SQL Server, không dùng
│   └── init_db.py            # ⚠ di sản SQL Server, không dùng
├── frontend/
│   ├── giaodienđnhap.html    # đăng nhập / quên mật khẩu
│   ├── giaodienđky.html      # đăng ký (OTP qua email)
│   ├── trangchu.html         # trang chủ độc giả
│   ├── index.html            # cổng chuyển hướng
│   ├── styles.css, password-toggle.js
│   ├── images/books/         # ảnh bìa
│   ├── nginx.conf            # cấu hình nginx cho Docker
│   └── Dockerfile
├── docker-compose.yml        # db + backend + frontend + tunnel
├── .env                      # biến môi trường (KHÔNG commit)
└── README.md
```

> `database/Dockerfile.init` và `database/init_db.py` là phần còn lại từ phiên bản SQL Server cũ. Luồng hiện tại chỉ dùng 2 file `.sql` phía trên.

---

## 2. Yêu cầu môi trường

### Cách 1 — Docker Compose

| Phần mềm | Phiên bản | Ghi chú |
|---|---|---|
| Docker Desktop | mới nhất | Đã bao gồm Docker Compose v2 |
| Git (tuỳ chọn) | — | Để clone source |

Không cần cài Java, Maven hay PostgreSQL.

Kiểm tra:

```powershell
docker --version
docker compose version
```

### Cách 2 — Dev thủ công

| Phần mềm | Phiên bản | Ghi chú |
|---|---|---|
| JDK | **21** | Bắt buộc (pom.xml đặt `java.version=21`) |
| PostgreSQL | **16** | Có thể dùng container thay vì cài local |
| Docker Desktop | mới nhất | Chỉ để chạy nginx phục vụ frontend |

Kiểm tra:

```powershell
java -version
psql --version
docker --version
```

> Maven không cần cài riêng vì dự án đã có Maven Wrapper (`mvnw.cmd` trên Windows, `./mvnw` trên macOS/Linux).

---

## 3. Cấu hình biến môi trường (.env)

File `.env` nằm ở thư mục gốc và **đã bị gitignore**, nên khi clone mới sẽ không có. Tạo file `.env` với nội dung:

```dotenv
# Mật khẩu PostgreSQL dùng cho cả container db và backend
POSTGRES_PASSWORD=YourStrong@Passw0rd

# Tài khoản Gmail gửi OTP (app password 16 ký tự, KHÔNG phải mật khẩu Gmail)
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=xxxxxxxxxxxxxxxx
```

| Biến | Bắt buộc | Ý nghĩa |
|---|---|---|
| `POSTGRES_PASSWORD` | Nên đặt | Mật khẩu user `postgres` của container DB và cũng là mật khẩu backend dùng để kết nối |
| `SPRING_MAIL_USERNAME` | Chỉ khi cần OTP | Địa chỉ Gmail dùng để gửi mã OTP |
| `SPRING_MAIL_PASSWORD` | Chỉ khi cần OTP | **App Password** 16 ký tự của Gmail |

### Lấy Gmail App Password

1. Đăng nhập Google Account → **Bảo mật**.
2. Bật **Xác minh 2 bước (2-Step Verification)** nếu chưa bật.
3. Vào **App passwords** (Mật khẩu ứng dụng) → tạo cho “Mail / Windows Computer”.
4. Copy chuỗi **16 ký tự** (có thể có khoảng trắng, bỏ khoảng trắng khi dán).

> ⚠ Không dùng mật khẩu đăng nhập Gmail thường — Gmail sẽ từ chối và backend báo `MailAuthenticationException`.
> Nếu chưa cấu hình mail, bạn vẫn đăng nhập được bằng tài khoản seed; chỉ các chức năng **đăng ký / quên mật khẩu / đổi email / đổi SĐT** (gửi OTP) sẽ báo lỗi.

> 🔒 Trước khi đưa source lên public, hãy đổi mật khẩu DB và dùng App Password riêng. Không commit `.env`, không hard-code thông tin thật vào `application.properties`.

---

## 4. Cách 1 — Chạy bằng Docker Compose

### 4.1. Khởi động

Tại thư mục gốc dự án:

```powershell
docker compose up -d --build
```

Lần đầu sẽ build backend (tải Maven dependencies) và frontend, sau đó tạo DB và nạp dữ liệu mẫu — có thể mất vài phút.

Kiểm tra trạng thái:

```powershell
docker compose ps
docker compose logs -f backend
```

### 4.2. Truy cập

| Mục | URL |
|---|---|
| Giao diện người dùng | http://localhost |
| Trang đăng nhập | http://localhost/giaodienđnhap.html |
| Trang thủ thư (chỉ `QUAN_TRI`) | http://localhost:8080/admin |
| Backend API (trực tiếp) | http://localhost:8080/api/... |

### 4.3. Đưa web ra Internet (tuỳ chọn)

Service `tunnel` dùng Cloudflare Quick Tunnel, không cần mở port trên router.

```powershell
docker compose logs -f tunnel
```

Tìm dòng có dạng:

```
https://ten-ngau-nhien.trycloudflare.com
```

Link này **đổi mỗi lần container tunnel khởi động lại**. Không cần sửa cấu hình vì `app.frontend.login-url` được để dạng tương đối (`/giaodienđnhap.html`).

### 4.4. Cơ chế nạp dữ liệu

- Container `db` tự chạy 2 file khi **volume còn trống**:
  - `database/create_postSQL.sql` → tạo bảng
  - `database/insert_postgreSQL.sql` → dữ liệu mẫu
- Nếu volume đã có dữ liệu, script **không chạy lại** (tránh lỗi trùng khoá).
- Vì backend đặt `spring.jpa.hibernate.ddl-auto=validate`, schema **phải** khớp với entity, nếu không backend sẽ dừng khi khởi động.

### 4.5. Các lệnh thường dùng

| Mục đích | Lệnh |
|---|---|
| Xem log backend | `docker compose logs -f backend` |
| Dừng (giữ dữ liệu) | `docker compose stop` |
| Dừng & xoá container (giữ dữ liệu) | `docker compose down` |
| **Reset sạch DB** | `docker compose down -v` rồi `docker compose up -d --build` |
| Build lại sau khi sửa code | `docker compose up -d --build` |
| Build lại riêng backend | `docker compose up -d --build backend` |
| Build lại riêng frontend | `docker compose up -d --build frontend` |
| Xem container đang chạy | `docker compose ps` |

> ⚠ `docker compose down -v` **xoá toàn bộ dữ liệu** trong volume `postgres_data` (bao gồm tài khoản và phiếu mượn đã tạo). Chỉ dùng khi muốn quay về dữ liệu mẫu ban đầu.

---

## 5. Cách 2 — Chạy dev thủ công

Cách này phù hợp khi cần sửa code backend/frontend và muốn thấy thay đổi nhanh.

### 5.1. Chuẩn bị cơ sở dữ liệu

Chọn **một** trong hai hướng:

#### A. Dùng PostgreSQL cài trên máy

```powershell
chcp 65001
$env:PGCLIENTENCODING = "UTF8"

createdb -U postgres quan_ly_thu_vien

psql -U postgres -d quan_ly_thu_vien -v ON_ERROR_STOP=1 -f database/create_postSQL.sql
psql -U postgres -d quan_ly_thu_vien -v ON_ERROR_STOP=1 -f database/insert_postgreSQL.sql
```

#### B. Dùng container `db` có sẵn

Tạo file `docker-compose.override.yml` ở thư mục gốc (chỉ để dev, **không commit**):

```yaml
services:
  db:
    ports:
      - "5432:5432"
```

Rồi chạy:

```powershell
docker compose up -d db
```

Compose sẽ tự gộp file override, giúp `localhost:5432` truy cập được từ máy host. Dữ liệu mẫu vẫn được nạp tự động ở lần đầu.

> Nếu dùng hướng B, mật khẩu trong container là `POSTGRES_PASSWORD` (mặc định `YourStrong@Passw0rd`), khác với mật khẩu mặc định `19062006` trong `application.properties`. Xem mục 5.2 để truyền đúng biến môi trường.

### 5.2. Chạy backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Trên macOS/Linux:

```bash
cd backend
./mvnw spring-boot:run
```

Cấu hình kết nối DB theo thứ tự ưu tiên:

1. **Biến môi trường** (khuyến nghị):

```powershell
$env:SPRING_DATASOURCE_URL      = "jdbc:postgresql://localhost:5432/quan_ly_thu_vien"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "YourStrong@Passw0rd"   # hoặc mật khẩu Postgres local của bạn
$env:SPRING_MAIL_USERNAME       = "your-email@gmail.com"
$env:SPRING_MAIL_PASSWORD       = "xxxxxxxxxxxxxxxx"

.\mvnw.cmd spring-boot:run
```

2. **Giá trị mặc định trong** `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/quan_ly_thu_vien}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:19062006}
```

Sửa trực tiếp các giá trị sau dấu `:` nếu muốn (không khuyến nghị commit mật khẩu thật).

Backend chạy tại **http://localhost:8080**. Kiểm tra nhanh:

```powershell
curl http://localhost:8080/api/sach
```

### 5.3. Phục vụ frontend qua nginx

> ⚠ **Không dùng Live Server thuần** (port 5500) cho frontend.
> Lý do: các trang gọi API bằng đường dẫn tương đối (`const API_URL = ""`, `const backendBase = ""`),
> nên request sẽ bay vào chính server tĩnh (`:5500/api/...`) và trả 404 thay vì tới backend `:8080`.

Cách đúng: dùng nginx làm reverse proxy, trỏ `/api/` và `/admin` về backend đang chạy trên host.

Tạo file `frontend/nginx.dev.conf` (chỉ để dev, **không commit**):

```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;
    charset utf-8;

    location /api/ {
        proxy_pass http://host.docker.internal:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    location /admin {
        proxy_pass http://host.docker.internal:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location / {
        try_files $uri $uri/ =404;
    }
}
```

Chạy nginx (PowerShell, tại thư mục gốc):

```powershell
docker run --rm -p 5500:80 `
  -v "${PWD}/frontend:/usr/share/nginx/html:ro" `
  -v "${PWD}/frontend/nginx.dev.conf:/etc/nginx/conf.d/default.conf:ro" `
  nginx:alpine
```

Mở **http://localhost:5500**.

Ghi chú:

- `host.docker.internal` hoạt động sẵn trên Docker Desktop (Windows/macOS). Trên Linux cần thêm `--add-host=host.docker.internal:host-gateway`.
- Vì trình duyệt chỉ nói chuyện với `localhost:5500`, không có lỗi CORS và cookie phiên `JSESSIONID` vẫn hoạt động bình thường.
- Sửa HTML/CSS/JS xong chỉ cần tải lại trang (không cần build lại).

### 5.4. So sánh nhanh

| Tiêu chí | Docker Compose | Dev thủ công |
|---|---|---|
| Cài đặt | Chỉ cần Docker | JDK 21 + PostgreSQL + Docker (nginx) |
| Tốc độ dựng lần đầu | Chậm hơn (build image) | Nhanh nếu đã có sẵn môi trường |
| Sửa code backend | Phải build lại image | Chạy lại `spring-boot:run` là xong |
| Sửa frontend | Phải build lại image | Tải lại trang |
| Dữ liệu | Volume tự nạp, reset bằng `down -v` | Tự quản lý DB/psql |
| Phù hợp | Demo, chấm bài, chạy thử | Phát triển, debug |

---

## 6. Tài khoản demo

Dữ liệu mẫu (`insert_postgreSQL.sql`) có sẵn các tài khoản sau. Ô đăng nhập chấp nhận **mã sinh viên hoặc email**.

| Vai trò | Tài khoản | Mật khẩu | Thông tin thêm |
|---|---|---|---|
| Quản trị (`QUAN_TRI`) | `ADMIN01` hoặc `admin@library.com` | `123456` | Vào được `/admin` |
| Độc giả (`DOC_GIA`) | `SV001` hoặc `nguyenvana@student.com` | `123456` | Thẻ `DG001`, lớp CNTT01 |
| Độc giả (`DOC_GIA`) | `SV002` hoặc `tranthib@student.com` | `123456` | Thẻ `DG002`, lớp CNTT02 |

> Mật khẩu trong dữ liệu mẫu được lưu dạng thô (plain text) để tiện demo. Không dùng cách này cho môi trường thật.

Dữ liệu mẫu còn có: 12 đầu sách, 21 cuốn sách, phiếu mượn ở các trạng thái `DA_TRA` / `DANG_MUON` / `CHO_DUYET`, 1 phiếu trả và 1 phiếu phạt chưa thanh toán.

---

## 7. Checklist sau khi chạy

Đánh dấu từng bước để chắc hệ thống chạy đúng:

- [ ] `docker compose ps` — 4 service (`db`, `backend`, `frontend`, `tunnel`) ở trạng thái `running` (riêng `db` có healthcheck).
- [ ] `docker compose logs backend` không có lỗi `Schema-validation` hay `Connection refused`.
- [ ] Mở `http://localhost` → tự chuyển tới trang đăng nhập.
- [ ] Đăng nhập `ADMIN01 / 123456` → vào được `http://localhost:8080/admin`, thấy danh sách sách/phiếu mượn.
- [ ] Đăng nhập `SV001 / 123456` → vào `trangchu.html`, xem được danh sách sách và lịch sử mượn.
- [ ] Thử **mượn online** một cuốn còn `CÓ_SẴN` → tạo phiếu `CHO_DUYET`.
- [ ] Đăng nhập `ADMIN01` → duyệt phiếu vừa tạo.
- [ ] Đổi mật khẩu / đổi SĐT (nếu đã cấu hình Gmail) → nhận được OTP trong email.
- [ ] Mở link tunnel (nếu dùng) → giao diện hoạt động qua HTTPS.

---

## 8. Lỗi thường gặp

| Triệu chứng | Nguyên nhân | Cách xử lý |
|---|---|---|
| `Bind for 0.0.0.0:80 failed: port is already allocated` | Port 80 bị IIS, Skype, XAMPP chiếm | Tắt dịch vụ đang giữ port, hoặc đổi mapping trong `docker-compose.yml` thành `"8081:80"` rồi mở `http://localhost:8081` |
| `Bind for 0.0.0.0:8080 failed` | Port 8080 bị chiếm | Tắt ứng dụng đang dùng 8080 (`netstat -ano | findstr :8080`) hoặc đổi mapping |
| Backend dừng với `Schema-validation: missing table [...]` | Chưa nạp SQL hoặc volume DB cũ/khác schema | `docker compose down -v` rồi `docker compose up -d --build`; hoặc nạp lại 2 file SQL |
| Sửa file SQL nhưng DB không thay đổi | Init script chỉ chạy khi volume trống | `docker compose down -v` để tạo lại DB từ đầu |
| `Connection refused` tới `localhost:5432` khi chạy backend thủ công | Postgres chưa chạy hoặc chưa mở port | Khởi động Postgres; nếu dùng container, thêm `ports: "5432:5432"` (mục 5.1B) |
| Sai mật khẩu DB (`password authentication failed`) | Mật khẩu container khác mặc định `19062006` | Set `SPRING_DATASOURCE_PASSWORD` đúng với `POSTGRES_PASSWORD` |
| Gửi OTP báo lỗi / không nhận được mail | Dùng mật khẩu Gmail thường thay vì App Password; chưa bật 2FA; sai username | Tạo lại App Password 16 ký tự, kiểm tra log backend |
| Mở `http://localhost:5500` thì API trả 404 | Chạy Live Server thuần, API dùng path tương đối | Chạy frontend qua nginx như mục 5.3 |
| `404` với `giaodienđnhap.html` | File có ký tự `đ`, bị đổi tên/encode sai | Giữ nguyên tên file, không rename; dùng đúng link trong tài liệu |
| Link Cloudflare cũ không còn tác dụng | Quick Tunnel cấp link mới sau mỗi lần restart | Chạy `docker compose logs -f tunnel` để lấy link mới |
| PowerShell báo không chạy được `mvnw.cmd` | Chính sách ExecutionPolicy | Chạy `cmd /c mvnw.cmd spring-boot:run` hoặc `Set-ExecutionPolicy -Scope Process Bypass` |
| `UnsupportedClassVersionError` | JDK thấp hơn 21 | Cài JDK 21 và kiểm tra `java -version` |
| Sửa code nhưng Docker vẫn chạy bản cũ | Image chưa build lại | `docker compose up -d --build` |

---

## 9. Phụ lục: API & sao lưu dữ liệu

### 9.1. Nhóm endpoint chính

Tất cả endpoint API có tiền tố `/api`. Backend trả JSON, riêng `/admin` trả HTML.

| Nhóm | Tiền tố | Endpoint tiêu biểu |
|---|---|---|
| Người dùng / xác thực | `/api/users` | `POST /dangnhap`, `GET /phien`, `POST /dang-xuat`, `POST /dangky/gui-otp`, `POST /dangky/xac-nhan`, `GET /profile/{id}`, `PATCH /{id}/trang-thai`, `POST /{id}/doi-mat-khau`, `POST /quen-mat-khau/gui-otp`, `POST /quen-mat-khau/xac-nhan`, `POST /{id}/doi-sdt/gui-otp`, `POST /{id}/doi-sdt/xac-nhan` |
| Sách | `/api/sach` | `GET /`, `GET /{id}`, `GET /{id}/cuon-sach`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Phiếu mượn | `/api/phieu-muon` | `GET /lich-su/{nguoiDungId}`, `POST /muon-online`, `GET /`, `GET /qua-han`, `GET /{id}`, `POST /`, `POST /{id}/duyet`, `POST /{id}/tu-choi`, `POST /{id}/huy`, `POST /{id}/tra`, `POST /{id}/gui-canh-bao` |
| Phiếu phạt | `/api/phieu-phat` | `GET /`, `GET /phieu-muon/{id}`, `POST /`, `POST /{id}/thanh-toan`, `POST /{id}/gui-thong-bao`, `DELETE /{id}` |
| Danh mục | `/api/danh-muc` | `GET /`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Thể loại | `/api/the-loai` | `GET /`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Metadata (dropdown) | `/api/metadata` | `GET /nhaxuatban`, `/danhmuc`, `/tacgia`, `/theloai`, `/vaitro` |
| Độc giả | `/api/doc-gia` | `GET /` |
| Thông báo | `/api/thong-bao` | `GET /nguoi-dung/{nguoiDungId}` |
| Trang thủ thư | `/admin` | `GET /admin` (yêu cầu session `QUAN_TRI`) |

### 9.2. Sao lưu & phục hồi DB (Docker)

Sao lưu:

```powershell
docker exec library_db pg_dump -U postgres -d quan_ly_thu_vien -Fc -f /tmp/backup.dump
docker cp library_db:/tmp/backup.dump .\backup.dump
```

Phục hồi:

```powershell
docker cp .\backup.dump library_db:/tmp/backup.dump
docker exec library_db pg_restore -U postgres -d quan_ly_thu_vien --clean --if-exists /tmp/backup.dump
```

> Cách này tránh lỗi PowerShell không hỗ trợ toán tử `<` khi pipe file nhị phân.
