package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình CORS cho giai đoạn chạy frontend riêng (Live Server).
 * <p>
 * Chỉ cho phép các origin localhost/127.0.0.1 và bật credentials để cookie
 * phiên đăng nhập (JSESSIONID) có thể được gửi tới backend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost",
                        "http://localhost:*",
                        "http://127.0.0.1",
                        "http://127.0.0.1:*"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
