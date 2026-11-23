package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.NguoiDung;
import com.example.backend.service.DangKyOtpService;
import com.example.backend.service.DoiEmailService;
import com.example.backend.service.NguoiDungService;
import com.example.backend.service.OtpService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class NguoiDungController {
    private final NguoiDungService nguoiDungService;
    private final OtpService otpService;
    private final DangKyOtpService dangKyOtpService;
    private final DoiEmailService doiEmailService;

    public NguoiDungController(NguoiDungService nguoiDungService,
                               OtpService otpService,
                               DangKyOtpService dangKyOtpService,
                               DoiEmailService doiEmailService) {
        this.nguoiDungService = nguoiDungService;
        this.otpService = otpService;
        this.dangKyOtpService = dangKyOtpService;
        this.doiEmailService = doiEmailService;
    }

    // ─────────────────────────────────────────────────────────────
    // ĐĂNG KÝ CÓ XÁC THỰC EMAIL BẰNG OTP
    // Bước 1: gửi OTP (lần đầu hoặc gửi lại)
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/dangky/gui-otp")
    public ResponseEntity<?> guiOtpDangKy(@Valid @RequestBody DangKyRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            return ResponseEntity.ok(dangKyOtpService.guiOtp(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Bước 2: xác nhận OTP → tạo tài khoản
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/dangky/xac-nhan")
    public ResponseEntity<?> xacNhanDangKy(@Valid @RequestBody XacNhanDangKyRequest request,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            return ResponseEntity.ok(dangKyOtpService.xacNhan(request.getEmail(), request.getOtp()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Hủy yêu cầu đăng ký đang chờ (khi quay lại chỉnh sửa form)
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/dangky/huy")
    public ResponseEntity<?> huyDangKy(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Thiếu email cần hủy đăng ký"));
            }
            dangKyOtpService.huyDangKy(email);
            return ResponseEntity.ok(Map.of("message", "Đã hủy yêu cầu đăng ký"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** API đăng nhập bằng Mã sinh viên hoặc Email + Mật khẩu */
    @PostMapping("/dangnhap")
    public ResponseEntity<?> dangNhap(@Valid @RequestBody DangNhapRequest request,
                                      BindingResult bindingResult,
                                      HttpSession session) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            NguoiDung nguoiDung = nguoiDungService.dangNhap(request);

            // Lưu phiên đăng nhập tối thiểu để bảo vệ trang thủ thư.
            // Kiểm tra phân quyền đầy đủ cho toàn bộ API sẽ làm ở bước bảo mật sau.
            String vaiTro = nguoiDung.getVai_tro_id() != null
                    ? nguoiDung.getVai_tro_id().getTenVaiTro() : "";
            session.setAttribute("nguoiDungId", nguoiDung.getId());
            session.setAttribute("vaiTro", vaiTro);
            session.setAttribute("hoTen", nguoiDung.getHo_ten());

            return ResponseEntity.ok(nguoiDung);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** Kiểm tra phiên đăng nhập hiện tại (dùng cho trang thủ thư). */
    @GetMapping("/phien")
    public ResponseEntity<?> kiemTraPhien(HttpSession session) {
        Object nguoiDungId = session.getAttribute("nguoiDungId");
        if (nguoiDungId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Chưa đăng nhập"));
        }
        return ResponseEntity.ok(Map.of(
                "nguoiDungId", nguoiDungId,
                "vaiTro", String.valueOf(session.getAttribute("vaiTro")),
                "hoTen", String.valueOf(session.getAttribute("hoTen"))
        ));
    }

    /** Đăng xuất: hủy phiên phía máy chủ. */
    @PostMapping("/dang-xuat")
    public ResponseEntity<?> dangXuat(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Đã đăng xuất"));
    }

    /** API xem thông tin hồ sơ cá nhân */
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(nguoiDungService.getProfile(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // QUẢN LÝ NGƯỜI DÙNG (US 1.2.1 & 1.2.2)
    // ─────────────────────────────────────────────────────────────

    /** API danh sách người dùng có phân trang, tìm kiếm và lọc */
    @GetMapping
    public ResponseEntity<?> getDanhSachNguoiDung(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String vaiTro,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(
                    nguoiDungService.getDanhSachNguoiDung(keyword, vaiTro, trangThai, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** API khóa / mở khóa tài khoản (thay vì xóa cứng) */
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<?> capNhatTrangThai(
            @PathVariable Integer id,
            @Valid @RequestBody CapNhatTrangThaiRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            boolean moKhoa = Boolean.TRUE.equals(request.getTrangThai());
            return ResponseEntity.ok(Map.of(
                    "message", moKhoa ? "Mở khóa tài khoản thành công !" : "Khóa tài khoản thành công !",
                    "data", nguoiDungService.capNhatTrangThai(
                            id, request.getTrangThai(), request.getNguoiThucHienId())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI MẬT KHẨU
    // POST /api/users/{id}/doi-mat-khau
    // Body: { matKhauCu, matKhauMoi, xacNhanMatKhauMoi }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(
            @PathVariable Integer id,
            @Valid @RequestBody DoiMatKhauRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            nguoiDungService.doiMatKhau(
                    id,
                    request.getMatKhauCu(),
                    request.getMatKhauMoi(),
                    request.getXacNhanMatKhauMoi()
            );
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // QUÊN MẬT KHẨU — Bước 1: gửi OTP về email đã đăng ký
    // POST /api/users/quen-mat-khau/gui-otp
    // Body: { email }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/quen-mat-khau/gui-otp")
    public ResponseEntity<?> guiOtpQuenMatKhau(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email") == null ? "" : body.get("email").trim();
            NguoiDung nguoiDung = nguoiDungService.getNguoiDungByEmail(email);

            if (Boolean.FALSE.equals(nguoiDung.getTrang_thai())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên"));
            }

            Map<String, Object> duLieu = otpService.guiOtp(
                    nguoiDung.getEmail(),
                    OtpService.ACTION_QUEN_MAT_KHAU,
                    "[Thư viện] Mã OTP đặt lại mật khẩu",
                    "đặt lại mật khẩu");

            long hetHanGiay = ((Number) duLieu.get("hetHanGiay")).longValue();
            Map<String, Object> ketQua = new LinkedHashMap<>(duLieu);
            ketQua.put("email", nguoiDung.getEmail());
            ketQua.put("message", "Đã gửi mã OTP tới email " + nguoiDung.getEmail()
                    + ". Mã có hiệu lực trong " + (hetHanGiay / 60)
                    + " phút. Vui lòng kiểm tra cả mục Thư rác.");
            return ResponseEntity.ok(ketQua);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // QUÊN MẬT KHẨU — Bước 2: xác nhận OTP + đặt mật khẩu mới
    // POST /api/users/quen-mat-khau/xac-nhan
    // Body: { email, otp, matKhauMoi, xacNhanMatKhauMoi }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/quen-mat-khau/xac-nhan")
    public ResponseEntity<?> xacNhanQuenMatKhau(
            @Valid @RequestBody DatLaiMatKhauRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            // Kiểm tra khớp mật khẩu trước khi xác thực OTP để không làm mất mã khi gõ sai
            if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhauMoi())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Mật khẩu mới và xác nhận mật khẩu không khớp"));
            }

            String email = request.getEmail().trim();
            NguoiDung nguoiDung = nguoiDungService.getNguoiDungByEmail(email);

            if (Boolean.FALSE.equals(nguoiDung.getTrang_thai())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên"));
            }

            if (!otpService.verifyOtp(email, OtpService.ACTION_QUEN_MAT_KHAU, request.getOtp())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Mã OTP không hợp lệ hoặc đã hết hạn"));
            }

            nguoiDungService.datLaiMatKhau(email, request.getMatKhauMoi());
            otpService.huyOtp(email, OtpService.ACTION_QUEN_MAT_KHAU);

            return ResponseEntity.ok(Map.of(
                    "message", "Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Bước 1: nhập email mới, gửi OTP về email hiện tại
    // POST /api/users/{id}/doi-email/bat-dau
    // Body: { emailMoi }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-email/bat-dau")
    public ResponseEntity<?> batDauDoiEmail(
            @PathVariable Integer id,
            @Valid @RequestBody BatDauDoiEmailRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            return ResponseEntity.ok(doiEmailService.batDau(id, request.getEmailMoi()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI EMAIL — Bước 2: xác nhận OTP email hiện tại
    // POST /api/users/{id}/doi-email/xac-nhan-cu
    // Body: { otp }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-email/xac-nhan-cu")
    public ResponseEntity<?> xacNhanEmailCu(
            @PathVariable Integer id,
            @Valid @RequestBody XacNhanOtpRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            return ResponseEntity.ok(doiEmailService.xacNhanEmailCu(id, request.getOtp()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI EMAIL — Gửi lại OTP của bước hiện tại
    // POST /api/users/{id}/doi-email/gui-lai
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-email/gui-lai")
    public ResponseEntity<?> guiLaiOtpDoiEmail(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(doiEmailService.guiLaiOtp(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI EMAIL — Bước 3: xác nhận OTP email mới + cập nhật email
    // POST /api/users/{id}/doi-email/xac-nhan
    // Body: { otp }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-email/xac-nhan")
    public ResponseEntity<?> xacNhanDoiEmail(
            @PathVariable Integer id,
            @Valid @RequestBody XacNhanOtpRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            return ResponseEntity.ok(doiEmailService.xacNhanEmailMoi(id, request.getOtp()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI SỐ ĐIỆN THOẠI — Bước 1: Gửi OTP về email cũ
    // POST /api/users/{id}/doi-sdt/gui-otp
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-sdt/gui-otp")
    public ResponseEntity<?> guiOtpDoiSdt(@PathVariable Integer id) {
        try {
            String emailCu = nguoiDungService.getEmailHienTai(id);
            Map<String, Object> duLieu = otpService.guiOtpKhongGioiHan(
                    emailCu,
                    OtpService.ACTION_DOI_SDT,
                    "[Thư viện] Xác nhận đổi số điện thoại",
                    "đổi số điện thoại"
            );

            long hetHanGiay = ((Number) duLieu.get("hetHanGiay")).longValue();
            Map<String, Object> body = new LinkedHashMap<>(duLieu);
            body.put("message", "Đã gửi mã OTP về email " + emailCu
                    + ". Mã có hiệu lực trong " + (hetHanGiay / 60) + " phút.");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐỔI SỐ ĐIỆN THOẠI — Bước 2: Xác nhận OTP + cập nhật SĐT mới
    // POST /api/users/{id}/doi-sdt/xac-nhan
    // Body: { otp, sdtMoi }
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/doi-sdt/xac-nhan")
    public ResponseEntity<?> xacNhanDoiSdt(
            @PathVariable Integer id,
            @Valid @RequestBody DoiSdtRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }
        try {
            String emailCu = nguoiDungService.getEmailHienTai(id);

            nguoiDungService.kiemTraSdtMoi(id, request.getSdtMoi());

            if (!otpService.verifyOtp(emailCu, OtpService.ACTION_DOI_SDT, request.getOtp())) {
                return ResponseEntity.badRequest().body(Map.of("message", "OTP không hợp lệ hoặc đã hết hạn"));
            }

            nguoiDungService.capNhatSdt(id, request.getSdtMoi());
            return ResponseEntity.ok(Map.of("message", "Đổi số điện thoại thành công !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────
}
