package com.example.backend.service;

import com.example.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Đổi email theo quy trình 2 lớp OTP:
 *   Bước 1: gửi OTP về email hiện tại (xác nhận danh tính)
 *   Bước 2: gửi OTP về email mới (xác nhận sở hữu email mới)
 *   Bước 3: nhập đúng OTP mới thì mới cập nhật email.
 *
 * Trạng thái tạm lưu trong RAM, không đổi schema DB.
 */
@Service
public class DoiEmailService {

    public static final String BUOC_EMAIL_CU = "XAC_NHAN_EMAIL_CU";
    public static final String BUOC_EMAIL_MOI = "XAC_NHAN_EMAIL_MOI";

    private final OtpService otpService;
    private final NguoiDungService nguoiDungService;
    private final EmailService emailService;

    @Value("${doi-email.pending.ttl.ms:600000}")
    private long pendingTtlMs;

    /** Key = người dùng, Value = phiên đổi email đang chờ. */
    private final Map<Integer, PendingDoiEmail> pendingByUser = new ConcurrentHashMap<>();

    public DoiEmailService(OtpService otpService,
                           NguoiDungService nguoiDungService,
                           EmailService emailService) {
        this.otpService = otpService;
        this.nguoiDungService = nguoiDungService;
        this.emailService = emailService;
    }

    /**
     * Bắt đầu đổi email: kiểm tra email mới và gửi OTP về email hiện tại.
     */
    public synchronized Map<String, Object> batDau(Integer nguoiDungId, String emailMoiRaw) {
        String emailMoi = emailMoiRaw == null ? "" : emailMoiRaw.trim();
        String emailCu = nguoiDungService.getEmailHienTai(nguoiDungId);

        // Kiểm tra trước khi gửi mail: định dạng đã được validate ở DTO,
        // ở đây kiểm tra trùng email cũ / email người khác.
        nguoiDungService.kiemTraEmailMoi(nguoiDungId, emailMoi);

        // Hủy phiên cũ và OTP của email mới cũ (nếu có) để không dính cooldown của lượt trước
        PendingDoiEmail cu = pendingByUser.remove(nguoiDungId);
        if (cu != null && cu.emailMoi != null) {
            otpService.huyOtp(cu.emailMoi, actionEmailMoi(nguoiDungId));
        }
        // Luôn xóa OTP cũ của email đích, kể cả khi phiên cũ đã hết hạn và bị xóa khỏi map
        otpService.huyOtp(emailMoi, actionEmailMoi(nguoiDungId));

        Map<String, Object> otpData = otpService.guiOtp(
                emailCu,
                actionEmailCu(nguoiDungId),
                "[Thư viện] Xác nhận đổi email - Bước 1/2",
                "xác nhận đổi email"
        );

        PendingDoiEmail pending = new PendingDoiEmail();
        pending.emailCu = emailCu;
        pending.emailMoi = emailMoi;
        pending.buoc = BUOC_EMAIL_CU;
        pending.hetHanLuc = System.currentTimeMillis() + pendingTtlMs;
        pendingByUser.put(nguoiDungId, pending);

        return taoPhanHoi(pending, otpData,
                "Đã gửi mã OTP tới email hiện tại: " + emailCu
                        + ". Nhập mã để xác nhận danh tính.");
    }

    /**
     * Xác nhận OTP email cũ. Nếu đúng, gửi OTP sang email mới.
     */
    public synchronized Map<String, Object> xacNhanEmailCu(Integer nguoiDungId, String otp) {
        PendingDoiEmail pending = layPending(nguoiDungId);
        if (!BUOC_EMAIL_CU.equals(pending.buoc)) {
            throw new BadRequestException("Bạn đã xác nhận email hiện tại rồi. Hãy nhập mã gửi tới email mới.");
        }

        // Kiểm tra email mới trước khi tiêu OTP
        nguoiDungService.kiemTraEmailMoi(nguoiDungId, pending.emailMoi);

        if (!otpService.verifyOtp(pending.emailCu, actionEmailCu(nguoiDungId), otp)) {
            throw new BadRequestException("Mã OTP email hiện tại không đúng hoặc đã hết hạn.");
        }

        Map<String, Object> otpData;
        try {
            otpData = otpService.guiOtp(
                    pending.emailMoi,
                    actionEmailMoi(nguoiDungId),
                    "[Thư viện] Xác nhận địa chỉ email mới",
                    "xác nhận địa chỉ email mới"
            );
        } catch (Exception e) {
            // OTP cũ đã dùng, giữ nguyên bước 1 để người dùng bấm "Gửi lại"
            throw new BadRequestException("Không gửi được mã tới email mới. " + e.getMessage());
        }

        pending.buoc = BUOC_EMAIL_MOI;
        pending.hetHanLuc = System.currentTimeMillis() + pendingTtlMs;

        return taoPhanHoi(pending, otpData,
                "Xác nhận email hiện tại thành công. Đã gửi mã OTP tới email mới: "
                        + pending.emailMoi + ".");
    }

    /**
     * Gửi lại OTP của bước hiện tại (cooldown 60s, tối đa 1 lần).
     */
    public synchronized Map<String, Object> guiLaiOtp(Integer nguoiDungId) {
        PendingDoiEmail pending = layPending(nguoiDungId);

        String emailNhan = BUOC_EMAIL_CU.equals(pending.buoc)
                ? pending.emailCu : pending.emailMoi;
        String action = BUOC_EMAIL_CU.equals(pending.buoc)
                ? actionEmailCu(nguoiDungId) : actionEmailMoi(nguoiDungId);
        String subject = BUOC_EMAIL_CU.equals(pending.buoc)
                ? "[Thư viện] Xác nhận đổi email - Bước 1/2"
                : "[Thư viện] Xác nhận địa chỉ email mới";

        Map<String, Object> otpData = otpService.guiOtp(
                emailNhan, action, subject,
                BUOC_EMAIL_CU.equals(pending.buoc)
                        ? "xác nhận đổi email" : "xác nhận địa chỉ email mới");

        pending.hetHanLuc = System.currentTimeMillis() + pendingTtlMs;

        return taoPhanHoi(pending, otpData,
                "Đã gửi lại mã OTP tới email: " + emailNhan + ".");
    }

    /**
     * Xác nhận OTP email mới và cập nhật email.
     */
    public synchronized Map<String, Object> xacNhanEmailMoi(Integer nguoiDungId, String otp) {
        PendingDoiEmail pending = layPending(nguoiDungId);
        if (!BUOC_EMAIL_MOI.equals(pending.buoc)) {
            throw new BadRequestException("Bạn chưa xác nhận email hiện tại. Vui lòng làm lại từ đầu.");
        }

        // Kiểm tra lại trước khi tiêu OTP (email mới có thể đã bị người khác dùng)
        nguoiDungService.kiemTraEmailMoi(nguoiDungId, pending.emailMoi);

        if (!otpService.verifyOtp(pending.emailMoi, actionEmailMoi(nguoiDungId), otp)) {
            throw new BadRequestException("Mã OTP email mới không đúng hoặc đã hết hạn.");
        }

        nguoiDungService.capNhatEmail(nguoiDungId, pending.emailMoi);

        pendingByUser.remove(nguoiDungId);
        otpService.huyOtp(pending.emailCu, actionEmailCu(nguoiDungId));
        otpService.huyOtp(pending.emailMoi, actionEmailMoi(nguoiDungId));

        guiMailThongBaoEmailCu(pending);

        Map<String, Object> ketQua = new LinkedHashMap<>();
        ketQua.put("message", "Đổi email thành công! Email mới: " + pending.emailMoi);
        ketQua.put("emailMoi", pending.emailMoi);
        return ketQua;
    }

    private PendingDoiEmail layPending(Integer nguoiDungId) {
        PendingDoiEmail pending = pendingByUser.get(nguoiDungId);
        if (pending == null || System.currentTimeMillis() > pending.hetHanLuc) {
            pendingByUser.remove(nguoiDungId);
            throw new BadRequestException("Phiên đổi email đã hết hạn. Vui lòng bắt đầu lại.");
        }
        return pending;
    }

    private Map<String, Object> taoPhanHoi(PendingDoiEmail pending,
                                           Map<String, Object> otpData,
                                           String message) {
        Map<String, Object> ketQua = new LinkedHashMap<>(otpData);
        ketQua.put("buoc", pending.buoc);
        ketQua.put("emailCu", pending.emailCu);
        ketQua.put("emailMoi", pending.emailMoi);
        ketQua.put("emailNhanOtp",
                BUOC_EMAIL_CU.equals(pending.buoc) ? pending.emailCu : pending.emailMoi);
        ketQua.put("message", message);
        return ketQua;
    }

    private void guiMailThongBaoEmailCu(PendingDoiEmail pending) {
        try {
            emailService.guiEmail(
                    pending.emailCu,
                    "[Thư viện] Email tài khoản đã được thay đổi",
                    "Xin chào,\n\n"
                            + "Email đăng nhập của tài khoản thư viện vừa được đổi thành: "
                            + pending.emailMoi + "\n\n"
                            + "Nếu không phải bạn thực hiện, vui lòng liên hệ quản trị viên ngay.\n\n"
                            + "Trân trọng,\nHệ thống Quản lý Thư viện"
            );
        } catch (Exception ignored) {
            // Đổi email đã thành công, không để lỗi gửi thông báo làm hỏng kết quả
        }
    }

    private String actionEmailCu(Integer nguoiDungId) {
        return OtpService.ACTION_DOI_EMAIL + "_CU#" + nguoiDungId;
    }

    private String actionEmailMoi(Integer nguoiDungId) {
        return OtpService.ACTION_DOI_EMAIL + "_MOI#" + nguoiDungId;
    }

    /** Phiên đổi email đang chờ xác thực. */
    private static class PendingDoiEmail {
        private String emailCu;
        private String emailMoi;
        private String buoc;
        private long hetHanLuc;
    }
}
