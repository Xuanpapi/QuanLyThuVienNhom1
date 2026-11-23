package com.example.backend.service;

import com.example.backend.dto.DangKyRequest;
import com.example.backend.exception.BadRequestException;
import com.example.backend.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quản lý OTP cho luồng đăng ký tài khoản.
 *
 * Đặc điểm:
 * - Chưa tạo tài khoản cho tới khi OTP được xác nhận (không đổi schema DB).
 * - OTP lưu trong bộ nhớ, hết hạn sau 5 phút, chỉ dùng một lần.
 * - Gửi lại mã: phải chờ 60 giây và tối đa 1 lần (tổng cộng 2 mã).
 */
@Service
public class DangKyOtpService {

    /** Số lần nhập sai tối đa trước khi hủy yêu cầu đăng ký. */
    private static final int SO_LAN_SAI_TOI_DA = 5;

    private final EmailService emailService;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungService nguoiDungService;

    @Value("${otp.expiry.ms:300000}")
    private long otpExpiryMs;

    @Value("${otp.resend.cooldown.ms:60000}")
    private long resendCooldownMs;

    @Value("${otp.resend.max:1}")
    private int maxResend;

    private final Map<String, PendingDangKy> pendingByEmail = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public DangKyOtpService(EmailService emailService,
                            NguoiDungRepository nguoiDungRepository,
                            NguoiDungService nguoiDungService) {
        this.emailService = emailService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungService = nguoiDungService;
    }

    /**
     * Gửi OTP đăng ký (lần đầu) hoặc gửi lại mã.
     */
    public synchronized Map<String, Object> guiOtp(DangKyRequest request) {
        String email = request.getEmail().trim();
        request.setEmail(email);

        if (nguoiDungRepository.existsByMasv(request.getMa_sv().trim())) {
            throw new BadRequestException("Mã sinh viên đã tồn tại");
        }
        if (nguoiDungRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã tồn tại");
        }

        long now = System.currentTimeMillis();
        PendingDangKy pending = pendingByEmail.get(email);

        // Yêu cầu cũ đã hết hạn thì coi như làm lại từ đầu
        if (pending != null && now > pending.hetHanLuc) {
            pendingByEmail.remove(email);
            pending = null;
        }

        boolean laGuiLai = pending != null;
        if (laGuiLai) {
            if (pending.soLanGuiLai >= maxResend) {
                throw new BadRequestException(
                        "Bạn đã dùng hết lượt gửi lại mã. Vui lòng quay lại chỉnh sửa và đăng ký lại.");
            }
            long conLaiMs = pending.lanGuiCuoi + resendCooldownMs - now;
            if (conLaiMs > 0) {
                long conLaiGiay = (conLaiMs + 999) / 1000;
                throw new BadRequestException("Vui lòng đợi " + conLaiGiay + " giây nữa mới được gửi lại mã.");
            }
        }

        String otp = generateOtp();
        try {
            emailService.guiEmail(
                    email,
                    "[Thư viện] Mã OTP xác nhận đăng ký tài khoản",
                    taoNoiDungOtp(request.getHo_ten(), otp));
        } catch (Exception e) {
            throw new BadRequestException(
                    "Không gửi được email OTP. Vui lòng kiểm tra địa chỉ email và thử lại.");
        }

        PendingDangKy moi = new PendingDangKy();
        moi.thongTin = request;
        moi.otp = otp;
        moi.hetHanLuc = now + otpExpiryMs;
        moi.lanGuiCuoi = now;
        moi.soLanGuiLai = laGuiLai ? pending.soLanGuiLai + 1 : 0;
        moi.soLanSai = 0;
        pendingByEmail.put(email, moi);

        Map<String, Object> ketQua = new LinkedHashMap<>();
        ketQua.put("message", laGuiLai
                ? "Đã gửi lại mã OTP. Vui lòng kiểm tra email."
                : "Đã gửi mã OTP. Vui lòng kiểm tra email.");
        ketQua.put("email", email);
        ketQua.put("emailMasked", maskEmail(email));
        ketQua.put("cooldownGiay", resendCooldownMs / 1000);
        ketQua.put("soLanGuiLaiConLai", Math.max(maxResend - moi.soLanGuiLai, 0));
        ketQua.put("hetHanGiay", otpExpiryMs / 1000);
        return ketQua;
    }

    /**
     * Xác nhận OTP và tạo tài khoản.
     */
    public synchronized Map<String, Object> xacNhan(String emailRaw, String otpRaw) {
        String email = emailRaw.trim();
        String otp = otpRaw.trim();
        long now = System.currentTimeMillis();

        PendingDangKy pending = pendingByEmail.get(email);
        if (pending == null || now > pending.hetHanLuc) {
            pendingByEmail.remove(email);
            throw new BadRequestException(
                    "Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng đăng ký lại từ đầu.");
        }

        if (!pending.otp.equals(otp)) {
            pending.soLanSai++;
            if (pending.soLanSai >= SO_LAN_SAI_TOI_DA) {
                pendingByEmail.remove(email);
                throw new BadRequestException(
                        "Bạn đã nhập sai quá " + SO_LAN_SAI_TOI_DA + " lần. Vui lòng đăng ký lại từ đầu.");
            }
            throw new BadRequestException("Mã OTP không đúng. Bạn còn "
                    + (SO_LAN_SAI_TOI_DA - pending.soLanSai) + " lần thử.");
        }

        nguoiDungService.dangky(pending.thongTin);
        pendingByEmail.remove(email);

        return Map.of("message", "Xác thực email thành công! Tài khoản đã được tạo.");
    }

    /**
     * Hủy yêu cầu đăng ký đang chờ (khi người dùng quay lại chỉnh sửa form).
     */
    public synchronized void huyDangKy(String email) {
        if (email != null) {
            pendingByEmail.remove(email.trim());
        }
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String taoNoiDungOtp(String hoTen, String otp) {
        return "Xin chào " + (hoTen != null && !hoTen.isBlank() ? hoTen : "bạn") + ",\n\n"
                + "Mã OTP xác nhận đăng ký tài khoản thư viện của bạn là: " + otp + "\n\n"
                + "Mã có hiệu lực trong " + (otpExpiryMs / 60000) + " phút và chỉ sử dụng được 1 lần.\n"
                + "Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Nếu bạn không thực hiện đăng ký, hãy bỏ qua email này.\n\n"
                + "Trân trọng,\nHệ thống Quản lý Thư viện";
    }

    /** Che một phần email khi hiển thị: nguyenvana@gmail.com → n*****a@gmail.com */
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return email;
        }
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        return local.charAt(0) + "*****" + local.charAt(local.length() - 1) + domain;
    }

    /** Thông tin đăng ký đang chờ xác thực (lưu trong RAM). */
    private static class PendingDangKy {
        private DangKyRequest thongTin;
        private String otp;
        private long hetHanLuc;
        private long lanGuiCuoi;
        private int soLanGuiLai;
        private int soLanSai;
    }
}
