package com.example.backend.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Trang thủ thư được đọc từ classpath (thư mục private, không nằm trong static)
 * và chỉ trả về khi phiên đăng nhập có vai trò QUAN_TRI.
 * <p>
 * Đây là lớp chặn tối thiểu theo mức B: người dùng thường không tải được mã HTML/JS
 * của trang quản trị qua URL trực tiếp.
 */
@RestController
public class AdminPageController {

    private static final MediaType HTML_UTF8 =
            MediaType.parseMediaType("text/html;charset=UTF-8");

    private final Resource adminHtml = new ClassPathResource("private/admin.html");

    @Value("${app.frontend.login-url:http://localhost:5500/giaodienđnhap.html}")
    private String loginUrl;

    @GetMapping({"/admin", "/admin/"})
    public ResponseEntity<String> trangAdmin(HttpSession session) {
        Object nguoiDungId = session.getAttribute("nguoiDungId");
        Object vaiTro = session.getAttribute("vaiTro");

        if (nguoiDungId == null || vaiTro == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(loginUri())
                    .cacheControl(CacheControl.noStore())
                    .build();
        }

        if (!"QUAN_TRI".equalsIgnoreCase(String.valueOf(vaiTro))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(HTML_UTF8)
                    .cacheControl(CacheControl.noStore())
                    .body("""
                            <!DOCTYPE html>
                            <html lang="vi">
                            <head><meta charset="UTF-8"><title>Không có quyền truy cập</title></head>
                            <body style="font-family:Segoe UI,Tahoma,sans-serif;padding:40px;text-align:center;">
                                <h1 style="color:#173b73;">403 - Không có quyền truy cập</h1>
                                <p>Trang này chỉ dành cho quản trị viên thư viện.</p>
                                <p><a href="%s" style="color:#173b73;">Quay về trang đăng nhập</a></p>
                            </body>
                            </html>
                            """.formatted(loginUrl));
        }

        try {
            String html = new String(adminHtml.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("__LOGIN_URL__", loginUrl);

            return ResponseEntity.ok()
                    .contentType(HTML_UTF8)
                    .cacheControl(CacheControl.noStore())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .body(html);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(HTML_UTF8)
                    .cacheControl(CacheControl.noStore())
                    .body("<h1>Không thể tải trang thủ thư</h1>");
        }
    }

    /**
     * Tên file frontend có ký tự tiếng Việt (giaodienđnhap.html), nên cần encode
     * trước khi đưa vào header Location. Nếu không, URI.create sẽ báo lỗi 400.
     */
    private URI loginUri() {
        return UriComponentsBuilder.fromUriString(loginUrl).build().encode().toUri();
    }
}
