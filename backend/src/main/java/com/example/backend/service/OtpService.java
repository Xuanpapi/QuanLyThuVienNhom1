package com.example.backend.service;

import com.example.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service quản lý OTP cho các thao tác nhạy cảm:
 * - Đổi email
 * - Đổi số điện thoại
 *
 * Đặc điểm:
 * - OTP lưu trong bộ nhớ (ConcurrentHashMap), hết hạn sau 5 phút.
 * - Mỗi hành động có không gian OTP riêng (key = hành động + email),
 *   nên OTP đổi email và đổi SĐT không ghi đè lên nhau.
 * - Gửi lại mã: phải chờ 60 giây, tối đa 1 lần.
 * - Nhập sai quá 5 lần: hủy OTP.
 */
@Service
public class OtpService {

    public static final String ACTION_DOI_EMAIL = "DOI_EMAIL";
    public static final String ACTION_DOI_SDT = "DOI_SDT";
    public static final String ACTION_QUEN_MAT_KHAU = "QUEN_MAT_KHAU";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${otp.expiry.ms:300000}")
    private long otpExpiryMs;

    @Value("${otp.resend.cooldown.ms:60000}")
    private long resendCooldownMs;

    @Value("${otp.resend.max:1}")
    private int maxResend;

    @Value("${otp.max.fail:5}")
    private int maxFail;

    // Key = hành động + email, Value = thông tin OTP
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Tạo OTP, gửi về email và lưu lại.
     *
     * @param email        email nhận OTP (email cũ của tài khoản)
     * @param action       hành động (xem các hằng ACTION_*)
     * @param subject      tiêu đề email
     * @param actionLabel  mô tả hành động để đưa vào nội dung mail
     * @return thông tin cooldown / số lần gửi lại / thời hạn
     */
    public synchronized Map<String, Object> guiOtp(String email, String action,
                                                   String subject, String actionLabel) {
        return guiOtpInternal(email, action, subject, actionLabel, true);
    }

    /**
     * Gửi OTP không áp dụng cooldown / giới hạn gửi lại.
     * Dùng cho luồng đổi SĐT hiện tại để không thay đổi hành vi cũ.
     */
    public synchronized Map<String, Object> guiOtpKhongGioiHan(String email, String action,
                                                               String subject, String actionLabel) {
        return guiOtpInternal(email, action, subject, actionLabel, false);
    }

    private Map<String, Object> guiOtpInternal(String email, String action,
                                               String subject, String actionLabel,
                                               boolean apDungGioiHanGuiLai) {
        String key = taoKey(email, action);
        long now = System.currentTimeMillis();

        OtpEntry cu = otpStore.get(key);
        if (cu != null && now > cu.expiresAt()) {
            otpStore.remove(key);
            cu = null;
        }

        boolean laGuiLai = cu != null;
        if (laGuiLai && apDungGioiHanGuiLai) {
            if (cu.resendCount() >= maxResend) {
                throw new BadRequestException(
                        "Bạn đã dùng hết lượt gửi lại mã. Vui lòng thử lại sau.");
            }
            long conLaiMs = cu.lastSentAt() + resendCooldownMs - now;
            if (conLaiMs > 0) {
                long conLaiGiay = (conLaiMs + 999) / 1000;
                throw new BadRequestException(
                        "Vui lòng đợi " + conLaiGiay + " giây nữa mới được gửi lại mã.");
            }
        }

        String otp = generateOtp();
        try {
            guiMail(email, subject, actionLabel, otp);
        } catch (Exception e) {
            throw new BadRequestException(
                    "Không gửi được email OTP. Vui lòng kiểm tra kết nối và thử lại.");
        }

        int resendCount = laGuiLai ? cu.resendCount() + 1 : 0;
        otpStore.put(key, new OtpEntry(otp, now + otpExpiryMs, now, resendCount, 0));

        Map<String, Object> ketQua = new LinkedHashMap<>();
        ketQua.put("email", email);
        ketQua.put("cooldownGiay", apDungGioiHanGuiLai ? resendCooldownMs / 1000 : 0);
        ketQua.put("soLanGuiLaiConLai", apDungGioiHanGuiLai
                ? Math.max(maxResend - resendCount, 0)
                : Integer.MAX_VALUE);
        ketQua.put("hetHanGiay", otpExpiryMs / 1000);
        ketQua.put("laGuiLai", laGuiLai);
        return ketQua;
    }

    /**
     * Kiểm tra OTP có hợp lệ không (đúng mã và chưa hết hạn).
     * OTP đúng sẽ bị xóa ngay (dùng một lần).
     */
    public synchronized boolean verifyOtp(String email, String action, String otp) {
        String key = taoKey(email, action);
        OtpEntry entry = otpStore.get(key);
        if (entry == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (now > entry.expiresAt()) {
            otpStore.remove(key);
            return false;
        }

        String otpNhap = otp == null ? "" : otp.trim();
        if (!entry.otp().equals(otpNhap)) {
            int soLanSai = entry.failCount() + 1;
            if (soLanSai >= maxFail) {
                otpStore.remove(key);
            } else {
                otpStore.put(key, new OtpEntry(entry.otp(), entry.expiresAt(),
                        entry.lastSentAt(), entry.resendCount(), soLanSai));
            }
            return false;
        }

        otpStore.remove(key);
        return true;
    }

    /** Hủy OTP đang chờ của một hành động. */
    public synchronized void huyOtp(String email, String action) {
        if (email != null) {
            otpStore.remove(taoKey(email, action));
        }
    }

    private String taoKey(String email, String action) {
        return action + ":" + email.trim().toLowerCase(Locale.ROOT);
    }

    private void guiMail(String email, String subject, String actionLabel, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(
                "Xin chào,\n\n"
                        + "Mã OTP để " + actionLabel + " của bạn là: " + otp + "\n\n"
                        + "Mã có hiệu lực trong " + (otpExpiryMs / 60000)
                        + " phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n"
                        + "Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.\n\n"
                        + "Trân trọng,\nHệ thống Quản lý Thư viện"
        );
        mailSender.send(message);
    }

    /** Tạo mã OTP 6 chữ số ngẫu nhiên (có thể có số 0 đứng đầu). */
    private String generateOtp() {
        return String.format("%06d", random.nextInt(1_000_000));
    }

    /** Record lưu trữ OTP, thời điểm hết hạn, lần gửi cuối và số lần sai. */
    private record OtpEntry(String otp, long expiresAt, long lastSentAt,
                            int resendCount, int failCount) {
    }
}
